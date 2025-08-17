package com.scheduler.helios.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scheduler.helios.entity.JobInfo;

@Repository
public interface JobInfoRepository extends JpaRepository<JobInfo, Integer> {
	
    Optional<JobInfo> findByJobNameAndJobGroup(String jobName, String jobGroup);
    boolean existsByJobNameAndJobGroup(String jobName, String jobGroup);
    
}