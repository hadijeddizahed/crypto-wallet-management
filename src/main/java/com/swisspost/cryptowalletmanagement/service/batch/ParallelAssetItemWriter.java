package com.swisspost.cryptowalletmanagement.service.batch;

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
public class ParallelAssetItemWriter implements ItemWriter<AssetEntity> {

    private final JpaItemWriter<AssetEntity> jpaItemWriter;
    private final ParallelBatchProcessor batchProcessor;

    public ParallelAssetItemWriter(EntityManagerFactory entityManagerFactory, ParallelBatchProcessor batchProcessor) {
        this.jpaItemWriter = new JpaItemWriter<>();
        this.jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
        this.batchProcessor = batchProcessor;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void write(Chunk<? extends AssetEntity> chunk) throws Exception {
        // Process chunk in parallel
        CompletableFuture<AssetEntity>[] futures = batchProcessor.processChunk(chunk);
        // Wait for all API calls to complete
        CompletableFuture.allOf(futures).join();
        // Collect processed items
        Chunk<AssetEntity> processedChunk = new Chunk<>();
        for (CompletableFuture<AssetEntity> future : futures) {
            AssetEntity assetEntity = future.get();
            processedChunk.add(assetEntity);
        }
        // Write to database
        jpaItemWriter.write(processedChunk);
    }
}