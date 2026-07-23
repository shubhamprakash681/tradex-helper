package in.shubhamprakash681.notification_service.services;

import in.shubhamprakash681.common_lib.security.JwtPrincipal;
import in.shubhamprakash681.notification_service.dtos.NotificationDtos.AlertRequest;
import in.shubhamprakash681.notification_service.dtos.NotificationDtos.AlertResponse;
import in.shubhamprakash681.notification_service.dtos.StockResponse;
import in.shubhamprakash681.notification_service.entity.PriceAlert;
import in.shubhamprakash681.notification_service.repositories.PriceAlertRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AlertService {
    private final PriceAlertRepository alertRepository;
    private final MarketClient marketClient;

    public AlertService(PriceAlertRepository alertRepository, MarketClient marketClient) {
        this.alertRepository = alertRepository;
        this.marketClient = marketClient;
    }

    @Transactional
    public AlertResponse create(JwtPrincipal principal, AlertRequest request) {
        String symbol = normalize(request.symbol());
        StockResponse stock = marketClient.stock(symbol);
        PriceAlert alert = alertRepository.save(new PriceAlert(
                principal.userId(),
                stock.symbol(),
                stock.name(),
                request.targetPrice(),
                request.direction()));
        return toResponse(alert);
    }

    @Transactional(readOnly = true)
    public List<AlertResponse> alerts(JwtPrincipal principal) {
        return alertRepository.findByUserIdOrderByCreatedAtDesc(principal.userId()).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void delete(JwtPrincipal principal, Long id, String symbol) {
        if (id != null) {
            PriceAlert alert = alertRepository.findByIdAndUserId(id, principal.userId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Alert not found"));
            alertRepository.delete(alert);
            return;
        }
        if (symbol != null && !symbol.isBlank()) {
            alertRepository.deleteByUserIdAndSymbol(principal.userId(), normalize(symbol));
            return;
        }
        alertRepository.deleteByUserId(principal.userId());
    }

    private AlertResponse toResponse(PriceAlert alert) {
        return new AlertResponse(
                alert.getId(),
                alert.getSymbol(),
                alert.getStockName(),
                alert.getTargetPrice(),
                alert.getDirection(),
                alert.getStatus(),
                alert.getCreatedAt());
    }

    private String normalize(String symbol) {
        return symbol.trim().toUpperCase();
    }
}
