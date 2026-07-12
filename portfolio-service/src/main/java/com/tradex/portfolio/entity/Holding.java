package com.tradex.portfolio.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "portfolio_holdings", uniqueConstraints = {
        @UniqueConstraint(name = "uk_portfolio_holdings_user_symbol", columnNames = {"user_id", "symbol"})
})
public class Holding {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 32)
    private String symbol;

    @Column(nullable = false, length = 180)
    private String stockName;

    @Column(nullable = false, precision = 18, scale = 4)
    private BigDecimal quantity;

    @Column(nullable = false, precision = 18, scale = 4)
    private BigDecimal averagePrice;

    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    protected Holding() {
    }

    public Holding(Long userId, String symbol, String stockName, BigDecimal quantity, BigDecimal averagePrice) {
        this.userId = userId;
        this.symbol = symbol;
        this.stockName = stockName;
        this.quantity = quantity;
        this.averagePrice = averagePrice;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getStockName() {
        return stockName;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public BigDecimal getAveragePrice() {
        return averagePrice;
    }

    public void buy(BigDecimal buyQuantity, BigDecimal buyPrice, String latestStockName) {
        BigDecimal currentValue = quantity.multiply(averagePrice);
        BigDecimal buyValue = buyQuantity.multiply(buyPrice);
        BigDecimal newQuantity = quantity.add(buyQuantity);
        this.quantity = newQuantity;
        this.averagePrice = currentValue.add(buyValue).divide(newQuantity, 4, java.math.RoundingMode.HALF_UP);
        this.stockName = latestStockName;
        this.updatedAt = Instant.now();
    }

    public void sell(BigDecimal sellQuantity) {
        this.quantity = this.quantity.subtract(sellQuantity);
        this.updatedAt = Instant.now();
    }

    public boolean isEmpty() {
        return quantity.compareTo(BigDecimal.ZERO) == 0;
    }
}
