package com.scheduler.manager.service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
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

import com.scheduler.helios.job.QueuedShellScriptJob;
import com.scheduler.manager.dto.JobRequest;
import com.scheduler.manager.dto.JobResponse;
import com.scheduler.manager.dto.JobsDashBoardDto;
import com.scheduler.manager.entity.JobScheduleDefinition;
import com.scheduler.manager.exception.JobAlreadyExistsException;
import com.scheduler.manager.exception.JobNotFoundException;
import com.scheduler.manager.repository.JobInfoRepository;
import com.scheduler.utils.TimestampConverter;

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
        jobInfo.setRetryMaxAttempts(jobRequest.getRetryConfig().getMaxAttempts());
        jobInfo.setRetryInitialDelayInMs(jobRequest.getRetryConfig().getInitialDelayMs());
        jobInfo.setRetryStrategy(jobRequest.getRetryConfig().getBackOffStrategy());    
        jobInfo.setRetryMultiplier(jobRequest.getRetryConfig().getMultiplier());
        jobInfo.setCreatedAt(LocalDateTime.now());
        jobInfo.setTimeout(jobRequest.getTimeout() !=null ? jobRequest.getTimeout() : 300);      
        for (Entry<String, String> entry : jobRequest.getParameters().entrySet()) {
            jobInfo.addParameter(entry.getKey(), entry.getValue());
        }
        jobInfo.setDescription(jobRequest.getDescription());
        jobInfo.setNonRetryableExitCodes(jobRequest.getRetryConfig().getNonRetryableExitCodes());
        jobInfo.setOwnerEmail(jobRequest.getOwnerEmail());
        
        validateTimezone(jobRequest.getTimezone());
        
        jobInfo.setTimezone(jobRequest.getTimezone());
        
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
        jobInfo.setRetryMaxAttempts(jobRequest.getRetryConfig().getMaxAttempts());
        jobInfo.setRetryInitialDelayInMs(jobRequest.getRetryConfig().getInitialDelayMs());
        jobInfo.setRetryStrategy(jobRequest.getRetryConfig().getBackOffStrategy());
        jobInfo.setTimeout(jobRequest.getTimeout() !=null ? jobRequest.getTimeout() : 300);  
        jobInfo.setDescription(jobRequest.getDescription());
        jobInfo.setRetryMultiplier(jobRequest.getRetryConfig().getMultiplier());
        for (Entry<String, String> entry : jobRequest.getParameters().entrySet()) {
            jobInfo.addParameter(entry.getKey(), entry.getValue());
        }
        jobInfo.setNonRetryableExitCodes(jobRequest.getRetryConfig().getNonRetryableExitCodes());
        jobInfo.setOwnerEmail(jobRequest.getOwnerEmail());
        
        validateTimezone(jobRequest.getTimezone());
        
        jobInfo.setTimezone(jobRequest.getTimezone());
        
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
    
    @Transactional
    public void inactivateJob(Integer jobId) throws JobNotFoundException,SchedulerException {
        
    	Optional<JobScheduleDefinition> optionalJobInfo = jobInfoRepository.findById(jobId);
        if (optionalJobInfo.isEmpty()) {
            throw new JobNotFoundException(jobId);
        }

        JobScheduleDefinition jobInfo = optionalJobInfo.get();
        jobInfo.setStatus("INACTIVE");
        
        // Remove from scheduler
        JobKey jobKey = new JobKey(jobInfo.getJobName(), jobInfo.getJobGroup());
        if (scheduler.checkExists(jobKey)) {
            scheduler.deleteJob(jobKey);
        }
        // Update status in db
        jobInfoRepository.save(jobInfo);
        JobResponse response  = convertToResponse(jobInfo);
        logger.info("Job Inactivated in database and scheduler : "+ response);
    }
    
    @Transactional
    public void activateJob(Integer jobId) throws JobNotFoundException,SchedulerException {
        
    	Optional<JobScheduleDefinition> optionalJobInfo = jobInfoRepository.findById(jobId);
        if (optionalJobInfo.isEmpty()) {
            throw new JobNotFoundException(jobId);
        }

        JobScheduleDefinition jobInfo = optionalJobInfo.get();
        jobInfo.setStatus("ACTIVE");
        
        jobInfo = jobInfoRepository.save(jobInfo);
        
        scheduleQuartzJob(jobInfo);
        
        JobResponse response  = convertToResponse(jobInfo);
        logger.info("Job Activated in database and scheduler : "+ response);
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
                 .usingJobData("timezone", jobInfo.getTimezone())
                 .storeDurably(true)
                 .build();
                 
         CronTrigger trigger = TriggerBuilder.newTrigger()
        		    .withIdentity(jobInfo.getJobName() + "_trigger", jobInfo.getJobGroup())
        		    .withSchedule(
        		        CronScheduleBuilder.cronSchedule(jobInfo.getCronExpression())
        		            .inTimeZone(TimeZone.getTimeZone(jobInfo.getZoneId()))  //Use provided time zone
        		    )
        		    .build();        
         try {
        	 scheduler.scheduleJob(jobDetail, trigger);
             jobInfo.setCronExpression(trigger.getCronExpression());
             
             logger.info("Next Fire Times : " + getNextFireTimes(jobInfo.getJobName(), jobInfo.getJobGroup(), 2));
             
             logger.info("Job Scheduled successfully job: {}.{} with Cron: {} in timezone {}", jobInfo.getJobGroup(), 
            		 jobInfo.getJobName(),jobInfo.getCronExpression(),jobInfo.getTimezone());
        	 
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
    
    public List<JobsDashBoardDto> getAllJobsDashBoardData() {
    	
    	List<JobScheduleDefinition> jobsDashBoardEntityList = jobInfoRepository.findAll();
    	logger.info("Request received for retrieving jobs dashboard data");
    	
    	return convertEntityToDto(jobsDashBoardEntityList);
    }
    
    private List<JobsDashBoardDto> convertEntityToDto(List<JobScheduleDefinition> jobsDashBoardEntityList) {
    	
    	List<JobsDashBoardDto> jobsInfoList = new ArrayList<JobsDashBoardDto>();
    	
    	for(JobScheduleDefinition entity : jobsDashBoardEntityList) {
    		JobsDashBoardDto jobsDashBoardDto = new JobsDashBoardDto();
    		jobsDashBoardDto.setErrorCount(entity.getErrorCount());
    		jobsDashBoardDto.setSuccessCount(entity.getSuccessCount());
    		jobsDashBoardDto.setId(entity.getId());
    		jobsDashBoardDto.setJobDescription(entity.getDescription());
    		jobsDashBoardDto.setJobId(entity.getId());
    		jobsDashBoardDto.setOwnerEmail(entity.getOwnerEmail());
    		jobsDashBoardDto.setSchedule(entity.getCronExpression());
    		jobsDashBoardDto.setStatus(entity.getStatus());
    	    jobsDashBoardDto.setTimezone(entity.getTimezone());
    	    jobsDashBoardDto.setLastErrorDt(TimestampConverter.formatLocalDateTime( entity.getLastErrorDt(),entity.getTimezone()));
    	    jobsDashBoardDto.setLastSuccessDt(TimestampConverter.formatLocalDateTime(  entity.getLastSuccessDt(),entity.getTimezone()));
    		jobsInfoList.add(jobsDashBoardDto);
    	}
    	return jobsInfoList;
    }
    
    /**
     * Validate timezone string
     */
    private void validateTimezone(String timezone) {
        try {
            ZoneId.of(timezone);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                "Invalid timezone: " + timezone + ". Use IANA timezone IDs like 'America/New_York' or 'UTC'");
        }
    }
    
    /**
     * Get next fire times for a job in its configured timezone
     */
    public List<String> getNextFireTimes(String jobName, String jobGroup, int count) 
            throws SchedulerException {
        
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName + "-trigger", jobGroup);
        CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
        
        if (trigger == null) {
            return Collections.emptyList();
        }
        
        List<String> fireTimes = new ArrayList<>();
        Date nextFireTime = trigger.getNextFireTime();
        
        TimeZone tz = trigger.getTimeZone();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        sdf.setTimeZone(tz);
        
        for (int i = 0; i < count && nextFireTime != null; i++) {
            fireTimes.add(sdf.format(nextFireTime));
            nextFireTime = trigger.getFireTimeAfter(nextFireTime);
        }
        
        return fireTimes;
    }
    
}
