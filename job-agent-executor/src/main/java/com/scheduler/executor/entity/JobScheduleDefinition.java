package com.scheduler.executor.entity;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.scheduler.utils.IntegerListToStringConverter;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "job_schedule_definitions")
public class JobScheduleDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name="job_name")
    private String jobName;
    
    @Column(name="job_group")
    private String jobGroup;
    
    @Column(name="description")
    private String description;
    
    @Column(name="cron_expression")
    private String cronExpression;
    
    @Column(name="script_path")
    private String scriptPath;
    
    @Column(name="created_at")
    private LocalDateTime createdAt;
    
    @Column(name="updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name="status")
    private String status;
    
    @Column(name="retry_max_attempts")
    private Integer retryMaxAttempts;
	
	@Column(name="retry_initial_delay_ms")
    private Integer retryInitialDelayInMs;
	
	@Column(name="retry_strategy")
    private String retryStrategy;
	
	@Column(name="retry_multiplier")
    private Integer retryMultiplier;
	
	@Column(name="timeout")
	private Integer timeout;
	
	@Column(name="non_retryable_exit_codes")
	@Convert(converter = IntegerListToStringConverter.class)
	private List<Integer> nonRetryableExitCodes;

    @OneToMany(mappedBy = "jobScheduleDefinition", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JobParameter> parameters = new ArrayList<>();
    
    @Column(name="owner_email")
    private String ownerEmail;
    
    @Column(name="success_count")
	private Integer successCount;
	
	@Column(name="error_count")
	private Integer errorCount;
	
	@Column(name="last_success_date")
	private Timestamp lastSuccessDt;
	
	@Column(name="last_error_date")
	private Timestamp lastErrorDt;
   
    // Add/remove helpers
    public void addParameter(String key, String value) {
        JobParameter param = new JobParameter();
        param.setParamKey(key);
        param.setParamValue(value);
        param.setJobScheduleDefinition(this);
        this.parameters.add(param);
    }

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCronExpression() {
		return cronExpression;
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public Map<String, String> getParameters() {
		
		Map<String,String> paramMap = new HashMap<String, String>();
		for(JobParameter jobParam : this.parameters) {
			paramMap.put(jobParam.getParamKey(), jobParam.getParamValue());
		}
		return paramMap;
	}

	public String getScriptPath() {
		return scriptPath;
	}

	public void setScriptPath(String scriptPath) {
		this.scriptPath = scriptPath;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getRetryMaxAttempts() {
		return retryMaxAttempts;
	}

	public void setRetryMaxAttempts(Integer retryMaxAttempts) {
		this.retryMaxAttempts = retryMaxAttempts;
	}

	public Integer getRetryInitialDelayInMs() {
		return retryInitialDelayInMs;
	}

	public void setRetryInitialDelayInMs(Integer retryInitialDelayInMs) {
		this.retryInitialDelayInMs = retryInitialDelayInMs;
	}

	public String getRetryStrategy() {
		return retryStrategy;
	}

	public void setRetryStrategy(String retryStrategy) {
		this.retryStrategy = retryStrategy;
	}

	public Integer getRetryMultiplier() {
		return retryMultiplier;
	}

	public void setRetryMultiplier(Integer retryMultiplier) {
		this.retryMultiplier = retryMultiplier;
	}

	public void setParameters(List<JobParameter> parameters) {
		this.parameters = parameters;
	}

	public Integer getTimeout() {
		return timeout;
	}

	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}

	public List<Integer> getNonRetryableExitCodes() {
		return nonRetryableExitCodes;
	}

	public void setNonRetryableExitCodes(List<Integer> nonRetryableExitCodes) {
		this.nonRetryableExitCodes = nonRetryableExitCodes;
	}

	public String getOwnerEmail() {
		return ownerEmail;
	}

	public void setOwnerEmail(String ownerEmail) {
		this.ownerEmail = ownerEmail;
	}

	public Integer getSuccessCount() {
		if(successCount == null)
			successCount = 0;
		return successCount;
	}

	public void setSuccessCount(Integer successCount) {
		this.successCount = successCount;
	}

	public Integer getErrorCount() {
		if(errorCount == null)
			errorCount =0;
		return errorCount;
	}

	public void setErrorCount(Integer errorCount) {
		this.errorCount = errorCount;
	}

	public Timestamp getLastSuccessDt() {
		return lastSuccessDt;
	}

	public void setLastSuccessDt(Timestamp timestamp) {
		this.lastSuccessDt = timestamp;
	}

	public Timestamp getLastErrorDt() {
		return lastErrorDt;
	}

	public void setLastErrorDt(Timestamp lastErrorDt) {
		this.lastErrorDt = lastErrorDt;
	}
	
}
