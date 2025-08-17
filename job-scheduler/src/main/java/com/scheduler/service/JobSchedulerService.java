package com.scheduler.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;

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

import com.scheduler.dto.JobRequest;
import com.scheduler.dto.JobResponse;
import com.scheduler.entity.JobInfo;
import com.scheduler.job.QueuedShellScriptJob;
import com.scheduler.repository.JobInfoRepository;

@Service
public class JobSchedulerService {
	
	private static final Logger logger = LoggerFactory.getLogger(JobSchedulerService.class);

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private JobInfoRepository jobInfoRepository;

    @Transactional
    public JobResponse createJob(JobRequest jobRequest) throws SchedulerException {
       
    	// Check if job already exists
        if (jobInfoRepository.existsByJobNameAndJobGroup(jobRequest.getJobName(), jobRequest.getJobGroup())) {
            throw new IllegalArgumentException("Job with name " + jobRequest.getJobName() + " and group " + jobRequest.getJobGroup() + " already exists");
        }

        // Save to database
        JobInfo jobInfo = new JobInfo();
        jobInfo.setJobName(jobRequest.getJobName());
        jobInfo.setJobGroup(jobRequest.getJobGroup());
        jobInfo.setCronExpression(jobRequest.getCronExpression());
        jobInfo.setScriptPath(jobRequest.getScriptPath());
        
        for (Entry<String, String> entry : jobRequest.getParameters().entrySet()) {
            jobInfo.addParameter(entry.getKey(), entry.getValue());
        }
        jobInfo.setDescription(jobRequest.getDescription());

        jobInfo = jobInfoRepository.save(jobInfo);
        
        scheduleQuartzJob(jobInfo);

        return convertToResponse(jobInfo);
    }

    @Transactional
    public JobResponse updateJob(Integer jobId, JobRequest jobRequest) throws SchedulerException {
       
    	Optional<JobInfo> optionalJobInfo = jobInfoRepository.findById(jobId);
        if (optionalJobInfo.isEmpty()) {
            throw new IllegalArgumentException("Job with id " + jobId + " not found");
        }

        JobInfo jobInfo = optionalJobInfo.get();
        
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

        return convertToResponse(jobInfo);
    }

    @Transactional
    public void deleteJob(Integer jobId) throws SchedulerException {
        
    	Optional<JobInfo> optionalJobInfo = jobInfoRepository.findById(jobId);
        if (optionalJobInfo.isEmpty()) {
            throw new IllegalArgumentException("Job with id " + jobId + " not found");
        }

        JobInfo jobInfo = optionalJobInfo.get();
        
        // Remove from scheduler
        JobKey jobKey = new JobKey(jobInfo.getJobName(), jobInfo.getJobGroup());
        if (scheduler.checkExists(jobKey)) {
            scheduler.deleteJob(jobKey);
        }

        // Remove from database
        jobInfoRepository.delete(jobInfo);
    }

    public JobResponse getJob(Integer jobId) throws SchedulerException {
        
    	Optional<JobInfo> optionalJobInfo = jobInfoRepository.findById(jobId);
        if (optionalJobInfo.isEmpty()) {
            throw new IllegalArgumentException("Job with id " + jobId + " not found");
        }

        return convertToResponse(optionalJobInfo.get());
    }

    public List<JobResponse> getAllJobs() throws SchedulerException {
       
    	List<JobInfo> jobInfos = jobInfoRepository.findAll();
        List<JobResponse> responses = new ArrayList<>();

        for (JobInfo jobInfo : jobInfos) {
            responses.add(convertToResponse(jobInfo));
        }

        return responses;
    }

    public void pauseJob(Integer jobId) throws SchedulerException {
      
    	Optional<JobInfo> optionalJobInfo = jobInfoRepository.findById(jobId);
        if (optionalJobInfo.isEmpty()) {
            throw new IllegalArgumentException("Job with id " + jobId + " not found");
        }

        JobInfo jobInfo = optionalJobInfo.get();
        JobKey jobKey = new JobKey(jobInfo.getJobName(), jobInfo.getJobGroup());
        
        if (scheduler.checkExists(jobKey)) {
            scheduler.pauseJob(jobKey);
        }
    }

    public void resumeJob(Integer jobId) throws SchedulerException {
      
    	Optional<JobInfo> optionalJobInfo = jobInfoRepository.findById(jobId);
        if (optionalJobInfo.isEmpty()) {
            throw new IllegalArgumentException("Job with id " + jobId + " not found");
        }

        JobInfo jobInfo = optionalJobInfo.get();
        JobKey jobKey = new JobKey(jobInfo.getJobName(), jobInfo.getJobGroup());
        
        if (scheduler.checkExists(jobKey)) {
            scheduler.resumeJob(jobKey);
        }
    }

    public void triggerJob(Integer jobId) throws SchedulerException {
        
    	Optional<JobInfo> optionalJobInfo = jobInfoRepository.findById(jobId);
        if (optionalJobInfo.isEmpty()) {
            throw new IllegalArgumentException("Job with id " + jobId + " not found");
        }

        JobInfo jobInfo = optionalJobInfo.get();
        JobKey jobKey = new JobKey(jobInfo.getJobName(), jobInfo.getJobGroup());
        
        if (scheduler.checkExists(jobKey)) {
            scheduler.triggerJob(jobKey);
        }
    }

    private void scheduleQuartzJob(JobInfo jobInfo) throws SchedulerException {
    	
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
                 .withSchedule(CronScheduleBuilder.cronSchedule(jobInfo.getCronExpression()))
                 .build();

         scheduler.scheduleJob(jobDetail, trigger);
         jobInfo.setCronExpression(trigger.getCronExpression());
         logger.info("Scheduled job: {}.{}", jobInfo.getJobGroup(), jobInfo.getJobName());
    }

    private JobResponse convertToResponse(JobInfo jobInfo) throws SchedulerException {
    	
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
