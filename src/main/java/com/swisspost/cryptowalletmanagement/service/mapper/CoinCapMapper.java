package com.swisspost.cryptowalletmanagement.service.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swisspost.cryptowalletmanagement.service.dto.HistoricalAssetResponse;
import com.swisspost.cryptowalletmanagement.service.dto.SingleAssetResponse;
import com.swisspost.cryptowalletmanagement.service.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CoinCapMapper implements PricingApiResponseMapper{

    private final ObjectMapper objectMapper;
    @Override
    public HistoricalAssetResponse mapHistoricalResponse(JsonNode jsonNode) {
        JsonNode dataNode = jsonNode.get("data");
        if (dataNode!= null && dataNode.isArray()) {
            if (dataNode.size() == 1) {
                return objectMapper.convertValue(dataNode.get(0), HistoricalAssetResponse.class);
            } else {
                throw new IllegalArgumentException("Expected a single object in array, but found " + dataNode.size());
            }
        }
        return null;
    }

    @Override
    public SingleAssetResponse mapSingleAssetResponse(JsonNode jsonNode) {
        JsonNode dataNode = jsonNode.get("data");
        if (dataNode != null)
          return objectMapper.convertValue(dataNode, SingleAssetResponse.class);
        throw new BusinessException("Pricing service not responded");
    }
}
