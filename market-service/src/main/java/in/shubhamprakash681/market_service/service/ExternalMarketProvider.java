package in.shubhamprakash681.market_service.service;

import in.shubhamprakash681.market_service.dtos.ExternalMarketDtos.MarketIndexResponse;
import in.shubhamprakash681.market_service.dtos.ExternalMarketDtos.MarketMoverResponse;
import in.shubhamprakash681.market_service.dtos.ExternalMarketDtos.MarketTrendResponse;

import java.util.List;

public interface ExternalMarketProvider {
    List<MarketIndexResponse> indices();

    List<MarketMoverResponse> gainers();

    List<MarketMoverResponse> losers();

    List<MarketTrendResponse> trending();
}
