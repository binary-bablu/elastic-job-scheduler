package com.scheduler.manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scheduler.manager.entity.JobParameter;

@Repository
public interface JobParameterRepository extends JpaRepository<JobParameter, Integer> {
    
}