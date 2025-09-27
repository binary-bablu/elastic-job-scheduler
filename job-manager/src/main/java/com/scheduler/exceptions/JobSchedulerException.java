package com.scheduler.exceptions;

public class JobSchedulerException extends RuntimeException {
    public JobSchedulerException(String message) {
        super(message);
    }
    
    public JobSchedulerException(String message, Throwable cause) {
        super(message, cause);
    }
}