package in.shubhamprakash681.price_stream_service.services;

import in.shubhamprakash681.price_stream_service.dtos.PriceResponse;
import in.shubhamprakash681.price_stream_service.dtos.PriceTick;
import in.shubhamprakash681.price_stream_service.entity.PriceHistory;
import in.shubhamprakash681.price_stream_service.repositories.PriceHistoryRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PriceEventHandler {
    private final PriceCacheService priceCacheService;
    private final PriceHistoryRepository priceHistoryRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public PriceEventHandler(PriceCacheService priceCacheService,
                             PriceHistoryRepository priceHistoryRepository,
                             SimpMessagingTemplate messagingTemplate) {
        this.priceCacheService = priceCacheService;
        this.priceHistoryRepository = priceHistoryRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @Transactional
    public void handle(PriceTick tick) {
        priceCacheService.put(tick);
        priceHistoryRepository.save(toHistory(tick));
        PriceResponse response = toResponse(tick);
        messagingTemplate.convertAndSend("/topic/market", response);
        messagingTemplate.convertAndSend("/topic/" + tick.symbol(), response);
    }

    private PriceHistory toHistory(PriceTick tick) {
        return new PriceHistory(
                tick.symbol(),
                tick.price(),
                tick.previousPrice(),
                tick.changeAmount(),
                tick.changePercent(),
                tick.synthetic(),
                tick.timestamp());
    }

    private PriceResponse toResponse(PriceTick tick) {
        return new PriceResponse(
                tick.symbol(),
                tick.price(),
                tick.previousPrice(),
                tick.changeAmount(),
                tick.changePercent(),
                tick.synthetic(),
                tick.timestamp());
    }
}
