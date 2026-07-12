package com.tradex.portfolio.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "trade_orders")
public class TradeOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 32)
    private String symbol;

    @Column(nullable = false, length = 180)
    private String stockName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private OrderSide side;

    @Column(nullable = false, precision = 18, scale = 4)
    private BigDecimal quantity;

    @Column(nullable = false, precision = 18, scale = 4)
    private BigDecimal price;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private OrderStatus status = OrderStatus.EXECUTED;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    protected TradeOrder() {
    }

    public TradeOrder(Long userId, String symbol, String stockName, OrderSide side,
                      BigDecimal quantity, BigDecimal price, BigDecimal totalAmount) {
        this.userId = userId;
        this.symbol = symbol;
        this.stockName = stockName;
        this.side = side;
        this.quantity = quantity;
        this.price = price;
        this.totalAmount = totalAmount;
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

    public OrderSide getSide() {
        return side;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
