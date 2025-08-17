package com.scheduler.helios.manager.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scheduler.helios.manager.entity.JobExecInfo;

@Repository
public interface JobExecInfoRepository extends JpaRepository<JobExecInfo, Integer> {
   
}