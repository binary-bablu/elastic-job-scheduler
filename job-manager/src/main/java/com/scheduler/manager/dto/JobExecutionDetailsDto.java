package com.scheduler.manager.dto;

public class JobExecutionDetailsDto {
	
	private Integer jobId;
	
	private Integer executionId;
	
	private String execStartTime;
	
	private String execEndTime;
	
	private String status;
	
	private String isRetryAttempt;
	
	private Integer retryAttemptNumber;
	
	private String queuedStartTime;
	
	private String queuedEndTime;
	
	private String errorMessage;
	
	private String outputMessage;
	
	private String sentToDlq;

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

	public String getIsRetryAttempt() {
		return isRetryAttempt;
	}

	public void setIsRetryAttempt(String isRetryAttempt) {
		this.isRetryAttempt = isRetryAttempt;
	}

	public Integer getRetryAttemptNumber() {
		return retryAttemptNumber;
	}

	public void setRetryAttemptNumber(Integer retryAttemptNumber) {
		this.retryAttemptNumber = retryAttemptNumber;
	}

	public String getQueuedStartTime() {
		return queuedStartTime;
	}

	public void setQueuedStartTime(String queuedStartTime) {
		this.queuedStartTime = queuedStartTime;
	}

	public String getQueuedEndTime() {
		return queuedEndTime;
	}

	public void setQueuedEndTime(String queuedEndTime) {
		this.queuedEndTime = queuedEndTime;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getOutputMessage() {
		return outputMessage;
	}

	public void setOutputMessage(String outputMessage) {
		this.outputMessage = outputMessage;
	}

	public String getSentToDlq() {
		return sentToDlq;
	}

	public void setSentToDlq(String sentToDlq) {
		this.sentToDlq = sentToDlq;
	}

	public Integer getExecutionId() {
		return executionId;
	}

	public void setExecutionId(Integer executionId) {
		this.executionId = executionId;
	}
}
