package com.scheduler.manager.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scheduler.manager.entity.JobScheduleDefinition;

@Repository
public interface JobInfoRepository extends JpaRepository<JobScheduleDefinition, Integer> {
	
    Optional<JobScheduleDefinition> findByJobNameAndJobGroup(String jobName, String jobGroup);
    boolean existsByJobNameAndJobGroup(String jobName, String jobGroup);
    
}