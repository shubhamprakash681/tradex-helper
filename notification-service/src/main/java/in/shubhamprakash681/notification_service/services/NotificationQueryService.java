package in.shubhamprakash681.notification_service.services;

import in.shubhamprakash681.common_lib.security.JwtPrincipal;
import in.shubhamprakash681.notification_service.dtos.NotificationDtos.NotificationResponse;
import in.shubhamprakash681.notification_service.entity.UserNotification;
import in.shubhamprakash681.notification_service.repositories.UserNotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationQueryService {
    private final UserNotificationRepository notificationRepository;

    public NotificationQueryService(UserNotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> notifications(JwtPrincipal principal) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(principal.userId()).stream()
                .map(this::toResponse)
                .toList();
    }

    private NotificationResponse toResponse(UserNotification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getType(),
                notification.getTitle(),
                notification.getMessage(),
                notification.isReadFlag(),
                notification.getCreatedAt());
    }
}
