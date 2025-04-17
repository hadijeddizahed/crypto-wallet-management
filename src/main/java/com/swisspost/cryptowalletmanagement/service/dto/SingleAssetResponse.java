package com.swisspost.cryptowalletmanagement.service.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record SingleAssetResponse(
        String id,
        String name,
        String symbol,
        BigDecimal priceUsd
) {
}
