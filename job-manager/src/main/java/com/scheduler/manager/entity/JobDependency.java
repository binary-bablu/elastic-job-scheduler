package com.scheduler.manager.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "job_dependencies")
public class JobDependency {
    
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
    private Integer jobDependencyId;
	
	@Column(name = "parent_job_id")
    private Integer parentJobId;
    
	@Column(name = "dependent_job_id")
    private Integer dependentJobId;
    
    @Column(name = "dependency_type")
    private String dependencyType;
    
    @Column(name = "enabled")
    private Boolean enabled = true;

	public Integer getParentJobId() {
		return parentJobId;
	}

	public void setParentJobId(Integer parentJobId) {
		this.parentJobId = parentJobId;
	}

	public Integer getDependentJobId() {
		return dependentJobId;
	}

	public void setDependentJobId(Integer dependentJobId) {
		this.dependentJobId = dependentJobId;
	}

	public String getDependencyType() {
		return dependencyType;
	}

	public void setDependencyType(String dependencyType) {
		this.dependencyType = dependencyType;
	}
	
	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public Integer getJobDependencyId() {
		return jobDependencyId;
	}

	public void setJobDependencyId(Integer jobDependencyId) {
		this.jobDependencyId = jobDependencyId;
	}
    
}
