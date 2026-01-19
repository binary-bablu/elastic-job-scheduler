package com.scheduler.manager.dto;

public class JobsDashBoardDto {

	private Integer id;
	
	private Integer jobId;
	
	private String jobName;
	
	private String jobDescription;
	
	private String schedule;
	
	private String ownerEmail;
	
	private Integer successCount;
	
	private Integer errorCount;
	
	private String lastSuccessDt;
	
	private String lastErrorDt;
	
	private String status;

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

	public String getJobDescription() {
		return jobDescription;
	}

	public void setJobDescription(String jobDescription) {
		this.jobDescription = jobDescription;
	}

	public String getSchedule() {
		return schedule;
	}

	public void setSchedule(String schedule) {
		this.schedule = schedule;
	}

	public String getOwnerEmail() {
		return ownerEmail;
	}

	public void setOwnerEmail(String ownerEmail) {
		this.ownerEmail = ownerEmail;
	}

	public Integer getSuccessCount() {
		return successCount;
	}

	public void setSuccessCount(Integer successCount) {
		this.successCount = successCount;
	}

	public Integer getErrorCount() {
		return errorCount;
	}

	public void setErrorCount(Integer errorCount) {
		this.errorCount = errorCount;
	}

	public String getLastSuccessDt() {
		return lastSuccessDt;
	}

	public void setLastSuccessDt(String lastSuccessDt) {
		this.lastSuccessDt = lastSuccessDt;
	}

	public String getLastErrorDt() {
		return lastErrorDt;
	}

	public void setLastErrorDt(String lastErrorDt) {
		this.lastErrorDt = lastErrorDt;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
}
