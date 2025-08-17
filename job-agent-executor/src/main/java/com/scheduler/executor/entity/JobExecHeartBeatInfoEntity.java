package com.scheduler.executor.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "job_execution_heartbeat_info")
public class JobExecHeartBeatInfoEntity {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
    private Integer id;
	
	@Column(name="last_heart_beat")
    private String lastHeartBeat;

	@Column(name="max_concurrent_jobs")
    private Integer maxConcurrentJobs;
	
    @Column(name="current_active_jobs")
    private Integer currentActiveJobs;
	
    @Column(name="status")
    private String status;
	
    @Column(name="agent_id")
    private String agentId;
	
    // Constructors
    public JobExecHeartBeatInfoEntity() {}

	public String getLastHeartBeat() {
		return lastHeartBeat;
	}

	public void setLastHeartBeat(String lastHeartBeat) {
		this.lastHeartBeat = lastHeartBeat;
	}

	public Integer getMaxConcurrentJobs() {
		return maxConcurrentJobs;
	}

	public void setMaxConcurrentJobs(Integer maxConcurrentJobs) {
		this.maxConcurrentJobs = maxConcurrentJobs;
	}

	public Integer getCurrentActiveJobs() {
		return currentActiveJobs;
	}

	public void setCurrentActiveJobs(Integer currentActiveJobs) {
		this.currentActiveJobs = currentActiveJobs;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAgentId() {
		return agentId;
	}

	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}
}
