package com.scheduler.manager.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scheduler.manager.entity.JobDependency;
import com.scheduler.manager.repository.JobDependencyRepository;
import com.scheduler.manager.repository.JobInfoRepository;

@Service
public class JobDependencyCrudOpService {

	private static final Logger logger = LoggerFactory.getLogger(JobDependencyCrudOpService.class);
	
	@Autowired
    private JobInfoRepository jobInfoRepository;
	
	@Autowired
	private JobDependencyRepository jobDependencyRepository;
	
	/**
	 * Create the dependency
	 * @param parentJobId
	 * @param dependentJobId
	 * @param dependencyType
	 * @return
	 */
	@Transactional
    public JobDependency createDependency(Integer parentJobId, Integer dependentJobId, 
                                         String dependencyType) {
                                         
        // Validate jobs exist
        validateJobsExist(parentJobId, dependentJobId);
        
        // Check for circular dependencies
        if (hasCircularDependency(parentJobId, dependentJobId)) {
            throw new IllegalArgumentException("Creating this dependency would create a circular dependency");
        }
        
        // Check if dependency already exists
        if (jobDependencyRepository.existsByParentJobIdAndDependentJobId(parentJobId, dependentJobId)) {
            throw new IllegalArgumentException("Dependency already exists between these jobs");
        }
        
        // Conditional execution is not yet supported
        
        JobDependency dependency = new JobDependency();
        dependency.setParentJobId(parentJobId);
        dependency.setDependentJobId(dependentJobId);
        dependency.setDependencyType(dependencyType);
        dependency.setEnabled(true);
        
        dependency = jobDependencyRepository.save(dependency);
        logger.info("Created dependency: parent={}, dependent={}, type={}", 
                   parentJobId, dependentJobId, dependencyType);
        
        return dependency;
    }
	
	/**
	 * Check the specified configuration has circular dependency
	 * @param parentJobId
	 * @param dependentJobId
	 * @return
	 */
    private boolean hasCircularDependency(Integer parentJobId, Integer dependentJobId) {
        return hasCircularDependencyRecursive(dependentJobId, parentJobId, new ArrayList<>());
    }
    
    /**
     * Check if its recursive circular dependency 
     * @param currentJobId
     * @param targetJobId
     * @param visited
     * @return
     */
    private boolean hasCircularDependencyRecursive(Integer currentJobId, Integer targetJobId, List<Integer> visited) {
       
    	if (currentJobId.equals(targetJobId)) {
            return true;
        }
        
        if (visited.contains(currentJobId)) {
            return false;
        }
        
        visited.add(currentJobId);
        
        List<JobDependency> dependencies = jobDependencyRepository.findDependentJobs(currentJobId);
        for (JobDependency dep : dependencies) {
            if (hasCircularDependencyRecursive(dep.getDependentJobId(), targetJobId, visited)) {
                return true;
            }
        }
        
        return false;
    }
	    
    /**
     * Validate that both jobs exist
     */
    private void validateJobsExist(Integer parentJobId, Integer dependentJobId) {
        if (!jobInfoRepository.existsById(parentJobId)) {
            throw new IllegalArgumentException("Parent job not found: " + parentJobId);
        }
        if (!jobInfoRepository.existsById(dependentJobId)) {
            throw new IllegalArgumentException("Dependent job not found: " + dependentJobId);
        }
    }
	    
    /**
     * Get all Dependents jobs for a given job
     */
    public List<JobDependency> getDependentJobs(Integer jobId) {
        return jobDependencyRepository.findDependentJobs(jobId);
    }
    
    /**
     * Get parent jobs for a job
     */
    public List<JobDependency> getParentJobsFor(Integer jobId) {
        return jobDependencyRepository.findParentJobs(jobId);
    }
	    
    /**
     * Delete dependency
     */
    @Transactional
    public void deleteDependency(Integer dependencyId) {
    	jobDependencyRepository.deleteById(dependencyId);
        logger.info("Deleted dependency: {}", dependencyId);
    }
	    
    /**
     * Enable/disable dependency
     */
    @Transactional
    public void toggleDependency(Integer dependencyId, boolean enabled) {
        Optional<JobDependency> optDep = jobDependencyRepository.findById(dependencyId);
        if (optDep.isPresent()) {
            JobDependency dep = optDep.get();
            dep.setEnabled(enabled);
            jobDependencyRepository.save(dep);
            logger.info("Dependency {} {} between parent {} and dependent {} ", dependencyId, 
            		enabled ? "enabled" : "disabled",dep.getParentJobId(),
            		dep.getDependentJobId());
        }
    }
}
