package in.shubhamprakash681.market_service.controllers;

import in.shubhamprakash681.market_service.dtos.ExternalMarketDtos.MarketIndexResponse;
import in.shubhamprakash681.market_service.dtos.ExternalMarketDtos.MarketMoverResponse;
import in.shubhamprakash681.market_service.dtos.ExternalMarketDtos.MarketTrendResponse;
import in.shubhamprakash681.market_service.service.ExternalMarketService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/market")
public class ExternalMarketController {
    private final ExternalMarketService externalMarketService;

    public ExternalMarketController(ExternalMarketService externalMarketService) {
        this.externalMarketService = externalMarketService;
    }

    @GetMapping("/indices")
    List<MarketIndexResponse> indices() {
        return externalMarketService.indices();
    }

    @GetMapping("/gainers")
    List<MarketMoverResponse> gainers() {
        return externalMarketService.gainers();
    }

    @GetMapping("/losers")
    List<MarketMoverResponse> losers() {
        return externalMarketService.losers();
    }

    @GetMapping("/trending")
    List<MarketTrendResponse> trending() {
        return externalMarketService.trending();
    }
}
