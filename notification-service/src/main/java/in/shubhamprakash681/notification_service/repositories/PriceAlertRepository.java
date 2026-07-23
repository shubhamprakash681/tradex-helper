package in.shubhamprakash681.notification_service.repositories;

import in.shubhamprakash681.notification_service.entity.PriceAlert;
import in.shubhamprakash681.notification_service.enums.AlertStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PriceAlertRepository extends JpaRepository<PriceAlert, Long> {
    List<PriceAlert> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<PriceAlert> findBySymbolAndStatus(String symbol, AlertStatus status);

    Optional<PriceAlert> findByIdAndUserId(Long id, Long userId);

    void deleteByUserIdAndSymbol(Long userId, String symbol);

    void deleteByUserId(Long userId);
}
