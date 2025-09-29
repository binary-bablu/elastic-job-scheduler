package com.scheduler.helios.job;

import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.scheduler.helios.dto.JobExecutionRequest;
import com.scheduler.helios.entity.JobExecution;
import com.scheduler.helios.repository.JobExecutionsRepository;
import com.scheduler.helios.service.JobQueueService;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
@DisallowConcurrentExecution
public class QueuedShellScriptJob implements Job {

private static final Logger logger = LoggerFactory.getLogger(QueuedShellScriptJob.class);
    
    @Autowired
    private JobQueueService jobQueueService;
    
    @Autowired
    private JobExecutionsRepository jobExecInfoRepository;
    
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
    	
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        String jobKey = context.getJobDetail().getKey().toString();
        
        logger.info("Quartz triggered job: {} - Publishing to execution queue", jobKey);
        
        try {
            // Create execution request
            JobExecutionRequest request = new JobExecutionRequest();
            request.setJobId((Integer) dataMap.get("jobId"));
            request.setJobName(context.getJobDetail().getKey().getName());
            request.setJobGroup(context.getJobDetail().getKey().getGroup());
            request.setScriptPath(dataMap.getString("scriptPath"));
            request.setQueuedTime(LocalDateTime.now());
            
            // Extract parameters
            Map<String, String> parameters = new HashMap<>();
            for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
                if (!isInternalKey(entry.getKey())) {
                    parameters.put(entry.getKey(), String.valueOf(entry.getValue()));
                }
            }
            request.setParameters(parameters);
            
            // Extract timeout and retry settings
            request.setTimeoutSeconds(getIntValue(dataMap, "timeoutSeconds", 300));
            request.setMaxRetries(getIntValue(dataMap, "maxRetries", 3));
            
            JobExecution jobExecInfo = jobExecInfoRepository.save(createJobExecInfoEntity(request,"QUEUED"));
            request.setExecutionId(jobExecInfo.getExecutionId());
          
            // Publish to execution queue
            jobQueueService.publishJobExecution(request);
            
            logger.info("Successfully published job to queue: {} with executionId: {}", 
                       jobKey, request.getExecutionId());
            
        } catch (Exception e) {
            logger.error("Failed to publish job to queue: {}", jobKey, e);
            throw new JobExecutionException("Failed to publish job to execution queue", e);
        }
    }
        
    private boolean isInternalKey(String key) {
        return key.equals("scriptPath") || key.equals("jobId") || 
               key.equals("timeoutSeconds") || key.equals("maxRetries");
    }
    
    private int getIntValue(JobDataMap dataMap, String key, int defaultValue) {
        Object value = dataMap.get(key);
        if (value instanceof Integer) {
            return (Integer) value;
        } else if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                logger.warn("Invalid {} value: {}, using default: {}", key, value, defaultValue);
            }
        }
        return defaultValue;
    }
    
    private JobExecution createJobExecInfoEntity(JobExecutionRequest jobExecRequest,String status) {
    	
    	JobExecution jobExecInfo = new JobExecution();
    	jobExecInfo.setJobId(jobExecRequest.getJobId());
    	jobExecInfo.setStatus(status);
    	jobExecInfo.setQueuedStartTime(Timestamp.valueOf(jobExecRequest.getQueuedTime()));
    	
    	return jobExecInfo;
    }
	
}
