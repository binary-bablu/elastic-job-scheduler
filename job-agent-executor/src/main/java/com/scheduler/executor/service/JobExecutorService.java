package com.scheduler.executor.service;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.scheduler.executor.dto.JobExecutionRequest;
import com.scheduler.executor.dto.JobExecutionResult;
import com.scheduler.executor.repository.JobExecInfoRepository;
import com.scheduler.executor.entity.JobExecInfo;

@Service
public class JobExecutorService {
	
private static final Logger logger = LoggerFactory.getLogger(JobExecutorService.class);
    
    @Value("${executor.agent.id}")
    private String agentId;
    
    @Autowired
    private JobExecInfoRepository jobExecInfoRepository;
    
    public JobExecutionResult executeJob(JobExecutionRequest request) {
    	
        JobExecutionResult result = new JobExecutionResult(
            request.getExecutionId(), 
            request.getJobId(), 
            agentId,
            request.getQueuedTime()
        );
        
        LocalDateTime startTime = LocalDateTime.now();
        result.setStartTime(startTime);
        
        logger.info("Agent {} executing job: {} ({})", agentId, request.getJobName(), request.getExecutionId());
        
        try {
        	
        	jobExecInfoRepository.save(createJobExecInfoEntity(request,"EXECUTING"));
        	
            // Build command
            List<String> command = buildCommand(request);
            
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true);
            
            // Set environment variables
            Map<String, String> env = processBuilder.environment();
            env.put("JOB_ID", request.getJobId().toString());
            env.put("EXECUTION_ID", request.getExecutionId().toString());
            env.put("AGENT_ID", agentId);
            
            // Start process
            Process process = processBuilder.start();
            
            // Read output
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                    logger.debug("Script output [{}]: {}", request.getExecutionId(), line);
                }
            }
            
            // Wait for completion with timeout
            boolean finished = process.waitFor(request.getTimeoutSeconds(), TimeUnit.SECONDS);
            
            if (!finished) {
                process.destroyForcibly();
                result.setSuccess(false);
                result.setErrorMessage("Script execution timed out after " + request.getTimeoutSeconds() + " seconds");
                result.setExitCode(-1);
            } else {
                int exitCode = process.exitValue();
                result.setExitCode(exitCode);
                result.setSuccess(exitCode == 0);
                result.setOutput(output.toString());
                
                if (exitCode != 0) {
                    result.setErrorMessage("Script failed with exit code: " + exitCode);
                }
            }
            
        } catch (IOException | InterruptedException e) {
          
        	logger.error("Error executing job: {} - {}", request.getExecutionId(), e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMessage("Execution error: " + e.getMessage());
            result.setExitCode(-2);
            
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
        }
        
        LocalDateTime endTime = LocalDateTime.now();
        result.setEndTime(endTime);
        result.setDurationMs(ChronoUnit.MILLIS.between(startTime, endTime));
        
        logger.info("Job execution completed: {} - Success: {} in {}ms", 
                   request.getExecutionId(), result.isSuccess(), result.getDurationMs());
        
        return result;
    }
    
    private List<String> buildCommand(JobExecutionRequest request) {
        List<String> command = new ArrayList<>();
        command.add("bash");
        command.add(request.getScriptPath());
        
        // Add parameters
        if (request.getParameters() != null) {
            for (Map.Entry<String, String> entry : request.getParameters().entrySet()) {
                command.add(entry.getValue());
            }
        }
        
        return command;
    }
    
    private JobExecInfo createJobExecInfoEntity(JobExecutionRequest jobExecRequest,String status) {
    	
    	JobExecInfo jobExecInfo = new JobExecInfo();
    	jobExecInfo.setJobId(jobExecRequest.getJobId());
    	jobExecInfo.setExecutionId(jobExecRequest.getExecutionId());
    	jobExecInfo.setStatus(status);
    	jobExecInfo.setExecutionStartTime(Timestamp.valueOf(LocalDateTime.now()));
    	jobExecInfo.setQueueTime(Timestamp.valueOf(jobExecRequest.getQueuedTime()));
    	
    	return jobExecInfo;
    }

}
