package com.scheduler.manager.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TimeZone;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scheduler.exceptions.JobAlreadyExistsException;
import com.scheduler.exceptions.JobNotFoundException;
import com.scheduler.helios.job.QueuedShellScriptJob;
import com.scheduler.manager.dto.JobRequest;
import com.scheduler.manager.dto.JobResponse;
import com.scheduler.manager.entity.JobScheduleDefinition;
import com.scheduler.manager.repository.JobInfoRepository;

@Service
public class JobSchedulerService {
	
	private static final Logger logger = LoggerFactory.getLogger(JobSchedulerService.class);

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private JobInfoRepository jobInfoRepository;

    @Transactional
    public JobResponse createJob(JobRequest jobRequest) throws JobAlreadyExistsException,SchedulerException {
       
    	// Check if job already exists
        if (jobInfoRepository.existsByJobNameAndJobGroup(jobRequest.getJobName(), jobRequest.getJobGroup())) {
            throw new JobAlreadyExistsException(jobRequest.getJobGroup(),jobRequest.getJobName());
        }

        // Save to database
        JobScheduleDefinition jobInfo = new JobScheduleDefinition();
        jobInfo.setJobName(jobRequest.getJobName());
        jobInfo.setJobGroup(jobRequest.getJobGroup());
        jobInfo.setCronExpression(jobRequest.getCronExpression());
        jobInfo.setScriptPath(jobRequest.getScriptPath());
        jobInfo.setStatus("ACTIVE");
        
        for (Entry<String, String> entry : jobRequest.getParameters().entrySet()) {
            jobInfo.addParameter(entry.getKey(), entry.getValue());
        }
        jobInfo.setDescription(jobRequest.getDescription());

        jobInfo = jobInfoRepository.save(jobInfo);
        
        scheduleQuartzJob(jobInfo);
        
        JobResponse response  = convertToResponse(jobInfo);
        logger.info("Job Created for Scheduling in database : "+response);

        return response;
    }

    @Transactional
    public JobResponse updateJob(Integer jobId, JobRequest jobRequest) throws JobNotFoundException, SchedulerException {
       
    	Optional<JobScheduleDefinition> optionalJobInfo = jobInfoRepository.findById(jobId);
        if (optionalJobInfo.isEmpty()) {
            throw new JobNotFoundException(jobId);
        }

        JobScheduleDefinition jobInfo = optionalJobInfo.get();
        
        // Remove existing job from scheduler
        JobKey jobKey = new JobKey(jobInfo.getJobName(), jobInfo.getJobGroup());
        if (scheduler.checkExists(jobKey)) {
            scheduler.deleteJob(jobKey);
        }

        // Update job info
        jobInfo.setJobName(jobRequest.getJobName());
        jobInfo.setJobGroup(jobRequest.getJobGroup());
        jobInfo.setCronExpression(jobRequest.getCronExpression());
        jobInfo.setScriptPath(jobRequest.getScriptPath());
        
        for (Entry<String, String> entry : jobRequest.getParameters().entrySet()) {
            jobInfo.addParameter(entry.getKey(), entry.getValue());
        }

        jobInfo = jobInfoRepository.save(jobInfo);
        scheduleQuartzJob(jobInfo);
        
        JobResponse response  = convertToResponse(jobInfo);
        logger.info("Job updates done in database and scheduler : "+ response);

        return response;
    }

    @Transactional
    public void deleteJob(Integer jobId) throws JobNotFoundException,SchedulerException {
        
    	Optional<JobScheduleDefinition> optionalJobInfo = jobInfoRepository.findById(jobId);
        if (optionalJobInfo.isEmpty()) {
            throw new JobNotFoundException(jobId);
        }

        JobScheduleDefinition jobInfo = optionalJobInfo.get();
        
        // Remove from scheduler
        JobKey jobKey = new JobKey(jobInfo.getJobName(), jobInfo.getJobGroup());
        if (scheduler.checkExists(jobKey)) {
            scheduler.deleteJob(jobKey);
        }
        // Remove from database
        jobInfoRepository.delete(jobInfo);
        JobResponse response  = convertToResponse(jobInfo);
        logger.info("Job Removed from database and scheduler : "+ response);
        
    }

    public JobResponse getJob(Integer jobId) throws JobNotFoundException,SchedulerException {
        
    	Optional<JobScheduleDefinition> optionalJobInfo = jobInfoRepository.findById(jobId);
        if (optionalJobInfo.isEmpty()) {
            throw new JobNotFoundException(jobId);
        }

        return convertToResponse(optionalJobInfo.get());
    }
    
    @Transactional
    public void pauseJob(Integer jobId) throws JobNotFoundException,SchedulerException {
      
    	Optional<JobScheduleDefinition> optionalJobInfo = jobInfoRepository.findById(jobId);
        if (optionalJobInfo.isEmpty()) {
            throw new JobNotFoundException(jobId);
        }

        JobScheduleDefinition jobInfo = optionalJobInfo.get();
        jobInfo.setStatus("PAUSED");
        JobKey jobKey = new JobKey(jobInfo.getJobName(), jobInfo.getJobGroup());
        
        if (scheduler.checkExists(jobKey)) {
            scheduler.pauseJob(jobKey);
            jobInfoRepository.save(jobInfo);
            logger.info("Job Paused in database and scheduler : "+ jobId);
        }
    }
    
    @Transactional
    public void resumeJob(Integer jobId) throws JobNotFoundException,SchedulerException {
      
    	Optional<JobScheduleDefinition> optionalJobInfo = jobInfoRepository.findById(jobId);
        if (optionalJobInfo.isEmpty()) {
            throw new JobNotFoundException(jobId);
        }

        JobScheduleDefinition jobInfo = optionalJobInfo.get();
        JobKey jobKey = new JobKey(jobInfo.getJobName(), jobInfo.getJobGroup());
        
        if (scheduler.checkExists(jobKey)) {
            scheduler.resumeJob(jobKey);
            jobInfo.setStatus("ACTIVE");
            jobInfoRepository.save(jobInfo);
            logger.info("Job Resumed in database and scheduler : "+ jobId);
        }
    }
    
    @Transactional
    public void triggerJob(Integer jobId) throws JobNotFoundException,SchedulerException {
        
    	Optional<JobScheduleDefinition> optionalJobInfo = jobInfoRepository.findById(jobId);
        if (optionalJobInfo.isEmpty()) {
            throw new JobNotFoundException(jobId);
        }

        JobScheduleDefinition jobInfo = optionalJobInfo.get();
        JobKey jobKey = new JobKey(jobInfo.getJobName(), jobInfo.getJobGroup());
        
        if (scheduler.checkExists(jobKey)) {
            scheduler.triggerJob(jobKey);
            logger.info("Job Triggered in database : "+ jobId);
        }
    }

    private void scheduleQuartzJob(JobScheduleDefinition jobInfo) throws SchedulerException {
    	
    	JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("scriptPath", jobInfo.getScriptPath());
        jobDataMap.put("jobId", jobInfo.getId());
         
        if (jobInfo.getParameters() != null) {
             jobDataMap.putAll(jobInfo.getParameters());
        }

        JobDetail jobDetail = JobBuilder.newJob(QueuedShellScriptJob.class)
                 .withIdentity(jobInfo.getJobName(), jobInfo.getJobGroup())
                 .withDescription(jobInfo.getDescription())
                 .setJobData(jobDataMap)
                 .storeDurably(true)
                 .build();
                 
         CronTrigger trigger = TriggerBuilder.newTrigger()
        		    .withIdentity(jobInfo.getJobName() + "_trigger", jobInfo.getJobGroup())
        		    .withSchedule(
        		        CronScheduleBuilder.cronSchedule(jobInfo.getCronExpression())
        		            .inTimeZone(TimeZone.getDefault())  // Use system default time zone
        		    )
        		    .build();        
         try {
        	 scheduler.scheduleJob(jobDetail, trigger);
             jobInfo.setCronExpression(trigger.getCronExpression());
             
             // Print initial fire time
             Date nextFireTime = trigger.getNextFireTime();
             System.out.println("Next Fire Time (raw): " + nextFireTime);
             System.out.println("Next Fire Time (local): " +
                     Instant.ofEpochMilli(nextFireTime.getTime())
                             .atZone(ZoneId.systemDefault()));

             
             logger.info("Scheduled job: {}.{}", jobInfo.getJobGroup(), jobInfo.getJobName());
        	 
         }catch(Exception exp) {
        	 exp.printStackTrace(); 
        	 logger.error(" Error in persisting job info "+exp.getMessage());
        }
    }

    private JobResponse convertToResponse(JobScheduleDefinition jobInfo) throws SchedulerException {
    	
        JobResponse response = new JobResponse();
        response.setId(jobInfo.getId());
        response.setJobName(jobInfo.getJobName());
      
        // Get status from Quartz scheduler
        JobKey jobKey = new JobKey(jobInfo.getJobName(), jobInfo.getJobGroup());
        if (scheduler.checkExists(jobKey)) {
            Trigger.TriggerState state = scheduler.getTriggerState(new TriggerKey(jobInfo.getJobName() + "_trigger", jobInfo.getJobGroup()));
            response.setStatus(state.name());

            List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
            if (!triggers.isEmpty()) {
                Trigger trigger = triggers.get(0);
                if (trigger.getNextFireTime() != null) {
                    response.setNextFireTime(LocalDateTime.ofInstant(trigger.getNextFireTime().toInstant(), ZoneId.systemDefault()));
                }
                if (trigger.getPreviousFireTime() != null) {
                    response.setPreviousFireTime(LocalDateTime.ofInstant(trigger.getPreviousFireTime().toInstant(), ZoneId.systemDefault()));
                }
            }
        } else {
            response.setStatus("NOT_SCHEDULED");
        }
        return response;
    }
}
