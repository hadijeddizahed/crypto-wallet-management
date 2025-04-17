package com.swisspost.cryptowalletmanagement.service.pricing;

import com.swisspost.cryptowalletmanagement.service.dto.HistoricalAssetResponse;
import com.swisspost.cryptowalletmanagement.service.dto.SingleAssetResponse;
import com.swisspost.cryptowalletmanagement.service.dto.HistoricalRequest;
import com.swisspost.cryptowalletmanagement.service.dto.SingleAssetRequest;


public interface PricingProviderService {
    HistoricalAssetResponse getHistoricalInfo(final HistoricalRequest historicalRequest);

    SingleAssetResponse getSingleAssetInfo(final SingleAssetRequest singleAssetRequest);
}
