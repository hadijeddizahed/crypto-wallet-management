package com.swisspost.cryptowalletmanagement.service.batch;

import com.swisspost.cryptowalletmanagement.repository.entity.AssetDetailEntity;
import com.swisspost.cryptowalletmanagement.service.dto.AssetEnum;
import com.swisspost.cryptowalletmanagement.service.dto.SingleAssetRequest;
import com.swisspost.cryptowalletmanagement.service.pricing.PricingProviderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemProcessor;

import java.util.concurrent.CompletableFuture;

@Slf4j
public class ParallelBatchProcessor implements ItemProcessor<AssetDetailEntity, AssetDetailEntity> {

    private final PricingProviderService pricingProviderService;

    public ParallelBatchProcessor(PricingProviderService pricingProviderService) {
        this.pricingProviderService = pricingProviderService;
    }

    @Override
    public AssetDetailEntity process(AssetDetailEntity assetDetailEntity) {
        return assetDetailEntity;
    }

    // Custom method to process a chunk of Assets in parallel
    public CompletableFuture<AssetDetailEntity>[] processChunk(Chunk<? extends AssetDetailEntity> chunk) {
        @SuppressWarnings("unchecked")
        CompletableFuture<AssetDetailEntity>[] futures = new CompletableFuture[chunk.size()];
        int index = 0;
        for (AssetDetailEntity item : chunk.getItems()) {
            futures[index++] = CompletableFuture.supplyAsync(() -> {
                final var assetInfo = pricingProviderService.getSingleAssetInfo(new SingleAssetRequest(AssetEnum.findBySymbol(item.getSymbol()).getName()));
                log.info("Updated price for asset:{} is: {}", item.getSymbol(), assetInfo.priceUsd());
                item.setPrice(assetInfo.priceUsd());
                return item;
            });
        }
        return futures;
    }
}