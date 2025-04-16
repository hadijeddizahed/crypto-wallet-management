package com.swisspost.cryptowalletmanagement.service.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record WalletResponseDTO(
        Long id,
        String email,
        List<AssetDto> assetsInfo
) {
}
