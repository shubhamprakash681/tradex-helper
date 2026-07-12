package com.tradex.portfolio.dto;

import com.tradex.portfolio.entity.OrderSide;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionResponse(
        Long id,
        Long orderId,
        OrderSide type,
        BigDecimal amount,
        String description,
        Instant createdAt) {
}
