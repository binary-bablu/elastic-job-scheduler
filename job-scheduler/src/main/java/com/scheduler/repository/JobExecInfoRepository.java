package com.scheduler.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scheduler.entity.JobExecInfo;

@Repository
public interface JobExecInfoRepository extends JpaRepository<JobExecInfo, Integer> {
   
}