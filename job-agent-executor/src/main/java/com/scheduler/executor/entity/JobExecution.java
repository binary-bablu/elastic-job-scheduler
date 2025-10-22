package com.scheduler.executor.entity;


import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "job_executions")
public class JobExecution {
	
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
    
    @Column(name="error_message")
    private String errorMessage;
    
    @Column(name="retry_attempt_number")
    private int retryAttemptNumber;
    
    @Column(name="is_retry_attempt")
    private String isRetryAttempt;//values YES Or NO
    
    @Column(name="output_message")
    private String outputMessage;
    
    @Column(name="sent_to_dlq")
    private String sentToDlq;
    
    public JobExecution() {}

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

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public int getRetryAttemptNumber() {
		return retryAttemptNumber;
	}

	public void setRetryAttemptNumber(int retryAttemptNumber) {
		this.retryAttemptNumber = retryAttemptNumber;
	}

	public String getIsRetryAttempt() {
		return isRetryAttempt;
	}

	public void setIsRetryAttempt(String isRetryAttempt) {
		this.isRetryAttempt = isRetryAttempt;
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
}
