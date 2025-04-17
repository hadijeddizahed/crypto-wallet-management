package com.swisspost.cryptowalletmanagement.service.pricing;

import com.swisspost.cryptowalletmanagement.service.dto.HistoricalAssetResponse;
import com.swisspost.cryptowalletmanagement.service.dto.SingleAssetResponse;
import com.swisspost.cryptowalletmanagement.service.dto.HistoricalRequest;
import com.swisspost.cryptowalletmanagement.service.dto.SingleAssetRequest;
import com.swisspost.cryptowalletmanagement.service.mapper.CoinCapMapper;
import com.swisspost.cryptowalletmanagement.utils.DateConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class PricingProviderServiceImpl implements PricingProviderService{

    private final CoinCapService coinCapService;
    private final CoinCapMapper mapper;

    @Override
    public HistoricalAssetResponse getHistoricalInfo(HistoricalRequest historicalRequest) {
        final var response =  coinCapService.getHistoricalInfo(historicalRequest.token(),
                "d1",
                DateConverter.getStartEpoch(historicalRequest.date()),
                DateConverter.getEndEpoch(historicalRequest.date()));
        var historicalInfo = mapper.mapHistoricalResponse(response);

        return HistoricalAssetResponse.builder()
                .priceUsd(historicalInfo.priceUsd())
                .symbol(historicalRequest.token())
                .build();
    }

    @Override
    public SingleAssetResponse getSingleAssetInfo(SingleAssetRequest singleAssetRequest) {
        final var response = coinCapService.getSingleAssetInfo(singleAssetRequest.token());
        return mapper.mapSingleAssetResponse(response);
    }
}
