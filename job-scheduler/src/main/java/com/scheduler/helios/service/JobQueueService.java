package com.scheduler.helios.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.scheduler.helios.config.RabbitMQConfig;
import com.scheduler.helios.dto.JobExecutionRequest;

@Service
public class JobQueueService {

    private static final Logger logger = LoggerFactory.getLogger(JobQueueService.class);
    
	@Autowired
    private RabbitTemplate rabbitTemplate;
    
    public void publishJobExecution(JobExecutionRequest request) {
       
    	try {
    		
            // Publish to execution queue
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.JOB_EXCHANGE,
                RabbitMQConfig.JOB_EXECUTION_ROUTING_KEY,
                request
            );
            
            logger.info("Published job execution request: {} for job: {}", 
            		request.getExecutionId(),request.getJobId());
                       
        } catch (Exception e) {
            logger.error("Failed to publish job execution request: {}", request.getExecutionId(), e);
            throw new RuntimeException("Failed to publish job execution", e);
        }
    }

}
