package com.scheduler.manager.dto;

public class JobExecutionListDto {
	
	private Integer jobId;
	
	private Integer executionId;
	
	private String execStartTime;
	
	private String execEndTime;
	
	private String status;
	
	private String isRetryAttempt;
	
	private Integer retryAttemptNumber;
	
	public JobExecutionListDto() {
		
	}

	public Integer getJobId() {
		return jobId;
	}

	public void setJobId(Integer jobId) {
		this.jobId = jobId;
	}

	public String getExecStartTime() {
		return execStartTime;
	}

	public void setExecStartTime(String execStartTime) {
		this.execStartTime = execStartTime;
	}

	public String getExecEndTime() {
		return execEndTime;
	}

	public void setExecEndTime(String execEndTime) {
		this.execEndTime = execEndTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String isRetryAttempt() {
		return isRetryAttempt;
	}

	public void setRetryAttempt(String isRetryAttempt) {
		this.isRetryAttempt = isRetryAttempt;
	}

	public Integer getRetryAttemptNumber() {
		return retryAttemptNumber;
	}

	public void setRetryAttemptNumber(Integer retryAttemptNumber) {
		this.retryAttemptNumber = retryAttemptNumber;
	}

	public Integer getExecutionId() {
		return executionId;
	}

	public void setExecutionId(Integer executionId) {
		this.executionId = executionId;
	}

	public String getIsRetryAttempt() {
		return isRetryAttempt;
	}

	public void setIsRetryAttempt(String isRetryAttempt) {
		this.isRetryAttempt = isRetryAttempt;
	}
}
