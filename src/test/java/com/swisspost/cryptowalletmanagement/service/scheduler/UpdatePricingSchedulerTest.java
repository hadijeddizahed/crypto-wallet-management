package com.swisspost.cryptowalletmanagement.service.scheduler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdatePricingSchedulerTest {

    @Mock
    private JobLauncher jobLauncher;

    @Mock
    private Job assetsProcessingJob;

    @Mock
    private JobExecution jobExecution;

    @InjectMocks
    private UpdatePricingScheduler scheduler;


    @Test
    void shouldRunJob_whenExecutesJobWithParameters() throws Exception {
        // Given
        when(jobLauncher.run(any(Job.class), any(JobParameters.class))).thenReturn(jobExecution);
        // When
        scheduler.runJob();
        // Then
        verify(jobLauncher).run(eq(assetsProcessingJob), any(JobParameters.class));
    }

    @Test
    void shouldRunJob_WhenIncludesJobIdInParameters() throws Exception {
        when(jobLauncher.run(any(Job.class), any(JobParameters.class))).thenReturn(jobExecution);
        scheduler.runJob();
        verify(jobLauncher).run(eq(assetsProcessingJob), any(JobParameters.class));
    }
}