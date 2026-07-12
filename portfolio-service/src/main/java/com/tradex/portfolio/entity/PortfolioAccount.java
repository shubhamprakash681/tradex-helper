package com.tradex.portfolio.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "portfolio_accounts")
public class PortfolioAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long userId;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal cashBalance;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    protected PortfolioAccount() {
    }

    public PortfolioAccount(Long userId, BigDecimal cashBalance) {
        this.userId = userId;
        this.cashBalance = cashBalance;
    }

    public Long getUserId() {
        return userId;
    }

    public BigDecimal getCashBalance() {
        return cashBalance;
    }

    public void debit(BigDecimal amount) {
        this.cashBalance = this.cashBalance.subtract(amount);
        this.updatedAt = Instant.now();
    }

    public void credit(BigDecimal amount) {
        this.cashBalance = this.cashBalance.add(amount);
        this.updatedAt = Instant.now();
    }
}
