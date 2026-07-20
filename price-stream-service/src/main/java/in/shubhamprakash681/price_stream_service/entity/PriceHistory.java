package in.shubhamprakash681.price_stream_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "price_history")
public class PriceHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 32)
    private String symbol;

    @Column(nullable = false, precision = 18, scale = 4)
    private BigDecimal price;

    @Column(nullable = false, precision = 18, scale = 4)
    private BigDecimal previousPrice;

    @Column(nullable = false, precision = 18, scale = 4)
    private BigDecimal changeAmount;

    @Column(nullable = false, precision = 9, scale = 4)
    private BigDecimal changePercent;

    @Column(nullable = false)
    private boolean synthetic;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    protected PriceHistory() {
    }

    public PriceHistory(String symbol,
                        BigDecimal price,
                        BigDecimal previousPrice,
                        BigDecimal changeAmount,
                        BigDecimal changePercent,
                        boolean synthetic,
                        LocalDateTime timestamp) {
        this.symbol = symbol;
        this.price = price;
        this.previousPrice = previousPrice;
        this.changeAmount = changeAmount;
        this.changePercent = changePercent;
        this.synthetic = synthetic;
        this.timestamp = timestamp;
    }

    public String getSymbol() {
        return symbol;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getPreviousPrice() {
        return previousPrice;
    }

    public BigDecimal getChangeAmount() {
        return changeAmount;
    }

    public BigDecimal getChangePercent() {
        return changePercent;
    }

    public boolean isSynthetic() {
        return synthetic;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
