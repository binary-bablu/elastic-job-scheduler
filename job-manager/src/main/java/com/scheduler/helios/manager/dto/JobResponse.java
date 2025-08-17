package com.scheduler.helios.manager.dto;

import java.time.LocalDateTime;

public class JobResponse {
    private Integer id;
    private String jobName;
    private String cronExpression;
    private LocalDateTime nextFireTime;
    private LocalDateTime previousFireTime;
    private String status;

    public JobResponse() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getJobName() { return jobName; }
    public void setJobName(String jobName) { this.jobName = jobName; }

    
    public String getCronExpression() { return cronExpression; }
    public void setCronExpression(String cronExpression) { this.cronExpression = cronExpression; }

    
    public LocalDateTime getNextFireTime() { return nextFireTime; }
    public void setNextFireTime(LocalDateTime nextFireTime) { this.nextFireTime = nextFireTime; }

    public LocalDateTime getPreviousFireTime() { return previousFireTime; }
    public void setPreviousFireTime(LocalDateTime previousFireTime) { this.previousFireTime = previousFireTime; }

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

  }

