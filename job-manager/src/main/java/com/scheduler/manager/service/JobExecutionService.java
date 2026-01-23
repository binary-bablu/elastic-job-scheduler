package com.scheduler.manager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.scheduler.manager.dto.JobExecutionDetailsDto;
import com.scheduler.manager.dto.JobExecutionListDto;
import com.scheduler.manager.entity.JobExecution;
import com.scheduler.manager.repository.JobExecutionsRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class JobExecutionService {
	
	@Autowired
	private JobExecutionsRepository jobExecutionsRepository;

	public List<JobExecutionListDto> listJobExecutions(Integer jobId) {
		List<JobExecution> jobExecutionsList = jobExecutionsRepository.findByJobId(jobId);
		return convertToJobExecutionListToDtoList(jobExecutionsList);
	}
	
	public JobExecutionDetailsDto getExecutionDetails(Integer jobId,Integer executionId) {
		
		JobExecution jobExecution = jobExecutionsRepository.findByJobIdAndExecutionId(jobId,executionId);
		return convertToJobExecutionDetailsDto(jobExecution);
	}
	
	private List<JobExecutionListDto> convertToJobExecutionListToDtoList(List<JobExecution> jobExecutionsList){
		
		List<JobExecutionListDto> dtoList = new ArrayList<JobExecutionListDto>();
		for(JobExecution jobExecution : jobExecutionsList) {
			JobExecutionListDto dto = new JobExecutionListDto();
			dto.setExecEndTime(jobExecution.getExecEndTime().toString());
			dto.setExecStartTime(jobExecution.getExecStartTime().toString());
			dto.setJobId(jobExecution.getJobId());
			dto.setRetryAttempt(jobExecution.getIsRetryAttempt());
			dto.setRetryAttemptNumber(jobExecution.getRetryAttemptNumber());
			dto.setStatus(jobExecution.getStatus());
			dto.setExecutionId(jobExecution.getExecutionId());
			dtoList.add(dto);
		}
		return dtoList;
	}
	
	private JobExecutionDetailsDto convertToJobExecutionDetailsDto(JobExecution jobExecution){
		
		JobExecutionDetailsDto dto = new JobExecutionDetailsDto();
		dto.setExecEndTime(jobExecution.getExecEndTime().toString());
		dto.setExecStartTime(jobExecution.getExecStartTime().toString());
		dto.setJobId(jobExecution.getJobId());
		dto.setIsRetryAttempt(jobExecution.getIsRetryAttempt());
		dto.setRetryAttemptNumber(jobExecution.getRetryAttemptNumber());
		dto.setStatus(jobExecution.getStatus());
		dto.setExecutionId(jobExecution.getExecutionId());
		dto.setQueuedEndTime(jobExecution.getQueuedEndTime().toString());
		dto.setQueuedStartTime(jobExecution.getQueuedStartTime().toString());
		dto.setErrorMessage(jobExecution.getErrorMessage());
		dto.setOutputMessage(jobExecution.getOutputMessage());
		
		return dto;
	}
}
