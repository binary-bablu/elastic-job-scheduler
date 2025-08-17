package com.scheduler.executor.service;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.scheduler.executor.entity.JobExecHeartBeatInfoEntity;
import com.scheduler.executor.repository.JobExecHeartBeatInfoRepository;

import jakarta.annotation.PostConstruct;

@Service
public class AgentHealthService {
	
private static final Logger logger = LoggerFactory.getLogger(AgentHealthService.class);
    
    @Value("${executor.agent.id}")
    private String agentId;
    
    @Value("${executor.agent.heartbeat-interval:30000}")
    private long heartbeatInterval;
    
    @Value("${executor.agent.max-concurrent-jobs:10}")
    private int maxConcurrentJobs;
    
    private int currentActiveJobs = 0;
    
    @Autowired
    private JobExecHeartBeatInfoRepository jobExecHeartBeatInfoRepository;
    
    @PostConstruct
    public void registerAgent() {
      
    	logger.info("Registering executor agent: {}", agentId);
        updateHeartbeat();
    }
    
    @Scheduled(fixedDelayString = "${executor.agent.heartbeat-interval:30000}")
    public void sendHeartbeat() {
        
    	updateHeartbeat();
    }
    
    private void updateHeartbeat() {
        
    	try {
        
        	String agentKey = "agent:" + agentId;
            
            JobExecHeartBeatInfoEntity jobExecHeartBeatInfoEntity = createJobExecHeartBeatentity(agentKey);
            jobExecHeartBeatInfoRepository.save(jobExecHeartBeatInfoEntity);
            
            logger.debug("Heartbeat sent for agent: {} - Active jobs: {}/{}", 
                        agentId, currentActiveJobs, maxConcurrentJobs);
                        
        } catch (Exception e) {
            logger.error("Failed to send heartbeat for agent: {}", agentId, e);
        }
    }
    
    private JobExecHeartBeatInfoEntity createJobExecHeartBeatentity(String agentId ) {
    	
    	JobExecHeartBeatInfoEntity jobExecHeartBeatInfoEntity = new JobExecHeartBeatInfoEntity();
    	jobExecHeartBeatInfoEntity.setStatus("ACTIVE");
    	jobExecHeartBeatInfoEntity.setLastHeartBeat(LocalDateTime.now().toString());
    	jobExecHeartBeatInfoEntity.setMaxConcurrentJobs(maxConcurrentJobs);
    	jobExecHeartBeatInfoEntity.setCurrentActiveJobs(currentActiveJobs);
    	jobExecHeartBeatInfoEntity.setAgentId(agentId);
    	
    	return jobExecHeartBeatInfoEntity;
    }
    
    public void incrementActiveJobs() {
       
    	currentActiveJobs++;
        updateHeartbeat();
    }
    
    public void decrementActiveJobs() {
        
    	if (currentActiveJobs > 0) {
            currentActiveJobs--;
        }
        updateHeartbeat();
    }
    
    public boolean canAcceptMoreJobs() {
        return currentActiveJobs < maxConcurrentJobs;
    }

	public String getAgentId() {
		return agentId;
	}

	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}

	public int getMaxConcurrentJobs() {
		return maxConcurrentJobs;
	}

	public void setMaxConcurrentJobs(int maxConcurrentJobs) {
		this.maxConcurrentJobs = maxConcurrentJobs;
	}

	public int getCurrentActiveJobs() {
		return currentActiveJobs;
	}

	public void setCurrentActiveJobs(int currentActiveJobs) {
		this.currentActiveJobs = currentActiveJobs;
	}
    
}
