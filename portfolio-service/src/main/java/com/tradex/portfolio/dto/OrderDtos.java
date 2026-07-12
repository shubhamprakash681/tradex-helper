package com.tradex.portfolio.dto;

import com.tradex.portfolio.entity.OrderSide;
import com.tradex.portfolio.entity.OrderStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.Instant;

public final class OrderDtos {
    private OrderDtos() {
    }

    public record OrderRequest(
            @NotBlank @Size(max = 32) String symbol,
            @NotNull @DecimalMin(value = "0.0001") BigDecimal quantity) {
    }

    public record OrderResponse(
            Long id,
            String symbol,
            String stockName,
            OrderSide side,
            BigDecimal quantity,
            BigDecimal price,
            BigDecimal totalAmount,
            OrderStatus status,
            Instant createdAt) {
    }
}
