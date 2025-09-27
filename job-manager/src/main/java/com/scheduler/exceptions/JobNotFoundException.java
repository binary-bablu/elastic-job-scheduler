package com.scheduler.exceptions;

public class JobNotFoundException extends JobSchedulerException {
	
    public JobNotFoundException(Integer jobDefinitionId) {
        super(String.format("Job not found: %s", jobDefinitionId));
    }
}
