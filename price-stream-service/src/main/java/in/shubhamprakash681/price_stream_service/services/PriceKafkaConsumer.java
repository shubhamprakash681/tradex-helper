package in.shubhamprakash681.price_stream_service.services;

import in.shubhamprakash681.price_stream_service.dtos.PriceTick;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class PriceKafkaConsumer {
    private final PriceEventHandler priceEventHandler;

    public PriceKafkaConsumer(PriceEventHandler priceEventHandler) {
        this.priceEventHandler = priceEventHandler;
    }

    @KafkaListener(topics = "${tradex.prices.topic:tradex.market.prices}", groupId = "price-stream-service")
    public void consume(PriceTick tick) {
        priceEventHandler.handle(tick);
    }
}
