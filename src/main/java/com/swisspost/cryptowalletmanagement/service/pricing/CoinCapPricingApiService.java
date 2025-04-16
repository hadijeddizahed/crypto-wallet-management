package com.swisspost.cryptowalletmanagement.service.pricing;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swisspost.cryptowalletmanagement.service.dto.AssetHistoricalInfo;
import com.swisspost.cryptowalletmanagement.service.dto.AssetInfo;
import com.swisspost.cryptowalletmanagement.service.exceptions.BusinessException;
import com.swisspost.cryptowalletmanagement.utils.DateConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.LocalDate;
import java.util.function.Function;

@Service
@Qualifier("CoinCapPricingService")
@RequiredArgsConstructor
@Slf4j
public class CoinCapPricingApiService implements PricingApiService {

    private final WebClient coinCapWebClient;
    private final ObjectMapper objectMapper;

    @Override
    public AssetInfo getHistoricalInfo(String token, LocalDate date) {
        try {
            return coinCapWebClient.get()
                    .uri(uriBuilder -> getHitoricalUri(token, date, uriBuilder))
                    .accept(MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, getNoResponseBody())
                    .bodyToMono(JsonNode.class)
                    .map(json -> convertToAssetInfo(json, token))
                    .onErrorResume(throwable -> null)
                    .block();
        } catch (Exception ex) {
            log.error("Unexpected exception: " + ex.getMessage());
            throw new BusinessException("Pricing service is not available");
        }

    }

    @Override
    public AssetInfo getSingleAssetInfo(final String token) {
        try {
            return coinCapWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/assets/")
                            .path(token)
                            .queryParam("apiKey", "{apiKey}")
                            .build())
                    .accept(MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, getNoResponseBody())
                    .bodyToMono(JsonNode.class)
                    .map(jsonNode -> convertToAssetInfo(jsonNode, null))
                    .onErrorResume(throwable -> null)
                    .block();
        } catch (Exception ex) {
            log.error("Unexpected exception: " + ex.getMessage());
            throw new BusinessException("Pricing service is not available");
        }

    }

    private static URI getHitoricalUri(String token, LocalDate date, UriBuilder uriBuilder) {
        return uriBuilder
                .path("/assets/")
                .path(token)
                .path("/history")
                .queryParam("apiKey", "{apiKey}")
                .queryParam("interval", "d1")
                .queryParam("start", DateConverter.getStartEpoch(date))
                .queryParam("end", DateConverter.getEndEpoch(date))
                .build();
    }

    private AssetInfo convertToAssetInfo(JsonNode json, String token) {
        JsonNode dataNode = json.get("data");
        AssetHistoricalInfo assetHistoricalInfo;
        AssetInfo assetInfo;
        if (dataNode.isArray()) {
            if (dataNode.size() == 1) {
                assetHistoricalInfo = objectMapper.convertValue(dataNode.get(0), AssetHistoricalInfo.class);
                return AssetInfo.builder()
                        .priceUsd(assetHistoricalInfo.priceUsd())
                        .symbol(token)
                        .build();
            } else {
                throw new IllegalArgumentException("Expected a single object in array, but found " + dataNode.size());
            }
        } else {
            assetInfo = objectMapper.convertValue(dataNode, AssetInfo.class);
        }
        return assetInfo;
    }

    private static Function<ClientResponse, Mono<? extends Throwable>> getNoResponseBody() {
        return clientResponse ->
                clientResponse.bodyToMono(String.class)
                        .defaultIfEmpty("No response body")
                        .flatMap(errorBody -> {
                            HttpStatusCode status = clientResponse.statusCode();
                            return Mono.error(new BusinessException("API error: " + status + " - " + errorBody));
                        });
    }

}
