CREATE TABLE job_info (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    job_name VARCHAR(100) NOT NULL,
    cron_expression VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    description VARCHAR(200) NOT NULL,
    script_path VARCHAR(500) NOT NULL
);

CREATE TABLE job_exec_info (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    job_info_id BIGINT,
    execution_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    queue_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20),
    error_message TEXT
);

CREATE TABLE job_parameters (
    id SERIAL PRIMARY KEY,
    job_info_id BIGINT NOT NULL,
    param_key VARCHAR(100) NOT NULL,
    param_value TEXT,
    FOREIGN KEY (job_info_id) REFERENCES job_info(id) ON DELETE CASCADE
);
