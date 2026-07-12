package com.tradex.portfolio.controller;

import com.tradex.common.security.JwtPrincipal;
import com.tradex.portfolio.dto.TransactionResponse;
import com.tradex.portfolio.service.PortfolioService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    private final PortfolioService portfolioService;

    public TransactionController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @GetMapping
    List<TransactionResponse> transactions(@AuthenticationPrincipal JwtPrincipal principal) {
        return portfolioService.transactions(principal);
    }
}
