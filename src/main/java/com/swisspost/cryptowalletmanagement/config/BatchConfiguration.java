package com.swisspost.cryptowalletmanagement.config;

import com.swisspost.cryptowalletmanagement.repository.AssetRepository;
import com.swisspost.cryptowalletmanagement.repository.entity.AssetEntity;
import com.swisspost.cryptowalletmanagement.service.batch.ParallelAssetItemWriter;
import com.swisspost.cryptowalletmanagement.service.batch.ParallelBatchProcessor;
import com.swisspost.cryptowalletmanagement.service.batch.RepositoryAssetItemReader;
import com.swisspost.cryptowalletmanagement.service.pricing.PricingApiService;
import com.swisspost.cryptowalletmanagement.service.pricing.PricingProviderService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import jakarta.persistence.EntityManagerFactory;

@Configuration
public class BatchConfiguration {

    private final AssetRepository assetRepository;
    private final PricingProviderService pricingProviderService;

    public BatchConfiguration(AssetRepository assetRepository,
                              PricingProviderService pricingProviderService) {
        this.assetRepository = assetRepository;
        this.pricingProviderService = pricingProviderService;
    }

    @Bean
    public ItemReader<AssetEntity> reader() {
        return new RepositoryAssetItemReader(assetRepository); // Page size 100
    }

    @Bean
    public ParallelBatchProcessor processor() {
        return new ParallelBatchProcessor(pricingProviderService);
    }

    @Bean
    public ItemWriter<AssetEntity> writer(EntityManagerFactory entityManagerFactory, ParallelBatchProcessor processor) {
        return new ParallelAssetItemWriter(entityManagerFactory, processor);
    }

    @Bean
    public Job assetsProcessingJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new JobBuilder("assetsProcessingJob", jobRepository)
                .start(createStep(jobRepository, transactionManager))
                .build();
    }

    @Bean
    public Step createStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("assetsProcessingJob", jobRepository)
                .<AssetEntity, AssetEntity>chunk(3, transactionManager)
                .allowStartIfComplete(true)
                .reader(reader())
                .processor(processor())
                .writer(writer(null, processor()))
                .build();
    }
}