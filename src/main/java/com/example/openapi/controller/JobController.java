package com.example.openapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
@Slf4j
public class JobController {

    private final JobLauncher jobLauncher;
    private final Job bookExportJob;

    @Operation(summary = "Start book export job manually", description = "Manually triggers the book export job to export all books to CSV file.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Job started successfully",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Error starting job",
                    content = @Content)
    })
    @PostMapping("/book-export/start")
    public ResponseEntity<Map<String, Object>> startBookExportJob() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();

            var jobExecution = jobLauncher.run(bookExportJob, jobParameters);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Book export job started successfully");
            response.put("jobExecutionId", jobExecution.getId());
            response.put("jobInstanceId", jobExecution.getJobInstance().getInstanceId());
            response.put("status", jobExecution.getStatus().toString());
            
            log.info("Book export job started manually with execution ID: {}", jobExecution.getId());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error starting book export job", e);
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Error starting job: " + e.getMessage());
            error.put("status", "error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}

