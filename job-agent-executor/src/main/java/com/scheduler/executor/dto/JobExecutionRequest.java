package com.scheduler.executor.dto;

import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JobExecutionRequest {
	
	private Integer jobId;
    private String jobName;
    private String jobGroup;
    private String scriptPath;
    private Map<String, String> parameters;
    private LocalDateTime scheduledTime;
    private LocalDateTime triggerTime;
    private Integer executionId; // Unique execution ID
    private int retryCount = 0;
    private int maxRetries = 3;
    private int timeoutSeconds = 300;
    private LocalDateTime startTime;
    
    public JobExecutionRequest() {}
    
    public JobExecutionRequest(Integer jobId, String jobName, String jobGroup, String scriptPath) {
        this.jobId = jobId;
        this.jobName = jobName;
        this.jobGroup = jobGroup;
        this.scriptPath = scriptPath;
       // this.executionId = generateExecutionId();
        this.triggerTime = LocalDateTime.now();
    }
    
	public Integer getJobId() {
		return jobId;
	}
	public void setJobId(Integer jobId) {
		this.jobId = jobId;
	}
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	public String getJobGroup() {
		return jobGroup;
	}
	public void setJobGroup(String jobGroup) {
		this.jobGroup = jobGroup;
	}
	public String getScriptPath() {
		return scriptPath;
	}
	public void setScriptPath(String scriptPath) {
		this.scriptPath = scriptPath;
	}
	public Map<String, String> getParameters() {
		return parameters;
	}
	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}
	public LocalDateTime getScheduledTime() {
		return scheduledTime;
	}
	public void setScheduledTime(LocalDateTime scheduledTime) {
		this.scheduledTime = scheduledTime;
	}
	public LocalDateTime getTriggerTime() {
		return triggerTime;
	}
	public void setTriggerTime(LocalDateTime triggerTime) {
		this.triggerTime = triggerTime;
	}
	public Integer getExecutionId() {
		return executionId;
	}
	public void setExecutionId(Integer executionId) {
		this.executionId = executionId;
	}
	public int getRetryCount() {
		return retryCount;
	}
	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}
	public int getMaxRetries() {
		return maxRetries;
	}
	public void setMaxRetries(int maxRetries) {
		this.maxRetries = maxRetries;
	}
	public int getTimeoutSeconds() {
		return timeoutSeconds;
	}
	public void setTimeoutSeconds(int timeoutSeconds) {
		this.timeoutSeconds = timeoutSeconds;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}
	
}
