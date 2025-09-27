package com.scheduler.executor.listener;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.rabbitmq.client.Channel;
import com.scheduler.executor.config.RabbitMQConfig;
import com.scheduler.executor.dto.JobExecutionRequest;
import com.scheduler.executor.dto.JobExecutionResult;
import com.scheduler.executor.entity.JobExecInfo;
import com.scheduler.executor.repository.JobExecInfoRepository;
import com.scheduler.executor.service.AgentHealthService;
import com.scheduler.executor.service.JobExecutorService;
import com.scheduler.executor.service.JobExecutorStatusService;
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
	
	@Autowired
    private JobExecutorStatusService jobExecutorStatusService;
	
	@Autowired
    private JobExecInfoRepository jobExecInfoRepository;
	
	@RabbitListener(queues = RabbitMQConfig.JOB_EXECUTION_QUEUE, concurrency = "#{@agentHealthService.maxConcurrentJobs}")
	public void handleJobExecution(JobExecutionRequest request, Channel channel,
	                             @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
	    
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
	    	
	    	jobExecutorStatusService.createJobExecutionEntry(createJobExecInfoEntity(request,"EXECUTING"));
        	
	        // Execute the job
	        result = jobExecutorService.executeJob(request);
	        
	    } catch (Exception e) {
	    	
	        logger.error("Error processing job: {}", request.getExecutionId(), e);
	        channel.basicAck(deliveryTag, false);
	        result = createFailureResult(request, e);
	        
	    } finally {
	        agentHealthService.decrementActiveJobs();
	        jobExecutorStatusService.updateJobExecutionEntry(updateJobExecInfoEntity(result));
	        // Publish result back to scheduler
	        jobResultPublisher.publishResult(result);
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
	    result.setErrorMessage(e.getMessage());
	    result.setExitCode(-998);
	    return result;
	}
	
	 private JobExecInfo createJobExecInfoEntity(JobExecutionRequest jobExecRequest,String status) {
	    	
	 	LocalDateTime startTime = LocalDateTime.now();
	 	jobExecRequest.setStartTime(startTime);
    	JobExecInfo jobExecInfo = jobExecInfoRepository.
    			findByJobIdAndExecutionId(jobExecRequest.getJobId(),jobExecRequest.getExecutionId());
    	
    	jobExecInfo.setStatus(status);
    	jobExecInfo.setExecStartTime(Timestamp.valueOf(startTime));
    	jobExecInfo.setQueuedEndTime(Timestamp.valueOf(startTime));
    	
    	return jobExecInfo;
	 }
	    
	 private JobExecInfo updateJobExecInfoEntity(JobExecutionResult result) {
    	 String status = result.isSuccess() ? "COMPLETED" : "FAILED";
         JobExecInfo jobExecInfo = jobExecInfoRepository.findByJobIdAndExecutionId(result.getJobId(),result.getExecutionId());
         jobExecInfo.setStatus(status);
         jobExecInfo.setExecStartTime(Timestamp.valueOf(result.getStartTime()));
         jobExecInfo.setExecEndTime(Timestamp.valueOf(result.getEndTime()));
         
         return jobExecInfo;
	 }

}
