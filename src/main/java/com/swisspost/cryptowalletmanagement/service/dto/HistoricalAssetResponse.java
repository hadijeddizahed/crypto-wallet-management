package com.swisspost.cryptowalletmanagement.service.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record HistoricalAssetResponse(
        BigDecimal priceUsd,
        String symbol
) {
}
