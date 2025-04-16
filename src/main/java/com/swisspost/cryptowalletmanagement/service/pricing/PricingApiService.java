package com.swisspost.cryptowalletmanagement.service.pricing;

import com.swisspost.cryptowalletmanagement.service.dto.AssetInfo;

import java.time.LocalDate;

public interface PricingApiService {

    AssetInfo getHistoricalInfo(final String token, final LocalDate date);

    AssetInfo getSingleAssetInfo(final String token);
}
