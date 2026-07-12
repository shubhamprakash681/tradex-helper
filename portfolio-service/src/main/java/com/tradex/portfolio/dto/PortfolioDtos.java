package com.tradex.portfolio.dto;

import java.math.BigDecimal;
import java.util.List;

public final class PortfolioDtos {
    private PortfolioDtos() {
    }

    public record PortfolioResponse(PortfolioSummaryResponse summary, List<HoldingResponse> holdings) {
    }

    public record PortfolioSummaryResponse(
            BigDecimal cashBalance,
            BigDecimal holdingsValue,
            BigDecimal totalValue,
            BigDecimal investedValue,
            BigDecimal unrealizedPnl,
            BigDecimal unrealizedPnlPercent) {
    }

    public record HoldingResponse(
            String symbol,
            String stockName,
            BigDecimal quantity,
            BigDecimal averagePrice,
            BigDecimal lastPrice,
            BigDecimal investedValue,
            BigDecimal marketValue,
            BigDecimal unrealizedPnl,
            BigDecimal unrealizedPnlPercent) {
    }
}
