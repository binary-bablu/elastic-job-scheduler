package com.scheduler.executor.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.scheduler.executor.config.RabbitMQConfig;
import com.scheduler.executor.dto.JobExecutionRequest;
import com.scheduler.executor.dto.JobExecutionResult;
import com.scheduler.executor.entity.JobExecution;
import com.scheduler.executor.repository.JobExecutionsRepository;

@Service
public class JobRetryService {
	
private static final Logger logger = LoggerFactory.getLogger(JobRetryService.class);
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    @Autowired
    private MessageConverter messageConverter;
    
    @Value("${executor.agent.id:unknown}")
    private String agentId;
    
    @Autowired
    private JobExecutionsRepository jobExecInfoRepository;
    
    @Autowired
    private JobExecutorStatusService jobExecutorStatusService;
    /**
     * Publishes job to retry queue using job's own retry configuration
     */
    public void publishToRetryQueue(JobExecutionRequest request, String failureReason) {
        
        // Record retry attempt
        request.incrementRetryCount();
        request.recordRetryAttempt(failureReason, agentId);
        
        // Calculate delay using job's retry configuration
        long delayMs = request.calculateDelay(request.getCurrentRetryCount());
        
        try {
        	
        	JobExecution jobExecInfo = jobExecInfoRepository.save(createInitialJobExecInfoEntity(request,"RE_TRY_QUEUED",null));
            request.setExecutionId(jobExecInfo.getExecutionId());
            // Convert request to message
            Message message = messageConverter.toMessage(request, new MessageProperties());
            
            // Set per-message TTL
            message.getMessageProperties().setExpiration(String.valueOf(delayMs));
            
            // Send to retry queue
            rabbitTemplate.send(
                RabbitMQConfig.JOB_RETRY_EXCHANGE,
                RabbitMQConfig.JOB_RETRY_ROUTING_KEY,
                message
            );
            
            logger.info("Successfully queued job {} with execution id {} for retry #{} with {} delay", 
                    request.getJobId(),   
            		request.getExecutionId(), 
                    request.getCurrentRetryCount(),
                    formatDelay(delayMs));
                       
        } catch (Exception e) {
            logger.error("Failed to publish job to retry queue: {}", request.getExecutionId(), e);
            jobExecInfoRepository.save(createFailureJobExecEntity(request,"FAILED","Error Connecting to Re-Try Queue"));
            publishToDLQ(request, "Failed to publish to retry queue: " + e.getMessage());
        }
    }
    
    /**
     * Publishes permanently failed jobs to Dead Letter Queue
     */
    public void publishToDLQ(JobExecutionRequest request, String finalFailureReason) {
        logger.error("Job {} permanently failed after {} attempts. Reason: {}", 
                    request.getExecutionId(), request.getCurrentRetryCount(), finalFailureReason);
        
        request.setLastFailureReason(finalFailureReason);
        
        try {
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.JOB_DLQ_EXCHANGE,
                RabbitMQConfig.JOB_DLQ_ROUTING_KEY,
                request
            );
            
            logger.info("Job {} with execution id {} sent to DLQ", request.getJobId(),request.getExecutionId());
            jobExecutorStatusService.updateJobExecDLQSentStatus(request.getJobId(),request.getExecutionId());
            
        } catch (Exception e) {
            logger.error("CRITICAL: Failed to send job {} with execution id to DLQ: {}",request.getJobId(), request.getExecutionId(), e);
            jobExecInfoRepository.save(createFailureJobExecEntity(request,"FAILED","Error Connecting to DLQ Queue"));
        }
    }
    
    /**
     * Check if error is retryable using job's retry configuration
     */
    public boolean isRetryableError(JobExecutionRequest request, JobExecutionResult result) {
        int exitCode = result.getExitCode();
        
        // Use job's retry configuration to check if exit code is retryable
        boolean retryable = request.isExitCodeRetryable(exitCode);
        
        if (!retryable) {
            logger.info("Exit code {} is in job's non-retryable list", exitCode);
        }
        
        return retryable;
    }
    
    /**
     * Format delay for human-readable logging
     */
    private String formatDelay(long delayMs) {
        if (delayMs < 60000) {
            return (delayMs / 1000) + " seconds";
        } else if (delayMs < 3600000) {
            return (delayMs / 60000) + " minutes";
        } else {
            return (delayMs / 3600000) + " hours";
        }
    }
    
    private JobExecution createInitialJobExecInfoEntity(JobExecutionRequest jobExecRequest,String status,String errMessage) {
    	
    	JobExecution jobExecInfo = new JobExecution();
    	jobExecInfo.setJobId(jobExecRequest.getJobId());
    	jobExecInfo.setStatus(status);
    	jobExecInfo.setQueuedStartTime(Timestamp.valueOf(LocalDateTime.now()));
    	jobExecInfo.setRetryAttemptNumber(jobExecRequest.getCurrentRetryCount());
    	jobExecInfo.setIsRetryAttempt(jobExecRequest.getCurrentRetryCount()>0 ? "YES":"NO");
    	
    	return jobExecInfo;
    }
	
    private JobExecution createFailureJobExecEntity(JobExecutionRequest jobExecRequest,String status,String errMessage) {
    	
    	JobExecution jobExecInfo = new JobExecution();
    	jobExecInfo.setJobId(jobExecRequest.getJobId());
    	jobExecInfo.setStatus(status);
    	jobExecInfo.setErrorMessage(errMessage);
    	jobExecInfo.setExecEndTime(Timestamp.valueOf(LocalDateTime.now()));
    	return jobExecInfo;
    }
  
}
