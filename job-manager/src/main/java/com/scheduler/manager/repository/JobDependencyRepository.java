package com.scheduler.manager.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scheduler.manager.entity.JobDependency;
import com.scheduler.manager.entity.JobInfo;

@Repository
public interface JobDependencyRepository extends JpaRepository<JobDependency, Integer> {
    List<JobDependency> findByParentJobAndIsActiveTrue(JobInfo parentJob);
    List<JobDependency> findByDependentJobAndIsActiveTrue(JobInfo dependentJob);
    boolean existsByParentJobAndDependentJobAndIsActiveTrue(JobInfo parentJob, JobInfo dependentJob);
}