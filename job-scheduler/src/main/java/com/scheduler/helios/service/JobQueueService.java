package com.scheduler.helios.service;


import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.scheduler.helios.config.RabbitMQConfig;
import com.scheduler.helios.dto.JobExecutionRequest;
import com.scheduler.helios.entity.JobExecInfo;
import com.scheduler.helios.repository.JobExecInfoRepository;

@Service
public class JobQueueService {

    private static final Logger logger = LoggerFactory.getLogger(JobQueueService.class);
    
	@Autowired
	private JobExecInfoRepository jobExecInfoRepository;

	@Autowired
    private RabbitTemplate rabbitTemplate;
    
    public void publishJobExecution(JobExecutionRequest request) {
       
    	try {
    		
    		JobExecInfo jobExecInfo = new JobExecInfo();
    		jobExecInfo.setJobId(request.getJobId());
    		jobExecInfo.setStatus("QUEUED");
    		
            // Publish to execution queue
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.JOB_EXCHANGE,
                RabbitMQConfig.JOB_EXECUTION_ROUTING_KEY,
                request
            );
            
            jobExecInfoRepository.save(jobExecInfo);
            
            logger.info("Published job execution request: {} for job: {}", 
            		request.getJobId(),jobExecInfo.getExecutionId());
                       
        } catch (Exception e) {
            logger.error("Failed to publish job execution request: {}", request.getExecutionId(), e);
            throw new RuntimeException("Failed to publish job execution", e);
        }
    }
    
    public void updateJobExecutionStatus(Integer executionId, String status) {
       
        JobExecInfo jobExecInfo = new JobExecInfo();
        jobExecInfo.setExecutionId(executionId);
        jobExecInfo.setStatus(status);
        
        jobExecInfoRepository.save(jobExecInfo);
        
    }
    
    public String getJobExecutionStatus(Integer executionId) {
      
        Optional<JobExecInfo> optionalJobExecInfo = jobExecInfoRepository.findById(executionId);
        JobExecInfo jobExecInfo = optionalJobExecInfo.get();
        
        return jobExecInfo.getStatus();
    }

}
