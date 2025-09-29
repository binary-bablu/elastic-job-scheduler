package com.scheduler.executor.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.scheduler.executor.entity.JobExecution;
import com.scheduler.executor.repository.JobExecutionsRepository;

@Service
public class JobExecutorStatusService {

	@Autowired
    private JobExecutionsRepository jobExecInfoRepository;
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void createJobExecutionEntry(JobExecution jobExecInfo) {
		
		jobExecInfoRepository.save(jobExecInfo);
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void updateJobExecutionEntry(JobExecution jobExecInfo) {
		
		jobExecInfoRepository.save(jobExecInfo);
	}
}
