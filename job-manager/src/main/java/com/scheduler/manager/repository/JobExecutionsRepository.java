package com.scheduler.manager.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scheduler.manager.entity.JobExecution;

@Repository
public interface JobExecutionsRepository extends JpaRepository<JobExecution, Integer> {
	
	public JobExecution findByJobIdAndExecutionId(Integer jobId, Integer exceutionId);
   
}