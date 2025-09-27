package com.scheduler.helios.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JobExecutionResult {
	
	private Integer executionId;
    private Integer jobId;
    private String agentId;
    private boolean success;
    private int exitCode;
    private String output;
    private String errorMessage;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private long durationMs;
    
    public JobExecutionResult() {}
    
    public JobExecutionResult(Integer executionId, Integer jobId, String agentId) {
        this.executionId = executionId;
        this.jobId = jobId;
        this.agentId = agentId;
        this.startTime = LocalDateTime.now();
    }
    
	public Integer getExecutionId() {
		return executionId;
	}
	public void setExecutionId(Integer executionId) {
		this.executionId = executionId;
	}
	public Integer getJobId() {
		return jobId;
	}
	public void setJobId(Integer jobId) {
		this.jobId = jobId;
	}
	public String getAgentId() {
		return agentId;
	}
	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public int getExitCode() {
		return exitCode;
	}
	public void setExitCode(int exitCode) {
		this.exitCode = exitCode;
	}
	public String getOutput() {
		return output;
	}
	public void setOutput(String output) {
		this.output = output;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public LocalDateTime getStartTime() {
		return startTime;
	}
	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}
	public LocalDateTime getEndTime() {
		return endTime;
	}
	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}
	public long getDurationMs() {
		return durationMs;
	}
	public void setDurationMs(long durationMs) {
		this.durationMs = durationMs;
	}
}
