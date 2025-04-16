package com.swisspost.cryptowalletmanagement.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "app.clients")
@Data
@Setter
@Getter
public class ClientApiProperties {

    private Map<String, ApiClientConfig> clientsApiConfig;

    @Data
    public static class ApiClientConfig {
        private String baseUrl;
        private String apiKey;
    }
}
