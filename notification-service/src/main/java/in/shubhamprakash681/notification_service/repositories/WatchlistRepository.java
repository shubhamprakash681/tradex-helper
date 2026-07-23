package in.shubhamprakash681.notification_service.repositories;

import in.shubhamprakash681.notification_service.entity.WatchlistItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WatchlistRepository extends JpaRepository<WatchlistItem, Long> {
    List<WatchlistItem> findByUserIdOrderBySymbolAsc(Long userId);

    boolean existsByUserIdAndSymbol(Long userId, String symbol);

    Optional<WatchlistItem> findByUserIdAndSymbol(Long userId, String symbol);

    void deleteByUserIdAndSymbol(Long userId, String symbol);
}
