package com.scheduler.manager.entity;


import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "job_exec_info")
public class JobExecInfo {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
    private Integer executionId;

	@Column(name="job_id")
    private Integer jobId;
	
	@NotBlank
    @Column(name="status")
    private String status;
	
	@Column(name="queued_start_time")
    private Timestamp queuedStartTime;
    
    @Column(name="queued_end_time")
    private Timestamp queuedEndTime;
    
    @Column(name="exec_start_time")
    private Timestamp execStartTime;
    
    @Column(name="exec_end_time")
    private Timestamp execEndTime;
	
    // Constructors
    public JobExecInfo() {}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	
	public Integer getExecutionId() {
		return executionId;
	}

	public void setExecutionId(Integer executionId) {
		this.executionId = executionId;
	}
	
	public Timestamp getQueuedStartTime() {
		return queuedStartTime;
	}

	public void setQueuedStartTime(Timestamp queuedStartTime) {
		this.queuedStartTime = queuedStartTime;
	}

	public Timestamp getQueuedEndTime() {
		return queuedEndTime;
	}

	public void setQueuedEndTime(Timestamp queuedEndTime) {
		this.queuedEndTime = queuedEndTime;
	}

	public Timestamp getExecStartTime() {
		return execStartTime;
	}

	public void setExecStartTime(Timestamp execStartTime) {
		this.execStartTime = execStartTime;
	}

	public Timestamp getExecEndTime() {
		return execEndTime;
	}

	public void setExecEndTime(Timestamp execEndTime) {
		this.execEndTime = execEndTime;
	}

	public Integer getJobId() {
		return jobId;
	}

	public void setJobId(Integer jobId) {
		this.jobId = jobId;
	}

}
