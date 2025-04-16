package com.swisspost.cryptowalletmanagement.service.batch;

import com.swisspost.cryptowalletmanagement.repository.entity.AssetEntity;
import com.swisspost.cryptowalletmanagement.service.pricing.PricingApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemProcessor;

import java.util.concurrent.CompletableFuture;

@Slf4j
public class ParallelBatchProcessor implements ItemProcessor<AssetEntity, AssetEntity> {

    private final PricingApiService pricingApiService;

    public ParallelBatchProcessor(PricingApiService pricingApiService) {
        this.pricingApiService = pricingApiService;
    }

    @Override
    public AssetEntity process(AssetEntity assetEntity) {
        return assetEntity;
    }

    // Custom method to process a chunk of Assets in parallel
    public CompletableFuture<AssetEntity>[] processChunk(Chunk<? extends AssetEntity> chunk) {
        @SuppressWarnings("unchecked")
        CompletableFuture<AssetEntity>[] futures = new CompletableFuture[chunk.size()];
        int index = 0;
        for (AssetEntity item : chunk.getItems()) {
            futures[index++] = CompletableFuture.supplyAsync(() -> {
//                final var assetInfo = pricingService.getSingleAssetInfo(item.getSymbol());
//                log.info("Updated price for asset:{} is: {}", item.getSymbol(), assetInfo.priceUsd());
//                item.setPrice(assetInfo.priceUsd());
                return item;
            });
        }
        return futures;
    }
}