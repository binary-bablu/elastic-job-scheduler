package com.scheduler.helios.exceptions;

public class JobNotFoundException extends Throwable {
	
    private static final long serialVersionUID = 1L;

	public JobNotFoundException(Integer jobDefinitionId) {
        super(String.format("Job not found: %s", jobDefinitionId));
    }
    
    public JobNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
