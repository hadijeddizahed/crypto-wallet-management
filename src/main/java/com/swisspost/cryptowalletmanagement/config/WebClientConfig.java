package com.swisspost.cryptowalletmanagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient coinCapWebClient(final ClientApiProperties clientApiProperties) {
        final ClientApiProperties.ApiClientConfig apiClientConfig = clientApiProperties.getClientsApiConfig().get("coin-cap");
        return WebClient.builder()
                .baseUrl(apiClientConfig.getBaseUrl())
                .defaultUriVariables(Map.of("apiKey", apiClientConfig.getApiKey()))
                .build();
    }
}
