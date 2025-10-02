package com.scheduler.executor.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.scheduler.executor.dto.JobExecutionRequest;
import com.scheduler.executor.dto.JobExecutionResult;
import com.scheduler.executor.entity.JobExecution;
import com.scheduler.executor.repository.JobExecutionsRepository;

@Service
public class JobExecutorStatusService {

	@Autowired
    private JobExecutionsRepository jobExecInfoRepository;
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void createJobExecutionEntry(JobExecutionRequest jobExecRequest,String status) {
		
		JobExecution jobExecInfo = createJobExecInfoEntity(jobExecRequest,status);
		jobExecInfoRepository.save(jobExecInfo);
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void updateJobExecutionEntry(JobExecutionResult result) {
		
		JobExecution jobExecInfo = updateJobExecInfoEntity(result);
		jobExecInfoRepository.save(jobExecInfo);
	}
	
	private JobExecution createJobExecInfoEntity(JobExecutionRequest jobExecRequest,String status) {
    	
	 	LocalDateTime startTime = LocalDateTime.now();
	 	jobExecRequest.setStartTime(startTime);
    	JobExecution jobExecInfo = jobExecInfoRepository.
    			findByJobIdAndExecutionId(jobExecRequest.getJobId(),jobExecRequest.getExecutionId());
    	
    	jobExecInfo.setStatus(status);
    	jobExecInfo.setExecStartTime(Timestamp.valueOf(startTime));
    	jobExecInfo.setQueuedEndTime(Timestamp.valueOf(startTime));
    	
    	return jobExecInfo;
	 }
	    
	 private JobExecution updateJobExecInfoEntity(JobExecutionResult result) {
    	 String status = result.isSuccess() ? "COMPLETED" : "FAILED";
         JobExecution jobExecInfo = jobExecInfoRepository.findByJobIdAndExecutionId(result.getJobId(),result.getExecutionId());
         jobExecInfo.setStatus(status);
         jobExecInfo.setExecStartTime(Timestamp.valueOf(result.getStartTime()));
         jobExecInfo.setExecEndTime(Timestamp.valueOf(result.getEndTime()));
         
         return jobExecInfo;
	 }
}
