package in.shubhamprakash681.market_service.service;

import in.shubhamprakash681.market_service.config.ExternalMarketProperties;
import in.shubhamprakash681.market_service.dtos.ExternalMarketDtos.MarketIndexResponse;
import in.shubhamprakash681.market_service.dtos.ExternalMarketDtos.MarketMoverResponse;
import in.shubhamprakash681.market_service.dtos.ExternalMarketDtos.MarketSnapshot;
import in.shubhamprakash681.market_service.dtos.ExternalMarketDtos.MarketTrendResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExternalMarketService {
    private final ThirdPartyMarketClient thirdPartyMarketClient;
    private final FallbackExternalMarketProvider fallbackExternalMarketProvider;
    private final MarketPricePublisher marketPricePublisher;
    private final ExternalMarketProperties properties;

    public ExternalMarketService(ThirdPartyMarketClient thirdPartyMarketClient,
                                 FallbackExternalMarketProvider fallbackExternalMarketProvider,
                                 MarketPricePublisher marketPricePublisher,
                                 ExternalMarketProperties properties) {
        this.thirdPartyMarketClient = thirdPartyMarketClient;
        this.fallbackExternalMarketProvider = fallbackExternalMarketProvider;
        this.marketPricePublisher = marketPricePublisher;
        this.properties = properties;
    }

    public List<MarketIndexResponse> indices() {
        List<MarketIndexResponse> response = provider().indices();
        publishIndices(response);
        return response;
    }

    public List<MarketMoverResponse> gainers() {
        List<MarketMoverResponse> response = provider().gainers();
        publishMovers(response);
        return response;
    }

    public List<MarketMoverResponse> losers() {
        List<MarketMoverResponse> response = provider().losers();
        publishMovers(response);
        return response;
    }

    public List<MarketTrendResponse> trending() {
        List<MarketTrendResponse> response = provider().trending();
        publishTrends(response);
        return response;
    }

    public MarketSnapshot snapshotAndPublish() {
        ExternalMarketProvider selectedProvider = provider();
        List<MarketIndexResponse> indices = selectedProvider.indices();
        List<MarketMoverResponse> gainers = selectedProvider.gainers();
        List<MarketMoverResponse> losers = selectedProvider.losers();
        List<MarketTrendResponse> trending = selectedProvider.trending();
        indices.forEach(marketPricePublisher::publishIndex);
        gainers.forEach(marketPricePublisher::publishMover);
        losers.forEach(marketPricePublisher::publishMover);
        trending.forEach(marketPricePublisher::publishTrend);
        return new MarketSnapshot(indices, gainers, losers, trending);
    }

    private ExternalMarketProvider provider() {
        if (!thirdPartyMarketClient.available()) {
            return fallbackExternalMarketProvider;
        }
        return new SafeExternalMarketProvider(thirdPartyMarketClient, fallbackExternalMarketProvider);
    }

    private void publishIndices(List<MarketIndexResponse> indices) {
        if (properties.isPublishOnRequest()) {
            indices.forEach(marketPricePublisher::publishIndex);
        }
    }

    private void publishMovers(List<MarketMoverResponse> movers) {
        if (properties.isPublishOnRequest()) {
            movers.forEach(marketPricePublisher::publishMover);
        }
    }

    private void publishTrends(List<MarketTrendResponse> trends) {
        if (properties.isPublishOnRequest()) {
            trends.forEach(marketPricePublisher::publishTrend);
        }
    }

    private record SafeExternalMarketProvider(ExternalMarketProvider primary,
                                              ExternalMarketProvider fallback) implements ExternalMarketProvider {
        @Override
        public List<MarketIndexResponse> indices() {
            try {
                return primary.indices();
            } catch (RuntimeException exception) {
                return fallback.indices();
            }
        }

        @Override
        public List<MarketMoverResponse> gainers() {
            try {
                return primary.gainers();
            } catch (RuntimeException exception) {
                return fallback.gainers();
            }
        }

        @Override
        public List<MarketMoverResponse> losers() {
            try {
                return primary.losers();
            } catch (RuntimeException exception) {
                return fallback.losers();
            }
        }

        @Override
        public List<MarketTrendResponse> trending() {
            try {
                return primary.trending();
            } catch (RuntimeException exception) {
                return fallback.trending();
            }
        }
    }
}
