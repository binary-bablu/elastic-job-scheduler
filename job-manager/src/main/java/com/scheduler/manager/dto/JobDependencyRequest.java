package com.scheduler.manager.dto;

public class JobDependencyRequest {
	
	private Integer parentJobId;
	
	private Integer dependentJobId;
	
	private String dependencyType;

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
	
}
