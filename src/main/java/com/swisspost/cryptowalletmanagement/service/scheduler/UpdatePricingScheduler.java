package com.swisspost.cryptowalletmanagement.service.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UpdatePricingScheduler {

    private final JobLauncher jobLauncher;
    private final Job assetsProcessingJob;

    public UpdatePricingScheduler(JobLauncher jobLauncher,
                                  @Qualifier("assetsProcessingJob") Job assetsProcessingJob) {
        this.jobLauncher = jobLauncher;
        this.assetsProcessingJob = assetsProcessingJob;
    }

    // @Scheduled(cron = "${app.pricing.update.cron:*/10 * * * * *}") //every 10 seconds
    @Scheduled(cron = "0 */10 * * * *") // every 10 min
    public void runJob() throws Exception {
        JobParameters params = new JobParametersBuilder()
                .addString("JobID", String.valueOf(System.currentTimeMillis()))
                .toJobParameters();
        jobLauncher.run(assetsProcessingJob, params);
    }
}