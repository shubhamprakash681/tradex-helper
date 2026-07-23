package in.shubhamprakash681.notification_service.entity;

import in.shubhamprakash681.notification_service.enums.AlertDirection;
import in.shubhamprakash681.notification_service.enums.AlertStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "price_alerts")
public class PriceAlert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 32)
    private String symbol;

    @Column(nullable = false, length = 180)
    private String stockName;

    @Column(nullable = false, precision = 18, scale = 4)
    private BigDecimal targetPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private AlertDirection direction;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private AlertStatus status = AlertStatus.ACTIVE;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    protected PriceAlert() {
    }

    public PriceAlert(Long userId, String symbol, String stockName, BigDecimal targetPrice, AlertDirection direction) {
        this.userId = userId;
        this.symbol = symbol;
        this.stockName = stockName;
        this.targetPrice = targetPrice;
        this.direction = direction;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getStockName() {
        return stockName;
    }

    public BigDecimal getTargetPrice() {
        return targetPrice;
    }

    public AlertDirection getDirection() {
        return direction;
    }

    public AlertStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void trigger() {
        this.status = AlertStatus.TRIGGERED;
    }
}
