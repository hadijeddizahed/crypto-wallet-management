package com.swisspost.cryptowalletmanagement.service.pricing;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swisspost.cryptowalletmanagement.service.exceptions.BusinessException;
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
import java.util.function.Function;

@Service
@Qualifier("CoinCapPricingService")
@RequiredArgsConstructor
@Slf4j
public class CoinCapService implements PricingApiService {

    private final WebClient coinCapWebClient;
    private final ObjectMapper objectMapper;

    @Override
    public JsonNode getHistoricalInfo(final String token, final String interval, final long startDate, final long endDate) {
        try {
            return coinCapWebClient.get()
                    .uri(uriBuilder -> getHitoricalUri(token, interval,startDate,endDate, uriBuilder))
                    .accept(MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, getNoResponseBody())
                    .bodyToMono(JsonNode.class)
                    .onErrorResume(throwable -> null)
                    .block();
        } catch (Exception ex) {
            log.error("Unexpected exception: " + ex.getMessage());
            throw new BusinessException("Pricing service is not available");
        }

    }

    @Override
    public JsonNode getSingleAssetInfo(final String token) {
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
                    .onErrorResume(throwable -> null)
                    .block();
        } catch (Exception ex) {
            log.error("Unexpected exception: " + ex.getMessage());
            throw new BusinessException("Pricing service is not available");
        }

    }

    private static URI getHitoricalUri(String token, final String interval, final long startDate, final long endDate, UriBuilder uriBuilder) {
        return uriBuilder
                .path("/assets/")
                .path(token)
                .path("/history")
                .queryParam("apiKey", "{apiKey}")
                .queryParam("interval", interval)
                .queryParam("start", startDate)
                .queryParam("end", endDate)
                .build();
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
