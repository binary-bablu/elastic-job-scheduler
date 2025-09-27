package com.scheduler.manager.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "job_info")
public class JobInfo {

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

    @OneToMany(mappedBy = "jobInfo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JobParameter> parameters = new ArrayList<>();

    // Add/remove helpers
    public void addParameter(String key, String value) {
        JobParameter param = new JobParameter();
        param.setParamKey(key);
        param.setParamValue(value);
        param.setJobInfo(this);
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
    
}
