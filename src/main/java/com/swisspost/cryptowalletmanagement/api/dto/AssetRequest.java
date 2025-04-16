package com.swisspost.cryptowalletmanagement.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;


@Data
public class AssetRequest {
    @NotBlank
    private String symbol;
    @NotNull
    private BigDecimal quantity;
}

