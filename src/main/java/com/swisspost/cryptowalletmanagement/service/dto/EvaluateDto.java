package com.swisspost.cryptowalletmanagement.service.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record EvaluateDto(
        BigDecimal total,
        String bestAsset,
        BigDecimal bestPerformance,
        String worstAsset,
        BigDecimal worstPerformance
) {
}
