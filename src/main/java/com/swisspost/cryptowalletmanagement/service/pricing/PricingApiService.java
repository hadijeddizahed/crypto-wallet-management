package com.swisspost.cryptowalletmanagement.service.pricing;

import com.fasterxml.jackson.databind.JsonNode;

public interface PricingApiService {

    JsonNode getHistoricalInfo(final String token,final String interval, final long startDate, final long endDate);

    JsonNode getSingleAssetInfo(final String token);
}
