package com.swisspost.cryptowalletmanagement.service.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.swisspost.cryptowalletmanagement.service.dto.HistoricalAssetResponse;
import com.swisspost.cryptowalletmanagement.service.dto.SingleAssetResponse;

public interface PricingApiResponseMapper {

    HistoricalAssetResponse mapHistoricalResponse(final JsonNode jsonNode);

    SingleAssetResponse mapSingleAssetResponse(final JsonNode jsonNode);
}
