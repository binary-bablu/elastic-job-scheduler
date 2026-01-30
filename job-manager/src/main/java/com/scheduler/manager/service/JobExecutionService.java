package com.scheduler.manager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.scheduler.manager.dto.JobExecutionDetailsDto;
import com.scheduler.manager.dto.JobExecutionListDto;
import com.scheduler.manager.entity.JobExecution;
import com.scheduler.manager.repository.JobExecutionsRepository;
import com.scheduler.utils.TimestampConverter;

import java.util.ArrayList;
import java.util.List;

@Service
public class JobExecutionService {
	
	@Autowired
	private JobExecutionsRepository jobExecutionsRepository;

	public List<JobExecutionListDto> listJobExecutions(Integer jobId,String timezone) {
	
		List<JobExecution> jobExecutionsList = jobExecutionsRepository.findByJobId(jobId);
		return convertToJobExecutionListToDtoList(jobExecutionsList,timezone);
	}
	
	public JobExecutionDetailsDto getExecutionDetails(Integer jobId,Integer executionId,String timezone) {
		
		JobExecution jobExecution = jobExecutionsRepository.findByJobIdAndExecutionId(jobId,executionId);
		return convertToJobExecutionDetailsDto(jobExecution,timezone);
	}
	
	private List<JobExecutionListDto> convertToJobExecutionListToDtoList(List<JobExecution> jobExecutionsList,String timezone){
		
		List<JobExecutionListDto> dtoList = new ArrayList<JobExecutionListDto>();
		for(JobExecution jobExecution : jobExecutionsList) {
			JobExecutionListDto dto = new JobExecutionListDto();
			dto.setExecEndTime(TimestampConverter.formatTimestamp(jobExecution.getExecEndTime(),timezone));
			dto.setExecStartTime(TimestampConverter.formatTimestamp(jobExecution.getExecStartTime(),timezone));
			dto.setJobId(jobExecution.getJobId());
			dto.setRetryAttempt(jobExecution.getIsRetryAttempt());
			dto.setRetryAttemptNumber(jobExecution.getRetryAttemptNumber());
			dto.setStatus(jobExecution.getStatus());
			dto.setExecutionId(jobExecution.getExecutionId());
			dtoList.add(dto);
		}
		return dtoList;
	}
	
	private JobExecutionDetailsDto convertToJobExecutionDetailsDto(JobExecution jobExecution,String timezone){
		
		JobExecutionDetailsDto dto = new JobExecutionDetailsDto();
		dto.setExecEndTime(TimestampConverter.formatTimestamp(jobExecution.getExecEndTime(),timezone));
		dto.setExecStartTime(TimestampConverter.formatTimestamp(jobExecution.getExecStartTime(),timezone));
		dto.setJobId(jobExecution.getJobId());
		dto.setIsRetryAttempt(jobExecution.getIsRetryAttempt());
		dto.setRetryAttemptNumber(jobExecution.getRetryAttemptNumber());
		dto.setStatus(jobExecution.getStatus());
		dto.setExecutionId(jobExecution.getExecutionId());
		dto.setQueuedEndTime(TimestampConverter.formatTimestamp(jobExecution.getQueuedEndTime(),timezone));
		dto.setQueuedStartTime(TimestampConverter.formatTimestamp(jobExecution.getQueuedStartTime(),timezone));
		dto.setErrorMessage(jobExecution.getErrorMessage());
		dto.setOutputMessage(jobExecution.getOutputMessage());
		
		return dto;
	}
}
