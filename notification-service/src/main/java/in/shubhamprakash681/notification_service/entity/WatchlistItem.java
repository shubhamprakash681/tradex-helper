package in.shubhamprakash681.notification_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "watchlist_items", uniqueConstraints = {
        @UniqueConstraint(name = "uk_watchlist_user_symbol", columnNames = {"user_id", "symbol"})
})
public class WatchlistItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 32)
    private String symbol;

    @Column(nullable = false, length = 180)
    private String stockName;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected WatchlistItem() {
    }

    public WatchlistItem(Long userId, String symbol, String stockName) {
        this.userId = userId;
        this.symbol = symbol;
        this.stockName = stockName;
    }

    public Long getId() {
        return id;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getStockName() {
        return stockName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
