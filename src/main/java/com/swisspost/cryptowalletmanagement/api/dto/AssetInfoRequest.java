package com.swisspost.cryptowalletmanagement.api.dto;

import java.math.BigDecimal;

public record AssetInfoRequest(String symbol, BigDecimal quantity, BigDecimal value){}
