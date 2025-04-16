package com.swisspost.cryptowalletmanagement.service.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record AssetDto(
        String symbol,
        BigDecimal quantity,
        BigDecimal price,
        BigDecimal value
        ) {
}
