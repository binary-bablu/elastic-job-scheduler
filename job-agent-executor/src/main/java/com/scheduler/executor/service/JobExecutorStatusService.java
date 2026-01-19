package com.scheduler.executor.service;

import java.sql.Timestamp;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.scheduler.executor.dto.JobExecutionRequest;
import com.scheduler.executor.dto.JobExecutionResult;
import com.scheduler.executor.entity.JobExecution;
import com.scheduler.executor.entity.JobScheduleDefinition;
import com.scheduler.executor.repository.JobExecutionsRepository;
import com.scheduler.executor.repository.JobInfoRepository;

@Service
public class JobExecutorStatusService {

	@Autowired
    private JobExecutionsRepository jobExecInfoRepository;
	
	@Autowired
	private JobInfoRepository jobInfoRepository;
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void updateJobExecutionEntry(JobExecutionRequest jobExecRequest,String status) {
		
		JobExecution jobExecInfo = updateJobExecInfoEntity(jobExecRequest,status);
		jobExecInfoRepository.save(jobExecInfo);
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void updateJobExecutionEntry(JobExecutionResult result) {
		
		JobExecution jobExecInfo = updateJobExecInfoEntity(result);
		jobExecInfoRepository.save(jobExecInfo);
		
		Optional<JobScheduleDefinition> jobSchedDefOpt = jobInfoRepository.findById(jobExecInfo.getJobId());
		JobScheduleDefinition jobSchedDef = jobSchedDefOpt.get();
		String status = result.isSuccess() ? "COMPLETED" : "FAILED";
		if(status == "COMPLETED") {
			jobSchedDef.setLastSuccessDt(Timestamp.valueOf(result.getEndTime()));
			int currSuccessCount = jobSchedDef.getSuccessCount() ;
			jobSchedDef.setSuccessCount(currSuccessCount+1);
			
		} else {
			jobSchedDef.setLastErrorDt(Timestamp.valueOf(result.getEndTime()));
			int currErrorCount = jobSchedDef.getErrorCount() ;
			jobSchedDef.setErrorCount(currErrorCount+1);
		}
		jobInfoRepository.save(jobSchedDef);
	}
	
	private JobExecution updateJobExecInfoEntity(JobExecutionRequest jobExecRequest,String status) {
	 	
    	JobExecution jobExecInfo = jobExecInfoRepository.
    			findByJobIdAndExecutionId(jobExecRequest.getJobId(),jobExecRequest.getExecutionId());
    	
    	jobExecInfo.setStatus(status);
    	jobExecInfo.setExecStartTime(Timestamp.valueOf(jobExecRequest.getStartTime()));
    	jobExecInfo.setQueuedEndTime(Timestamp.valueOf(jobExecRequest.getStartTime()));
    	jobExecInfo.setIsRetryAttempt(jobExecRequest.getCurrentRetryCount()>0 ? "YES" :"NO");
    	
    	return jobExecInfo;
	 }
	    
	 private JobExecution updateJobExecInfoEntity(JobExecutionResult result) {
    	 
		 String status = result.isSuccess() ? "COMPLETED" : "FAILED";
         JobExecution jobExecInfo = jobExecInfoRepository.findByJobIdAndExecutionId(result.getJobId(),result.getExecutionId());
         jobExecInfo.setStatus(status);
         jobExecInfo.setExecStartTime(Timestamp.valueOf(result.getStartTime()));
         jobExecInfo.setExecEndTime(Timestamp.valueOf(result.getEndTime()));
         jobExecInfo.setErrorMessage(result.getErrorMessage());
         jobExecInfo.setOutputMessage(result.getOutput());
         
         return jobExecInfo;
	 }
	 
	 @Transactional(propagation = Propagation.REQUIRES_NEW)
	 public void updateJobExecDLQSentStatus(Integer jobId,Integer jobExecutionId) {
		 
		 JobExecution jobExecInfo = jobExecInfoRepository.findByJobIdAndExecutionId(jobId,jobExecutionId);
         jobExecInfo.setSentToDlq("YES");
         jobExecInfoRepository.save(jobExecInfo);
     }
}
