package com.tradex.portfolio.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

@Component
public class MarketClient {
    private final RestClient restClient;

    public MarketClient(RestClient.Builder restClientBuilder,
                        @Value("${tradex.services.market-url:http://localhost:8082}") String marketServiceUrl) {
        this.restClient = restClientBuilder.baseUrl(marketServiceUrl).build();
    }

    public StockSnapshot findStock(String symbol, String authorization) {
        try {
            StockResponse stock = restClient.get()
                    .uri("/api/stocks/{symbol}", symbol)
                    .header(HttpHeaders.AUTHORIZATION, authorization)
                    .retrieve()
                    .body(StockResponse.class);
            if (stock == null) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Market service returned an empty stock response");
            }
            return new StockSnapshot(stock.symbol(), stock.name(), stock.referencePrice());
        } catch (RestClientResponseException exception) {
            if (exception.getStatusCode().value() == 404) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Stock not found");
            }
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Market service rejected stock lookup");
        } catch (RuntimeException exception) {
            if (exception instanceof ResponseStatusException responseStatusException) {
                throw responseStatusException;
            }
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Market service is unavailable");
        }
    }

    private record StockResponse(String symbol, String name, BigDecimal referencePrice) {
    }

    public record StockSnapshot(String symbol, String name, BigDecimal referencePrice) {
    }
}
