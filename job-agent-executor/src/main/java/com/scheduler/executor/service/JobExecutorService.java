package com.scheduler.executor.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scheduler.executor.dto.JobExecutionRequest;
import com.scheduler.executor.dto.JobExecutionResult;

@Service
public class JobExecutorService {
	
private static final Logger logger = LoggerFactory.getLogger(JobExecutorService.class);
    
    @Value("${executor.agent.id}")
    private String agentId;
    
    @Transactional
    public JobExecutionResult executeJob(JobExecutionRequest request) {
    	
        JobExecutionResult result = new JobExecutionResult(
            request.getExecutionId(), 
            request.getJobId(), 
            agentId
        );
       
        result.setStartTime(request.getStartTime());
        
        logger.info("Agent {} executing job: {} ({})", agentId, request.getJobName(), request.getExecutionId());
        
        try {
        	
            // Build command
            List<String> command = buildCommand(request);
            
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true);
            
            // Set environment variables
            Map<String, String> environment = processBuilder.environment();
            environment.put("JOB_ID", String.valueOf(request.getJobId()));
            environment.put("EXECUTION_ID", String.valueOf(request.getExecutionId()));
            environment.put("AGENT_ID", String.valueOf(agentId));
            
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
                logger.error(" Job {} with execution id : {} - {} terminated due to Timing Out",request.getJobId(),
                		request.getExecutionId());
            } else {
                int exitCode = process.exitValue();
                result.setExitCode(exitCode);
                result.setSuccess(exitCode == 0);
                result.setOutput(output.toString());
                
                if (exitCode != 0) {
                    result.setErrorMessage("Script failed with exit code: " + exitCode);
                }else {
                	 logger.info("Job {} with execution id completed: {} - Success: {} in {}ms", 
                             request.getJobId(),request.getExecutionId(), result.isSuccess(), result.getDurationMs());
                }
            }
            
        } catch (IOException | InterruptedException e) {
          
        	logger.error("Error executing job {} with execution id : {} - {}",request.getJobId(), request.getExecutionId(),
        			e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMessage("Execution error: " + e.getMessage());
            result.setExitCode(-12);
            
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
        }
        
        LocalDateTime endTime = LocalDateTime.now();
        result.setEndTime(endTime);
        result.setDurationMs(ChronoUnit.MILLIS.between(result.getStartTime(), endTime));
        
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
}
