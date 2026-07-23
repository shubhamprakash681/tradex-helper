package in.shubhamprakash681.notification_service.dtos;

import in.shubhamprakash681.notification_service.enums.AlertDirection;
import in.shubhamprakash681.notification_service.enums.AlertStatus;
import in.shubhamprakash681.notification_service.enums.NotificationType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class NotificationDtos {
    private NotificationDtos() {
    }

    public record WatchlistRequest(@NotBlank @Size(max = 32) String symbol) {
    }

    public record WatchlistResponse(Long id, String symbol, String stockName, LocalDateTime createdAt) {
    }

    public record AlertRequest(
            @NotBlank @Size(max = 32) String symbol,
            @NotNull @DecimalMin(value = "0.0001") BigDecimal targetPrice,
            @NotNull AlertDirection direction) {
    }

    public record AlertResponse(
            Long id,
            String symbol,
            String stockName,
            BigDecimal targetPrice,
            AlertDirection direction,
            AlertStatus status,
            LocalDateTime createdAt) {
    }

    public record NotificationResponse(
            Long id,
            NotificationType type,
            String title,
            String message,
            boolean read,
            LocalDateTime createdAt) {
    }
}
