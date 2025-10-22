package com.scheduler.helios.job;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.scheduler.helios.dto.JobExecutionRequest;
import com.scheduler.helios.entity.JobExecution;
import com.scheduler.helios.entity.JobScheduleDefinition;
import com.scheduler.helios.repository.JobExecutionsRepository;
import com.scheduler.helios.repository.JobSchedDefRepository;
import com.scheduler.helios.service.JobQueueService;

@Component
@DisallowConcurrentExecution
public class QueuedShellScriptJob implements Job {

private static final Logger logger = LoggerFactory.getLogger(QueuedShellScriptJob.class);
    
    @Autowired
    private JobQueueService jobQueueService;
    
    @Autowired
    private JobExecutionsRepository jobExecInfoRepository;
    
    @Autowired
    private JobSchedDefRepository jobInfoRepository;
    
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
    	
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        String jobKey = context.getJobDetail().getKey().toString();
       
        JobScheduleDefinition jobSchedDef = getJobSchedDefinition(dataMap);
        logger.info("Quartz triggered job: {} - Publishing to execution queue", jobKey);
        
        try {
            // Create execution request
            JobExecutionRequest request = createJobExecReq(context,jobSchedDef);
            
            JobExecution jobExecInfo = jobExecInfoRepository.save(createInitialJobExecInfoEntity(request,"QUEUED",null));
            request.setExecutionId(jobExecInfo.getExecutionId());
          
            // Publish to execution queue
            jobQueueService.publishJobExecution(request);
            
            logger.info("Successfully published job to queue: {} with executionId: {}", 
                       jobKey, request.getExecutionId());
            
        } catch (Exception  e) {
            logger.error("Failed to publish job to queue: {}", jobKey, e);
            
            JobExecutionRequest request  = createJobExecReq(context,jobSchedDef);
            jobExecInfoRepository.save(createFailureJobExecEntity(request,"FAILED","Error Connecting to Queue"));

            throw new JobExecutionException("Failed to publish job to execution queue", e);
        }
    }
    
    private JobExecutionRequest createJobExecReq(JobExecutionContext context,JobScheduleDefinition jobInfo ) {
    	
    	JobDataMap dataMap = context.getJobDetail().getJobDataMap();
    	
    	JobExecutionRequest request = new JobExecutionRequest();
        request.setJobId((Integer) dataMap.get("jobId"));
        request.setJobName(context.getJobDetail().getKey().getName());
        request.setJobGroup(context.getJobDetail().getKey().getGroup());
        request.setScriptPath(dataMap.getString("scriptPath"));
        request.setMaxRetryAttempts(jobInfo.getRetryMaxAttempts());
        request.setCurrentRetryCount(0);
        request.setBackOffStrategy(jobInfo.getRetryStrategy());
        request.setInitialDelayMs(jobInfo.getRetryInitialDelayInMs());
        request.setQueuedTime(LocalDateTime.now());
        request.setTimeoutSeconds(jobInfo.getTimeout());
        request.setNonRetryableExitCodes(jobInfo.getNonRetryableExitCodes());
        
        // Extract parameters
        Map<String, String> parameters = new HashMap<>();
        for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
            if (!isInternalKey(entry.getKey())) {
                parameters.put(entry.getKey(), String.valueOf(entry.getValue()));
            }
        }
        request.setParameters(parameters);
        
        return request;
    }
        
    private boolean isInternalKey(String key) {
        return key.equals("scriptPath") || key.equals("jobId") || 
               key.equals("timeoutSeconds") || key.equals("maxRetries");
    }
    
    private JobExecution createInitialJobExecInfoEntity(JobExecutionRequest jobExecRequest,String status,String errMessage) {
    	
    	JobExecution jobExecInfo = new JobExecution();
    	jobExecInfo.setJobId(jobExecRequest.getJobId());
    	jobExecInfo.setStatus(status);
    	jobExecInfo.setQueuedStartTime(Timestamp.valueOf(jobExecRequest.getQueuedTime()));
    	jobExecInfo.setRetryAttemptNumber(0);
    	jobExecInfo.setIsRetryAttempt("NO");
    	
    	return jobExecInfo;
    }
    
    private JobScheduleDefinition getJobSchedDefinition(JobDataMap dataMap) {
    	
    	Integer jobId = (Integer) dataMap.get("jobId");
    	Optional<JobScheduleDefinition> optionalJobInfo = jobInfoRepository.findById(jobId);
        if (optionalJobInfo.isEmpty()) {
            logger.error("Job Not Found : "+ jobId);
            return null;
        }
        JobScheduleDefinition jobInfo = optionalJobInfo.get();
    	return jobInfo;
    }
	
    private JobExecution createFailureJobExecEntity(JobExecutionRequest jobExecRequest,String status,String errMessage) {
    	
    	JobExecution jobExecInfo = new JobExecution();
    	jobExecInfo.setJobId(jobExecRequest.getJobId());
    	jobExecInfo.setStatus(status);
    	jobExecInfo.setErrorMessage(errMessage);
    	Timestamp startEndTs = Timestamp.valueOf(LocalDateTime.now());
    	jobExecInfo.setExecEndTime(startEndTs);
    	jobExecInfo.setExecStartTime(startEndTs);
    	return jobExecInfo;
    }
}
