package in.shubhamprakash681.market_service.service;

import in.shubhamprakash681.market_service.config.ExternalMarketProperties;
import in.shubhamprakash681.market_service.dtos.ExternalMarketDtos.MarketIndexResponse;
import in.shubhamprakash681.market_service.dtos.ExternalMarketDtos.MarketMoverResponse;
import in.shubhamprakash681.market_service.dtos.ExternalMarketDtos.MarketTrendResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.List;

@Component
public class ThirdPartyMarketClient implements ExternalMarketProvider {
    private final ExternalMarketProperties properties;
    private final RestClient.Builder restClientBuilder;

    public ThirdPartyMarketClient(ExternalMarketProperties properties, RestClient.Builder restClientBuilder) {
        this.properties = properties;
        this.restClientBuilder = restClientBuilder;
    }

    @Override
    public List<MarketIndexResponse> indices() {
        return fetch("/indices", MarketIndexResponse[].class);
    }

    @Override
    public List<MarketMoverResponse> gainers() {
        return fetch("/gainers", MarketMoverResponse[].class);
    }

    @Override
    public List<MarketMoverResponse> losers() {
        return fetch("/losers", MarketMoverResponse[].class);
    }

    @Override
    public List<MarketTrendResponse> trending() {
        return fetch("/trending", MarketTrendResponse[].class);
    }

    public boolean available() {
        return properties.isEnabled() && properties.getBaseUrl() != null && !properties.getBaseUrl().isBlank();
    }

    private <T> List<T> fetch(String path, Class<T[]> responseType) {
        if (!available()) {
            throw new IllegalStateException("External market provider is disabled");
        }
        RestClient restClient = restClientBuilder.baseUrl(properties.getBaseUrl()).build();
        T[] body = restClient.get()
                .uri(path)
                .headers(headers -> applyAuth(headers, properties.getApiKey()))
                .retrieve()
                .body(responseType);
        return body == null ? List.of() : Arrays.asList(body);
    }

    private void applyAuth(HttpHeaders headers, String apiKey) {
        if (apiKey != null && !apiKey.isBlank()) {
            headers.set("X-API-Key", apiKey);
        }
    }
}
