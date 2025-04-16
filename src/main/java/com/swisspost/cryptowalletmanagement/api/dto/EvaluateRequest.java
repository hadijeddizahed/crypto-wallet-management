package com.swisspost.cryptowalletmanagement.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class EvaluateRequest {
    @NotNull(message = "Date must not be null")
    private LocalDate date;

    private List<AssetInfoRequest> assets;
}
