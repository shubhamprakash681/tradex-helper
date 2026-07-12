package com.tradex.portfolio.controller;

import com.tradex.common.security.JwtPrincipal;
import com.tradex.portfolio.dto.PortfolioDtos.HoldingResponse;
import com.tradex.portfolio.dto.PortfolioDtos.PortfolioResponse;
import com.tradex.portfolio.dto.PortfolioDtos.PortfolioSummaryResponse;
import com.tradex.portfolio.service.PortfolioService;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/portfolio")
public class PortfolioController {
    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @GetMapping
    PortfolioResponse portfolio(@AuthenticationPrincipal JwtPrincipal principal,
                                @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        return portfolioService.portfolio(principal, authorization);
    }

    @GetMapping("/summary")
    PortfolioSummaryResponse summary(@AuthenticationPrincipal JwtPrincipal principal,
                                     @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        return portfolioService.summary(principal, authorization);
    }

    @GetMapping("/holdings")
    List<HoldingResponse> holdings(@AuthenticationPrincipal JwtPrincipal principal,
                                   @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        return portfolioService.holdings(principal, authorization);
    }
}
