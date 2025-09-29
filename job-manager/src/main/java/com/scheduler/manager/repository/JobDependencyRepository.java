package com.scheduler.manager.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scheduler.manager.entity.JobDependency;
import com.scheduler.manager.entity.JobScheduleDefinition;

@Repository
public interface JobDependencyRepository extends JpaRepository<JobDependency, Integer> {
    List<JobDependency> findByParentJobAndIsActiveTrue(JobScheduleDefinition parentJob);
    List<JobDependency> findByDependentJobAndIsActiveTrue(JobScheduleDefinition dependentJob);
    boolean existsByParentJobAndDependentJobAndIsActiveTrue(JobScheduleDefinition parentJob, JobScheduleDefinition dependentJob);
}