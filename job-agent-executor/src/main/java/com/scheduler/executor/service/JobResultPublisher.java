package com.scheduler.executor.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.scheduler.executor.config.RabbitMQConfig;
import com.scheduler.executor.dto.JobExecutionResult;

@Service
public class JobResultPublisher {
	
private static final Logger logger = LoggerFactory.getLogger(JobResultPublisher.class);
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    public void publishResult(JobExecutionResult result) {
        try {
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.JOB_EXCHANGE,
                RabbitMQConfig.JOB_RESULT_ROUTING_KEY,
                result
            );
            
            logger.info("Published job result: {} - Success: {}", 
                       result.getExecutionId(), result.isSuccess());
                       
        } catch (Exception e) {
            logger.error("Failed to publish job result: {}", result.getExecutionId(), e);
            throw new RuntimeException("Failed to publish job result", e);
        }
    }

}
