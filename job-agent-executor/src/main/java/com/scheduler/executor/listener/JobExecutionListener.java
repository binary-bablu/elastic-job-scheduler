package com.scheduler.executor.listener;

import java.io.IOException;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.rabbitmq.client.Channel;
import com.scheduler.executor.dto.JobExecutionRequest;
import com.scheduler.executor.dto.JobExecutionResult;
import com.scheduler.executor.service.JobExecutorService;
import com.scheduler.executor.service.JobExecutorStatusService;
import com.scheduler.executor.service.JobResultPublisher;
import com.scheduler.executor.service.JobRetryService;

@Component
public class JobExecutionListener {
	
	private static final Logger logger = LoggerFactory.getLogger(JobExecutionListener.class);
	
	@Value("${executor.agent.id}")
	private String agentId;
	    
	@Autowired
	private JobExecutorService jobExecutorService;
	
	@Autowired
	private JobResultPublisher jobResultPublisher;
	
	@Autowired
    private JobExecutorStatusService jobExecutorStatusService;
	
	@Autowired
    private JobRetryService jobRetryService;
	
	@RabbitListener(queues = "${agent.execution-queue-name}")
	public void handleJobExecution(JobExecutionRequest request, Channel channel,
	                             @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
		
		// Log job type and retry configuration
        if (request.getCurrentRetryCount() > 0) {
            logger.info("Processing RETRY job: {} with execution id {} (attempt {}/{}) - Strategy: {}, Next delay would be: {}ms", 
            		request.getJobId(),   
            		request.getExecutionId(), 
                    request.getCurrentRetryCount(), 
                    request.getMaxRetryAttempts(),
                    request.getBackOffStrategy(),
                    request.calculateDelay(request.getCurrentRetryCount() + 1));
        } else {
            logger.info("Processing NEW job: {} with execution id {} - Retry config: maxAttempts={}, strategy={}, initialDelay={}ms", 
            		request.getJobId(),
            		request.getExecutionId(),
            		request.getMaxRetryAttempts(),
            		request.getBackOffStrategy(),
            		request.calculateDelay(request.getCurrentRetryCount()));
        }
	    
	    logger.info("Processing job: {} with execution id {} on agent with delivery tag: {}", 
	    		request.getJobId(),request.getExecutionId(), deliveryTag);
	    
	    JobExecutionResult result = null;
	    try {
	    	request.setStartTime(LocalDateTime.now());
	    	jobExecutorStatusService.updateJobExecutionEntry(request,"RUNNING");
        	
	        // Execute the job
	        result = jobExecutorService.executeJob(request);
	        
	        // Handle result
            if (result.isSuccess()) {
                logger.info("Job {} with execution id {} executed successfully:  after {} attempt(s)", 
                		request.getJobId(),   
                		request.getExecutionId(),
                        request.getCurrentRetryCount());
            } 
	        
	    } catch (Exception e) {
	    	
	        logger.error("Error processing job {} execution id : {}", request.getJobId(),request.getExecutionId(), e);
	       
	        //Remove message
	        channel.basicAck(deliveryTag, false);
	        result = createFailureResult(request, e);
	        
	    } finally {

	    	jobExecutorStatusService.updateJobExecutionEntry(result);
	        
	        // Publish result back to scheduler
	        jobResultPublisher.publishResult(result);
	       
	        //handle job failure
	        if(!result.isSuccess()) {
	        	 handleJobFailure(request, result);
	        }
	    }
	}
	
	private JobExecutionResult createFailureResult(JobExecutionRequest request, Exception e) {
	    JobExecutionResult result = new JobExecutionResult(
	        request.getExecutionId(), 
	        request.getJobId(), 
	        agentId
	    );
	    result.setSuccess(false);
	    result.setErrorMessage(e.getMessage());
	    result.setExitCode(-998);
	    return result;
	}
	
	/**
     * Handles job failure with retry logic using job's configuration
     */
    private void handleJobFailure(JobExecutionRequest request, JobExecutionResult result) {
        
    	logger.warn("Job {} with execution id {} failed: {} - Exit code: {}, Reason: {}", 
    			request.getJobId(), request.getExecutionId(), result.getExitCode(), result.getErrorMessage());
        
        // Check if error is retryable using job's retry config
        if (!jobRetryService.isRetryableError(request, result)) {
            logger.info("Error is not retryable (exit code {}), sending to DLQ: {}", 
                       result.getExitCode(), request.getExecutionId());
            jobRetryService.publishToDLQ(request, result.getErrorMessage());
            
            // Publish final result
            result.setSuccess(false);
            result.setErrorMessage("Non-retryable error: " + result.getErrorMessage());
            jobResultPublisher.publishResult(result);
            return;
        }
        
        // Check if we can retry
        if (request.canRetry()) {
            long nextDelay = request.calculateDelay(request.getCurrentRetryCount() + 1);
            
            logger.info("Job {} with execution id {} will be retried (next attempt will be {}/{}) in {}ms [strategy={}]", 
                       request.getJobId(),
            		   request.getExecutionId(), 
                       request.getCurrentRetryCount() + 1, 
                       request.getMaxRetryAttempts(),
                       nextDelay,request.getBackOffStrategy());
            
            // Publish to retry queue
            jobRetryService.publishToRetryQueue(request, result.getErrorMessage());
            
            // Publish current failure result for monitoring
            result.setSuccess(false);
            result.setErrorMessage(String.format("Job failed (attempt %d/%d), retry scheduled: %s", 
                                                 request.getCurrentRetryCount(),
                                                 request.getMaxRetryAttempts(),
                                                 result.getErrorMessage()));
            jobResultPublisher.publishResult(result);
            
        } else {
            // Max retries exceeded
            logger.error("Job {} with exceution id {} permanently failed after {} attempts with strategy {}. Sending to DLQ", 
            		    request.getJobId(),
                        request.getExecutionId(), 
                        request.getCurrentRetryCount(),
                        request.getBackOffStrategy());
            
            // Send to DLQ
            jobRetryService.publishToDLQ(request, result.getErrorMessage());
            logger.error("Job {} with execution id {} sent to DLQ",request.getJobId(),request.getExecutionId());      
            
            // Publish final failure result
            result.setSuccess(false);
            result.setErrorMessage(String.format("Max retries (%d) exceeded: %s", 
                                                 request.getMaxRetryAttempts(),
                                                 result.getErrorMessage()));
            jobResultPublisher.publishResult(result);
        }
    }

}
