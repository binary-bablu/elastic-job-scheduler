package com.scheduler.helios.listerner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.scheduler.helios.config.RabbitMQConfig;
import com.scheduler.helios.dto.JobExecutionResult;

@Component
public class JobResultListener {

private static final Logger logger = LoggerFactory.getLogger(JobResultListener.class);
    
    @RabbitListener(queues = RabbitMQConfig.JOB_RESULT_QUEUE)
    public void handleJobResult(JobExecutionResult result) {
    	
        logger.info("Received job execution result: {} - Success: {}", 
                   result.getExecutionId(), result.isSuccess());
        
        try {
            // Update execution status
            //String status = result.isSuccess() ? "COMPLETED" : "FAILED";
            //jobQueueService.updateJobExecutionStatus(result,status);
            
            // Here you could:
            // 1. Update job statistics in database
            // 2. Send notifications for failures
            // 3. Trigger dependent jobs
            // 4. Update monitoring dashboards
            
            if (!result.isSuccess()) {
                logger.error("Job execution failed: {} - Error: {}", 
                           result.getExecutionId(), result.getErrorMessage());
                
                // Could implement alerting logic here
                handleJobFailure(result);
            } else {
                logger.info("Job execution completed successfully: {} in {}ms", 
                           result.getExecutionId(), result.getDurationMs());
            }
            
        } catch (Exception e) {
            logger.error("Failed to process job result: {}", result.getExecutionId(), e);
        }
    }
    
    private void handleJobFailure(JobExecutionResult result) {
        // Implement failure handling logic:
        // - Send alerts
        // - Log to metrics
        // - Trigger retries if needed
        logger.warn("Handling job failure for: {} - Exit code: {}", 
                   result.getExecutionId(), result.getExitCode());
    }
	
}
