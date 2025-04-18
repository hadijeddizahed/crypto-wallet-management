package com.swisspost.cryptowalletmanagement.service.batch;

import com.swisspost.cryptowalletmanagement.repository.entity.AssetDetailEntity;
import com.swisspost.cryptowalletmanagement.repository.entity.AssetEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

@Slf4j
public class ParallelAssetItemWriter implements ItemWriter<AssetDetailEntity> {

    private final JpaItemWriter<AssetDetailEntity> jpaItemWriter;
    private final ParallelBatchProcessor batchProcessor;

    public ParallelAssetItemWriter(EntityManagerFactory entityManagerFactory, ParallelBatchProcessor batchProcessor) {
        this.jpaItemWriter = new JpaItemWriter<>();
        this.jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
        this.batchProcessor = batchProcessor;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void write(Chunk<? extends AssetDetailEntity> chunk) throws Exception {
        // Process chunk in parallel
        CompletableFuture<AssetDetailEntity>[] futures = batchProcessor.processChunk(chunk);
        // Wait for all API calls to complete
        CompletableFuture.allOf(futures).join();
        // Collect processed items
        Chunk<AssetDetailEntity> processedChunk = new Chunk<>();
        for (CompletableFuture<AssetDetailEntity> future : futures) {
            AssetDetailEntity assetEntity = future.get();
            processedChunk.add(assetEntity);
        }
        // Write to database
        jpaItemWriter.write(processedChunk);
    }
}