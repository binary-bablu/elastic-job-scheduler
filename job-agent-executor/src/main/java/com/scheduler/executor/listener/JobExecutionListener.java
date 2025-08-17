package com.scheduler.executor.listener;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.scheduler.executor.config.RabbitMQConfig;
import com.scheduler.executor.dto.JobExecutionRequest;
import com.scheduler.executor.dto.JobExecutionResult;
import com.scheduler.executor.service.AgentHealthService;
import com.scheduler.executor.service.JobExecutorService;
import com.scheduler.executor.service.JobResultPublisher;

@Component
public class JobExecutionListener {
	
	private static final Logger logger = LoggerFactory.getLogger(JobExecutionListener.class);
	    
	@Autowired
	private JobExecutorService jobExecutorService;
	
	@Autowired
	private JobResultPublisher jobResultPublisher;
	
	@Autowired
	private AgentHealthService agentHealthService;
	
	@RabbitListener(queues = RabbitMQConfig.JOB_EXECUTION_QUEUE, concurrency = "#{@agentHealthService.maxConcurrentJobs}")
	public void handleJobExecution(JobExecutionRequest request, 
	                             @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
	    
	    // Check if agent can accept more jobs
	    if (!agentHealthService.canAcceptMoreJobs()) {
	        logger.warn("Agent at capacity, rejecting job: {}", request.getExecutionId());
	        // Message will be re-queued automatically
	        throw new RuntimeException("Agent at capacity");
	    }
	    
	    agentHealthService.incrementActiveJobs();
	    
	    logger.info("Processing job: {} on agent with delivery tag: {}", 
	               request.getExecutionId(), deliveryTag);
	    
	    JobExecutionResult result = null;
	    try {
	        // Execute the job
	        result = jobExecutorService.executeJob(request);
	        
	        // Publish result back to scheduler
	        jobResultPublisher.publishResult(result);
	        
	    } catch (Exception e) {
	        logger.error("Error processing job: {}", request.getExecutionId(), e);
	        
	        // Handle retry logic
	        if (request.getRetryCount() < request.getMaxRetries()) {
	            request.setRetryCount(request.getRetryCount() + 1);
	            logger.info("Retrying job: {} - Attempt {}/{}", 
	                       request.getExecutionId(), request.getRetryCount(), request.getMaxRetries());
	            
	            // Could publish to retry queue with delay
	            publishToRetryQueue(request);
	        } else {
	            // Max retries exceeded, send failure result
	            result = createFailureResult(request, e);
	            jobResultPublisher.publishResult(result);
	        }
	        
	    } finally {
	        agentHealthService.decrementActiveJobs();
	    }
	}
	
	private void publishToRetryQueue(JobExecutionRequest request) {
	    // Implementation would publish to retry queue
	    logger.info("Publishing job to retry queue: {}", request.getExecutionId());
	}
	
	private JobExecutionResult createFailureResult(JobExecutionRequest request, Exception e) {
	    JobExecutionResult result = new JobExecutionResult(
	        request.getExecutionId(), 
	        request.getJobId(), 
	        agentHealthService.getAgentId()
	    );
	    result.setSuccess(false);
	    result.setErrorMessage("Max retries exceeded: " + e.getMessage());
	    result.setExitCode(-998);
	    return result;
	}

}
