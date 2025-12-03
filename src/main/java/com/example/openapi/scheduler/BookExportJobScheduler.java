package com.example.openapi.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookExportJobScheduler {

    private final JobLauncher jobLauncher;
    private final Job bookExportJob;

    @Scheduled(cron = "0 0 1 * * ?") // Every day at 1:00 AM
    public void scheduleBookExportJob() {
        log.info("Starting scheduled book export job...");
        
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis())
                    .addString("trigger", "scheduled")
                    .toJobParameters();

            var jobExecution = jobLauncher.run(bookExportJob, jobParameters);
            
            log.info("Scheduled book export job started successfully. Execution ID: {}, Status: {}", 
                    jobExecution.getId(), 
                    jobExecution.getStatus());
        } catch (Exception e) {
            log.error("Error starting scheduled book export job", e);
        }
    }
}

