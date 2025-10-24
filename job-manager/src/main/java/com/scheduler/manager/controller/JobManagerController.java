package com.scheduler.manager.controller;

import java.util.Map;

import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scheduler.manager.dto.JobRequest;
import com.scheduler.manager.dto.JobResponse;
import com.scheduler.manager.exception.JobAlreadyExistsException;
import com.scheduler.manager.exception.JobNotFoundException;
import com.scheduler.manager.service.JobSchedulerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/jobs")
@CrossOrigin(origins = "*")

public class JobManagerController {
	
	@Autowired
    private JobSchedulerService jobSchedulerService;

    @PostMapping
    public ResponseEntity<?> createJob(@Valid @RequestBody JobRequest jobRequest) {
      
    	try {
            JobResponse response = jobSchedulerService.createJob(jobRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (JobAlreadyExistsException | SchedulerException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create job: " + e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{jobId}")
    public ResponseEntity<?> updateJob(@PathVariable Integer jobId, @Valid @RequestBody JobRequest jobRequest) {
       
    	try {
            JobResponse response = jobSchedulerService.updateJob(jobId, jobRequest);
            return ResponseEntity.ok(response);
        } catch (JobNotFoundException | SchedulerException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update job: " + e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{jobId}")
    public ResponseEntity<?> deleteJob(@PathVariable Integer jobId) {
      
    	try {
            jobSchedulerService.deleteJob(jobId);
            return ResponseEntity.ok(Map.of("message", "Job deleted successfully"));
        } catch (JobNotFoundException | SchedulerException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete job: " + e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<?> getJob(@PathVariable Integer jobId) {
       
    	try {
            JobResponse response = jobSchedulerService.getJob(jobId);
            return ResponseEntity.ok(response);
        } catch (JobNotFoundException | SchedulerException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get job: " + e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{jobId}/pause")
    public ResponseEntity<?> pauseJob(@PathVariable Integer jobId) {
        
    	try {
            jobSchedulerService.pauseJob(jobId);
            return ResponseEntity.ok(Map.of("message", "Job paused successfully"));
        } catch (JobNotFoundException | SchedulerException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to pause job: " + e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{jobId}/resume")
    public ResponseEntity<?> resumeJob(@PathVariable Integer jobId) {
      
    	try {
            jobSchedulerService.resumeJob(jobId);
            return ResponseEntity.ok(Map.of("message", "Job resumed successfully"));
        } catch (JobNotFoundException | SchedulerException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to resume job: " + e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/{jobId}/inactivate")
    public ResponseEntity<?> inactivate(@PathVariable Integer jobId) {
      
    	try {
            jobSchedulerService.inactivateJob(jobId);
            return ResponseEntity.ok(Map.of("message", "Job Inactivated successfully"));
        } catch (JobNotFoundException | SchedulerException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to Inactivate job: " + e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/{jobId}/activate")
    public ResponseEntity<?> activate(@PathVariable Integer jobId) {
      
    	try {
            jobSchedulerService.activateJob(jobId);
            return ResponseEntity.ok(Map.of("message", "Job Activated successfully"));
        } catch (JobNotFoundException | SchedulerException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to Activate job: " + e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{jobId}/invoke_now")
    public ResponseEntity<?> triggerJob(@PathVariable Integer jobId) {
       
    	try {
            jobSchedulerService.triggerJob(jobId);
            return ResponseEntity.ok(Map.of("message", "Job triggered successfully"));
        } catch (JobNotFoundException | SchedulerException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to trigger job: " + e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
