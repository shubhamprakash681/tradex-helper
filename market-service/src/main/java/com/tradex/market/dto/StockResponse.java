package com.tradex.market.dto;

import java.math.BigDecimal;

public record StockResponse(
        String symbol,
        String name,
        String exchange,
        String sector,
        BigDecimal referencePrice,
        boolean synthetic) {
}
