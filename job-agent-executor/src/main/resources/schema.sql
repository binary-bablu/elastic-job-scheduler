CREATE TABLE job_execution_heartbeat_info (
    id BIGINT AUTO_INCREMENT,
	last_heart_beat VARCHAR(500),
	max_concurrent_jobs INTEGER,
	current_active_jobs INTEGER,
	status VARCHAR(100),
	agent_id VARCHAR(100) PRIMARY KEY
   
);