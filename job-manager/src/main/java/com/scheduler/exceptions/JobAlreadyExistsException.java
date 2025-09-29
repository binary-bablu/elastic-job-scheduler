package com.scheduler.exceptions;

public class JobAlreadyExistsException extends Throwable {
	
    private static final long serialVersionUID = 1L;

	public JobAlreadyExistsException(String jobName, String jobGroup) {
        super(String.format("Job already exists: %s.%s", jobGroup, jobName));
    }
    
    public JobAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
