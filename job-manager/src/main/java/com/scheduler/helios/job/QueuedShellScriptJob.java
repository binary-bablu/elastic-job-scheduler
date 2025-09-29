package com.scheduler.helios.job;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

@Component
@DisallowConcurrentExecution
public class QueuedShellScriptJob implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
   
    }
   
}
