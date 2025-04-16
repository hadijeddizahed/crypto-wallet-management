package com.swisspost.cryptowalletmanagement.service.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AssetHistoricalInfo(
        BigDecimal priceUsd,
        long time,
        LocalDate date
) {
}
