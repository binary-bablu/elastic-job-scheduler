package com.scheduler.exceptions;

public class JobAlreadyExistsException extends JobSchedulerException {
	
    public JobAlreadyExistsException(String jobName, String jobGroup) {
        super(String.format("Job already exists: %s.%s", jobGroup, jobName));
    }
}
