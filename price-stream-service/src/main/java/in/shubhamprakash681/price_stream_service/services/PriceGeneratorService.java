package in.shubhamprakash681.price_stream_service.services;

import in.shubhamprakash681.price_stream_service.config.PriceStreamProperties;
import in.shubhamprakash681.price_stream_service.dtos.PriceTick;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Random;

@Service
public class PriceGeneratorService {
    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    private final KafkaTemplate<String, PriceTick> kafkaTemplate;
    private final PriceStreamProperties properties;
    private final SeedPriceCatalog seedPriceCatalog;
    private final PriceCacheService priceCacheService;
    private final PriceEventHandler priceEventHandler;
    private final Random random = new Random();

    public PriceGeneratorService(KafkaTemplate<String, PriceTick> kafkaTemplate,
                                 PriceStreamProperties properties,
                                 SeedPriceCatalog seedPriceCatalog,
                                 PriceCacheService priceCacheService,
                                 PriceEventHandler priceEventHandler) {
        this.kafkaTemplate = kafkaTemplate;
        this.properties = properties;
        this.seedPriceCatalog = seedPriceCatalog;
        this.priceCacheService = priceCacheService;
        this.priceEventHandler = priceEventHandler;
    }

    @Scheduled(fixedDelayString = "${tradex.prices.generation-interval-ms:2000}")
    public void generateSyntheticBatch() {
        for (String configuredSymbol : properties.getSymbols()) {
            String symbol = configuredSymbol.trim().toUpperCase();
            if (!symbol.isBlank()) {
                publish(nextTick(symbol));
            }
        }
    }

    private PriceTick nextTick(String symbol) {
        BigDecimal previousPrice = priceCacheService.get(symbol)
                .map(PriceTick::price)
                .orElse(seedPriceCatalog.basePrice(symbol))
                .setScale(4, RoundingMode.HALF_UP);

        BigDecimal movePercent = BigDecimal.valueOf((random.nextDouble() - 0.5) * 1.4)
                .setScale(4, RoundingMode.HALF_UP);
        BigDecimal changeAmount = previousPrice.multiply(movePercent)
                .divide(ONE_HUNDRED, 4, RoundingMode.HALF_UP);
        BigDecimal price = previousPrice.add(changeAmount).max(new BigDecimal("1.0000")).setScale(4, RoundingMode.HALF_UP);

        return new PriceTick(
                symbol,
                price,
                previousPrice,
                price.subtract(previousPrice).setScale(4, RoundingMode.HALF_UP),
                price.subtract(previousPrice).multiply(ONE_HUNDRED).divide(previousPrice, 4, RoundingMode.HALF_UP),
                seedPriceCatalog.synthetic(symbol),
                LocalDateTime.now());
    }

    private void publish(PriceTick tick) {
        try {
            kafkaTemplate.send(properties.getTopic(), tick.symbol(), tick)
                    .whenComplete((result, exception) -> {
                        if (exception != null) {
                            priceEventHandler.handle(tick);
                        }
                    });
        } catch (RuntimeException exception) {
            priceEventHandler.handle(tick);
        }
    }
}
