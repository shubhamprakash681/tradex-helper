package in.shubhamprakash681.market_service.service;

import in.shubhamprakash681.market_service.dtos.ExternalMarketDtos.MarketIndexResponse;
import in.shubhamprakash681.market_service.dtos.ExternalMarketDtos.MarketMoverResponse;
import in.shubhamprakash681.market_service.dtos.ExternalMarketDtos.MarketTrendResponse;
import in.shubhamprakash681.market_service.dtos.PriceTick;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class MarketPricePublisher {
    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    private final KafkaTemplate<String, PriceTick> kafkaTemplate;
    private final String topic;

    public MarketPricePublisher(KafkaTemplate<String, PriceTick> kafkaTemplate,
                                @Value("${tradex.market.topic:tradex.market.prices}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void publishIndex(MarketIndexResponse index) {
        publish(index.symbol(), index.value(), index.changeAmount(), index.changePercent(), index.asOf(), false);
    }

    public void publishMover(MarketMoverResponse mover) {
        publish(mover.symbol(), mover.price(), mover.changeAmount(), mover.changePercent(), mover.asOf(), false);
    }

    public void publishTrend(MarketTrendResponse trend) {
        BigDecimal changeAmount = trend.price()
                .multiply(trend.changePercent())
                .divide(ONE_HUNDRED, 4, RoundingMode.HALF_UP);
        publish(trend.symbol(), trend.price(), changeAmount, trend.changePercent(), trend.asOf(), trend.symbol().startsWith("S"));
    }

    private void publish(String symbol,
                         BigDecimal price,
                         BigDecimal changeAmount,
                         BigDecimal changePercent,
                         java.time.LocalDateTime asOf,
                         boolean synthetic) {
        BigDecimal normalizedPrice = scale(price);
        BigDecimal normalizedChange = scale(changeAmount);
        BigDecimal previousPrice = normalizedPrice.subtract(normalizedChange).max(BigDecimal.ONE).setScale(4, RoundingMode.HALF_UP);
        PriceTick tick = new PriceTick(
                symbol,
                normalizedPrice,
                previousPrice,
                normalizedChange,
                changePercent.setScale(4, RoundingMode.HALF_UP),
                synthetic,
                asOf);
        try {
            kafkaTemplate.send(topic, symbol, tick);
        } catch (RuntimeException ignored) {
            // Market APIs should remain responsive even if Kafka is temporarily unavailable.
        }
    }

    private BigDecimal scale(BigDecimal value) {
        return value.setScale(4, RoundingMode.HALF_UP);
    }
}
