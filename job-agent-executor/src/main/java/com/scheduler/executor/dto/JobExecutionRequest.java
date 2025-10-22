package com.scheduler.executor.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
    private Integer executionId; // Unique execution ID
    private int timeoutSeconds = 300;
    private LocalDateTime queuedTime;
    private int currentRetryCount = 0;
    private String isRetryAttempt;
	private List<RetryAttempt> retryHistory = new ArrayList<>();
	private String lastFailureReason;
	private int maxRetryAttempts;
	private int initialDelayMs;
	private String backOffStrategy;
	private int multiplier;
	
	private LocalDateTime startTime;
	private List<Integer> nonRetryableExitCodes = List.of(2, 126,-998,-999);
    
    public JobExecutionRequest() {}
    
    public JobExecutionRequest(Integer jobId, String jobName, String jobGroup, String scriptPath) {
        this.jobId = jobId;
        this.jobName = jobName;
        this.jobGroup = jobGroup;
        this.scriptPath = scriptPath;
    }
    
    public void recordRetryAttempt(String reason, String agentId) {
        RetryAttempt attempt = new RetryAttempt();
        attempt.setAttemptNumber(currentRetryCount);
        attempt.setAttemptTime(LocalDateTime.now());
        attempt.setFailureReason(reason);
        attempt.setAgentId(agentId);
        retryHistory.add(attempt);
        this.lastFailureReason = reason;
    }
    
    public boolean canRetry() {
    	return currentRetryCount < maxRetryAttempts;
    }
    
    public void incrementRetryCount() {
        this.currentRetryCount++;
    }
    
    public boolean isExitCodeRetryable(int exitCode) {
        return nonRetryableExitCodes == null || !nonRetryableExitCodes.contains(exitCode);
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
	
	public Integer getExecutionId() {
		return executionId;
	}
	public void setExecutionId(Integer executionId) {
		this.executionId = executionId;
	}
	
	public int getTimeoutSeconds() {
		return timeoutSeconds;
	}
	public void setTimeoutSeconds(int timeoutSeconds) {
		this.timeoutSeconds = timeoutSeconds;
	}
	
    /**
     * Calculate retry delay based on current attempt and strategy
     */
    public long calculateDelay(int retryAttempt) {
        long delay;
        
        switch (backOffStrategy) {
            case "FIXED":
                delay = initialDelayMs;
                break;
                
            case "LINEAR":
                delay = initialDelayMs * retryAttempt;
                break;
                
            case "EXPONENTIAL":
            default:
                delay = (long) (initialDelayMs * Math.pow(multiplier, retryAttempt - 1));
                break;
        }
        return delay;
    } 

    // Inner class for retry history
    public static class RetryAttempt {
        private int attemptNumber;
        private LocalDateTime attemptTime;
        private String failureReason;
        private String agentId;
        
        public int getAttemptNumber() { return attemptNumber; }
        public void setAttemptNumber(int attemptNumber) { this.attemptNumber = attemptNumber; }
        
        public LocalDateTime getAttemptTime() { return attemptTime; }
        public void setAttemptTime(LocalDateTime attemptTime) { this.attemptTime = attemptTime; }
        
        public String getFailureReason() { return failureReason; }
        public void setFailureReason(String failureReason) { this.failureReason = failureReason; }
        
        public String getAgentId() { return agentId; }
        public void setAgentId(String agentId) { this.agentId = agentId; }
    }

	public LocalDateTime getQueuedTime() {
		return queuedTime;
	}

	public void setQueuedTime(LocalDateTime queuedTime) {
		this.queuedTime = queuedTime;
	}

	public int getCurrentRetryCount() {
		return currentRetryCount;
	}

	public void setCurrentRetryCount(int currentRetryCount) {
		this.currentRetryCount = currentRetryCount;
	}

	public String getIsRetryAttempt() {
		return isRetryAttempt;
	}

	public void setIsRetryAttempt(String isRetryAttempt) {
		this.isRetryAttempt = isRetryAttempt;
	}

	public List<RetryAttempt> getRetryHistory() {
		return retryHistory;
	}

	public void setRetryHistory(List<RetryAttempt> retryHistory) {
		this.retryHistory = retryHistory;
	}

	public String getLastFailureReason() {
		return lastFailureReason;
	}

	public void setLastFailureReason(String lastFailureReason) {
		this.lastFailureReason = lastFailureReason;
	}

	public int getMaxRetryAttempts() {
		return maxRetryAttempts;
	}

	public void setMaxRetryAttempts(int maxRetryAttempts) {
		this.maxRetryAttempts = maxRetryAttempts;
	}

	public int getInitialDelayMs() {
		return initialDelayMs;
	}

	public void setInitialDelayMs(int initialDelayMs) {
		this.initialDelayMs = initialDelayMs;
	}

	public String getBackOffStrategy() {
		return backOffStrategy;
	}

	public void setBackOffStrategy(String backOffStrategy) {
		this.backOffStrategy = backOffStrategy;
	}

	public int getMultiplier() {
		return multiplier;
	}

	public void setMultiplier(int multiplier) {
		this.multiplier = multiplier;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}
}

