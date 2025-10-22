package com.scheduler.manager.dto;

import java.util.Map;

public class JobRequest {
	
    private String jobName;
    
    private String jobGroup;
    
    private String cronExpression;
    
    private String scriptPath;
    
    private Map<String, String> parameters;
    
    private String description;
    
    private Integer timeout;//seconds
    
    // Per-job retry configuration
    private RetryConfig retryConfig;
    
    // Constructors
    public JobRequest() {}
    
    // Getters and Setters
    public String getJobName() { return jobName; }
    public void setJobName(String jobName) { this.jobName = jobName; }

    public String getJobGroup() { return jobGroup; }
    public void setJobGroup(String jobGroup) { this.jobGroup = jobGroup; }

    public String getCronExpression() { return cronExpression; }
    public void setCronExpression(String cronExpression) { this.cronExpression = cronExpression; }

    public String getScriptPath() { return scriptPath; }
    public void setScriptPath(String scriptPath) { this.scriptPath = scriptPath; }

    public Map<String, String> getParameters() { return parameters; }
    public void setParameters(Map<String, String> parameters) { this.parameters = parameters; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    /**
     * Calculate retry delay based on current attempt and strategy
     */
    public long calculateDelay(int retryAttempt) {
        long delay;
        
        switch (retryConfig.getBackOffStrategy()) {
            case "FIXED":
                delay = retryConfig.getInitialDelayMs();
                break;
                
            case "LINEAR":
                delay = retryConfig.getInitialDelayMs() * retryAttempt;
                break;
                
            case "EXPONENTIAL":
            default:
                delay = (long) (retryConfig.getInitialDelayMs() * Math.pow(retryConfig.getMultiplier(), retryAttempt - 1));
                break;
        }
        
        return delay;
    }

	public RetryConfig getRetryConfig() {
		return retryConfig;
	}

	public void setRetryConfig(RetryConfig retryConfig) {
		this.retryConfig = retryConfig;
	}

	public Integer getTimeout() {
		return timeout;
	}

	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}
}