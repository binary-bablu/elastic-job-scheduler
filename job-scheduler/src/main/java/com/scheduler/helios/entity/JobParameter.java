package com.scheduler.helios.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "job_parameters")
public class JobParameter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String paramKey;
    private String paramValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_info_id")
    private JobScheduleDefinition jobScheduleDefinition;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getParamKey() {
		return paramKey;
	}

	public void setParamKey(String paramKey) {
		this.paramKey = paramKey;
	}

	public String getParamValue() {
		return paramValue;
	}

	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}

	public JobScheduleDefinition getJobScheduleDefinition() {
		return jobScheduleDefinition;
	}

	public void setJobScheduleDefinition(JobScheduleDefinition jobScheduleDefinition) {
		this.jobScheduleDefinition = jobScheduleDefinition;
	}
    
 }

