package com.scheduler.executor.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.scheduler.executor.entity.JobExecInfo;
import com.scheduler.executor.repository.JobExecInfoRepository;

@Service
public class JobExecutorStatusService {

	@Autowired
    private JobExecInfoRepository jobExecInfoRepository;
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void createJobExecutionEntry(JobExecInfo jobExecInfo) {
		
		jobExecInfoRepository.save(jobExecInfo);
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void updateJobExecutionEntry(JobExecInfo jobExecInfo) {
		
		jobExecInfoRepository.save(jobExecInfo);
	}
}
