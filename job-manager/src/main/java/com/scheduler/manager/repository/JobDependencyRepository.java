package com.scheduler.manager.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.scheduler.manager.entity.JobDependency;

@Repository
public interface JobDependencyRepository extends JpaRepository<JobDependency, Integer> {
    
    List<JobDependency> findByParentJobIdAndEnabledTrue(Integer parentJobId);
    
    List<JobDependency> findByDependentJobIdAndEnabledTrue(Integer dependentJobId);
    
    @Query("SELECT jd FROM JobDependency jd WHERE jd.parentJobId = :jobId AND jd.enabled = true")
    List<JobDependency> findDependentJobs(@Param("jobId") Integer jobId);
    
    @Query("SELECT jd FROM JobDependency jd WHERE jd.dependentJobId = :jobId AND jd.enabled = true")
    List<JobDependency> findParentJobs(@Param("jobId") Integer jobId);
    
    boolean existsByParentJobIdAndDependentJobId(Integer parentJobId, Integer dependentJobId);
    
}