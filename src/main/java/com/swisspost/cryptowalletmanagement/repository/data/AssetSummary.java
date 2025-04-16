package com.swisspost.cryptowalletmanagement.repository.data;

import java.math.BigDecimal;

public interface AssetSummary {
    String getSymbol();
    BigDecimal getPrice();
    BigDecimal getQuantity();
    BigDecimal getTotalValue();
}
