package com.scheduler.helios.manager.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitMQConfig {
	
	// Queue Names
    public static final String JOB_EXECUTION_QUEUE = "job.execution.queue";
    public static final String JOB_RESULT_QUEUE = "job.result.queue";
    public static final String JOB_RETRY_QUEUE = "job.retry.queue";
    public static final String JOB_DLQ = "job.dlq";  // Dead Letter Queue
    
    // Exchange Names
    public static final String JOB_EXCHANGE = "job.exchange";
    public static final String JOB_RETRY_EXCHANGE = "job.retry.exchange";
    
    // Routing Keys
    public static final String JOB_EXECUTION_ROUTING_KEY = "job.execute";
    public static final String JOB_RESULT_ROUTING_KEY = "job.result";
    public static final String JOB_RETRY_ROUTING_KEY = "job.retry";
    
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        template.setConfirmCallback((correlationData, ack, cause) -> {
            if (!ack) {
                // Handle message not delivered to exchange
                System.err.println("Message not delivered to exchange: " + cause);
            }
        });
        template.setReturnsCallback(returned -> {
            // Handle message not routed to queue
            System.err.println("Message returned: " + returned.getReplyText());
        });
        return template;
    }
    
    // Main job exchange
    @Bean
    public DirectExchange jobExchange() {
        return new DirectExchange(JOB_EXCHANGE, true, false);
    }
    
    // Retry exchange with TTL
    @Bean
    public DirectExchange jobRetryExchange() {
        return new DirectExchange(JOB_RETRY_EXCHANGE, true, false);
    }
    
    // Job execution queue
    @Bean
    public Queue jobExecutionQueue() {
        return QueueBuilder.durable(JOB_EXECUTION_QUEUE)
                .withArgument("x-dead-letter-exchange", JOB_RETRY_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", JOB_RETRY_ROUTING_KEY)
                .build();
    }
    
    // Job result queue
    @Bean
    public Queue jobResultQueue() {
        return QueueBuilder.durable(JOB_RESULT_QUEUE).build();
    }
    
    // Retry queue with TTL
    @Bean
    public Queue jobRetryQueue() {
        return QueueBuilder.durable(JOB_RETRY_QUEUE)
                .withArgument("x-message-ttl", 60000) // 1 minute delay
                .withArgument("x-dead-letter-exchange", JOB_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", JOB_EXECUTION_ROUTING_KEY)
                .build();
    }
    
    // Dead letter queue for failed jobs
    @Bean
    public Queue jobDlq() {
        return QueueBuilder.durable(JOB_DLQ).build();
    }
    
    // Bindings
    @Bean
    public Binding jobExecutionBinding() {
        return BindingBuilder.bind(jobExecutionQueue())
                .to(jobExchange())
                .with(JOB_EXECUTION_ROUTING_KEY);
    }
    
    @Bean
    public Binding jobResultBinding() {
        return BindingBuilder.bind(jobResultQueue())
                .to(jobExchange())
                .with(JOB_RESULT_ROUTING_KEY);
    }
    
    @Bean
    public Binding jobRetryBinding() {
        return BindingBuilder.bind(jobRetryQueue())
                .to(jobRetryExchange())
                .with(JOB_RETRY_ROUTING_KEY);
    }
}
