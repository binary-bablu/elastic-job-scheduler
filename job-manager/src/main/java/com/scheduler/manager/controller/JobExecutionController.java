package com.scheduler.manager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scheduler.manager.dto.JobExecutionDetailsDto;
import com.scheduler.manager.dto.JobExecutionListDto;
import com.scheduler.manager.service.JobExecutionService;
import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@CrossOrigin(origins = "*")
public class JobExecutionController {
	
	@Autowired
	private JobExecutionService jobExecutionService;
	
	@GetMapping("/{jobId}/executions")
    public ResponseEntity<?> listExecutionsByJob(@PathVariable Integer jobId) {
    	
		List<JobExecutionListDto> jobExecutionList = jobExecutionService.listJobExecutions(jobId);
        return ResponseEntity.ok(jobExecutionList);
    }

	@GetMapping("/{jobId}/executions/{executionId}")
    public ResponseEntity<?> getExecutionById(@PathVariable Integer jobId,@PathVariable Integer executionId) {
    	
		JobExecutionDetailsDto jobExecutionDetail = jobExecutionService.getExecutionDetails(jobId,executionId);
        return ResponseEntity.ok(jobExecutionDetail);
    }
}
