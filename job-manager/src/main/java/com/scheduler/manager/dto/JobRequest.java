package com.scheduler.manager.dto;

import java.util.Map;

public class JobRequest {
	
    private String jobName;
    
    private String jobGroup;
    
    private String cronExpression;
    
    private String scriptPath;
    
    private Map<String, String> parameters;
    
    private String description;
    
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
    
}