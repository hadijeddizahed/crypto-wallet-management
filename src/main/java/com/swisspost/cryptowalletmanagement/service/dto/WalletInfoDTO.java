package com.swisspost.cryptowalletmanagement.service.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder(toBuilder = true)
public record WalletInfoDTO(
        List<AssetDto> assets,
         BigDecimal totalValue,
         int page,
         int size,
         long totalElements
) {
}
