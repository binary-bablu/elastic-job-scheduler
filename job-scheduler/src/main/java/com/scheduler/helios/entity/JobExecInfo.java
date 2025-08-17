package com.scheduler.helios.entity;


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
	
    @Column(name="queue_time")
    private Timestamp queueTime;
	
    @Column(name="exec_finish_time")
    private Timestamp execFinishTime;
	
    // Constructors
    public JobExecInfo() {}

	public Integer getJobId() {
		return jobId;
	}

	public void setJobId(Integer jobId) {
		this.jobId = jobId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Timestamp getQueueTime() {
		return queueTime;
	}

	public void setQueueTime(Timestamp queueTime) {
		this.queueTime = queueTime;
	}

	public Timestamp getExecFinishTime() {
		return execFinishTime;
	}

	public void setExecFinishTime(Timestamp execFinishTime) {
		this.execFinishTime = execFinishTime;
	}

	public Integer getExecutionId() {
		return executionId;
	}

	public void setExecutionId(Integer executionId) {
		this.executionId = executionId;
	}

}
