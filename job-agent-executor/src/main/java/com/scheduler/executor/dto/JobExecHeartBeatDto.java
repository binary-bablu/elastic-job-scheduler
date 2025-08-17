package com.scheduler.executor.dto;

public class JobExecHeartBeatDto {
	
    private Integer lastHeartBeat;

    private Integer maxConcurrentJobs;
	
    private String currentActiveJobs;
	
    private String status;
	
    private String agentId;

	public Integer getLastHeartBeat() {
		return lastHeartBeat;
	}

	public void setLastHeartBeat(Integer lastHeartBeat) {
		this.lastHeartBeat = lastHeartBeat;
	}

	public Integer getMaxConcurrentJobs() {
		return maxConcurrentJobs;
	}

	public void setMaxConcurrentJobs(Integer maxConcurrentJobs) {
		this.maxConcurrentJobs = maxConcurrentJobs;
	}

	public String getCurrentActiveJobs() {
		return currentActiveJobs;
	}

	public void setCurrentActiveJobs(String currentActiveJobs) {
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
