package in.shubhamprakash681.notification_service.services;

import in.shubhamprakash681.common_lib.security.JwtPrincipal;
import in.shubhamprakash681.notification_service.dtos.NotificationDtos.WatchlistRequest;
import in.shubhamprakash681.notification_service.dtos.NotificationDtos.WatchlistResponse;
import in.shubhamprakash681.notification_service.dtos.StockResponse;
import in.shubhamprakash681.notification_service.entity.UserNotification;
import in.shubhamprakash681.notification_service.entity.WatchlistItem;
import in.shubhamprakash681.notification_service.enums.NotificationType;
import in.shubhamprakash681.notification_service.repositories.UserNotificationRepository;
import in.shubhamprakash681.notification_service.repositories.WatchlistRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class WatchlistService {
    private final WatchlistRepository watchlistRepository;
    private final UserNotificationRepository notificationRepository;
    private final WatchlistCacheService cacheService;
    private final MarketClient marketClient;

    public WatchlistService(WatchlistRepository watchlistRepository,
                            UserNotificationRepository notificationRepository,
                            WatchlistCacheService cacheService,
                            MarketClient marketClient) {
        this.watchlistRepository = watchlistRepository;
        this.notificationRepository = notificationRepository;
        this.cacheService = cacheService;
        this.marketClient = marketClient;
    }

    @Transactional(readOnly = true)
    public List<WatchlistResponse> watchlist(JwtPrincipal principal) {
        return cacheService.get(principal.userId()).orElseGet(() -> {
            List<WatchlistResponse> response = watchlistRepository.findByUserIdOrderBySymbolAsc(principal.userId()).stream()
                    .map(this::toResponse)
                    .toList();
            cacheService.put(principal.userId(), response);
            return response;
        });
    }

    @Transactional
    public WatchlistResponse add(JwtPrincipal principal, WatchlistRequest request) {
        String symbol = normalize(request.symbol());
        if (watchlistRepository.existsByUserIdAndSymbol(principal.userId(), symbol)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Stock is already in watchlist");
        }
        StockResponse stock = marketClient.stock(symbol);
        WatchlistItem item = watchlistRepository.save(new WatchlistItem(principal.userId(), stock.symbol(), stock.name()));
        notificationRepository.save(new UserNotification(
                principal.userId(),
                NotificationType.WATCHLIST,
                "Watchlist updated",
                stock.symbol() + " was added to your watchlist"));
        cacheService.evict(principal.userId());
        return toResponse(item);
    }

    @Transactional
    public void remove(JwtPrincipal principal, String symbol) {
        String normalized = normalize(symbol);
        WatchlistItem item = watchlistRepository.findByUserIdAndSymbol(principal.userId(), normalized)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Watchlist item not found"));
        watchlistRepository.delete(item);
        cacheService.evict(principal.userId());
    }

    private WatchlistResponse toResponse(WatchlistItem item) {
        return new WatchlistResponse(item.getId(), item.getSymbol(), item.getStockName(), item.getCreatedAt());
    }

    private String normalize(String symbol) {
        return symbol.trim().toUpperCase();
    }
}
