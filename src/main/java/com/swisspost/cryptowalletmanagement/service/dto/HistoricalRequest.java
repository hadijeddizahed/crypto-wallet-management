package com.swisspost.cryptowalletmanagement.service.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record HistoricalRequest(String token,
                                LocalDate date) {
}
