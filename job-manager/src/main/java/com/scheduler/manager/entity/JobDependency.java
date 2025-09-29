package com.scheduler.manager.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "job_dependencies")
public class JobDependency {
    
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "job_dependency_id")
    private Integer jobDependencyId;
	
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_job_id", nullable = false)
    private JobScheduleDefinition parentJob;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dependent_job_id", nullable = false)
    private JobScheduleDefinition dependentJob;
    
    @Column(name = "dependency_type", nullable = false)
    private String dependencyType;
    
    @Column(name = "condition_expression", length = 500)
    private String conditionExpression; // Optional condition for dependency
    
    @Column(name = "is_active")
    private Boolean isActive = true;

	public JobScheduleDefinition getParentJob() {
		return parentJob;
	}

	public void setParentJob(JobScheduleDefinition parentJob) {
		this.parentJob = parentJob;
	}

	public JobScheduleDefinition getDependentJob() {
		return dependentJob;
	}

	public void setDependentJob(JobScheduleDefinition dependentJob) {
		this.dependentJob = dependentJob;
	}

	public String getDependencyType() {
		return dependencyType;
	}

	public void setDependencyType(String dependencyType) {
		this.dependencyType = dependencyType;
	}

	public String getConditionExpression() {
		return conditionExpression;
	}

	public void setConditionExpression(String conditionExpression) {
		this.conditionExpression = conditionExpression;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public Integer getJobDependencyId() {
		return jobDependencyId;
	}

	public void setJobDependencyId(Integer jobDependencyId) {
		this.jobDependencyId = jobDependencyId;
	}
    
}
