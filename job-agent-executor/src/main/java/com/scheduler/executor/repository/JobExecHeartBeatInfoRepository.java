package com.scheduler.executor.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scheduler.executor.entity.JobExecHeartBeatInfo;

@Repository
public interface JobExecHeartBeatInfoRepository extends JpaRepository<JobExecHeartBeatInfo, Integer> {
   
}