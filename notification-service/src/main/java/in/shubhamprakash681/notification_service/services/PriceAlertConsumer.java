package in.shubhamprakash681.notification_service.services;

import in.shubhamprakash681.notification_service.dtos.PriceTick;
import in.shubhamprakash681.notification_service.entity.PriceAlert;
import in.shubhamprakash681.notification_service.entity.UserNotification;
import in.shubhamprakash681.notification_service.enums.AlertDirection;
import in.shubhamprakash681.notification_service.enums.AlertStatus;
import in.shubhamprakash681.notification_service.enums.NotificationType;
import in.shubhamprakash681.notification_service.repositories.PriceAlertRepository;
import in.shubhamprakash681.notification_service.repositories.UserNotificationRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class PriceAlertConsumer {
    private final PriceAlertRepository alertRepository;
    private final UserNotificationRepository notificationRepository;

    public PriceAlertConsumer(PriceAlertRepository alertRepository, UserNotificationRepository notificationRepository) {
        this.alertRepository = alertRepository;
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    @KafkaListener(topics = "${tradex.notifications.price-topic:tradex.market.prices}", groupId = "notification-service")
    public void onPrice(PriceTick tick) {
        alertRepository.findBySymbolAndStatus(tick.symbol(), AlertStatus.ACTIVE).stream()
                .filter(alert -> triggered(alert, tick.price()))
                .forEach(alert -> {
                    alert.trigger();
                    notificationRepository.save(new UserNotification(
                            alert.getUserId(),
                            NotificationType.PRICE_ALERT,
                            "Price alert triggered",
                            alert.getSymbol() + " is " + tick.price() + ", target was " + alert.getDirection() + " " + alert.getTargetPrice()));
                });
    }

    private boolean triggered(PriceAlert alert, BigDecimal price) {
        if (alert.getDirection() == AlertDirection.ABOVE) {
            return price.compareTo(alert.getTargetPrice()) >= 0;
        }
        return price.compareTo(alert.getTargetPrice()) <= 0;
    }
}
