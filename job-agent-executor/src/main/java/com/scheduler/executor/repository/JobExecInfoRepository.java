package com.scheduler.executor.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scheduler.executor.entity.JobExecInfo;

@Repository
public interface JobExecInfoRepository extends JpaRepository<JobExecInfo, Integer> {
	
	public JobExecInfo findByJobIdAndExecutionId(Integer jobId, Integer exceutionId);
   
}