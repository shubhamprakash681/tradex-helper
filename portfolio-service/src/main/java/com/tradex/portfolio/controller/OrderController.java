package com.tradex.portfolio.controller;

import com.tradex.common.security.JwtPrincipal;
import com.tradex.portfolio.dto.OrderDtos.OrderRequest;
import com.tradex.portfolio.dto.OrderDtos.OrderResponse;
import com.tradex.portfolio.service.PortfolioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final PortfolioService portfolioService;

    public OrderController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @PostMapping("/buy")
    OrderResponse buy(@AuthenticationPrincipal JwtPrincipal principal,
                      @Valid @RequestBody OrderRequest request,
                      @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        return portfolioService.buy(principal, request, authorization);
    }

    @PostMapping("/sell")
    OrderResponse sell(@AuthenticationPrincipal JwtPrincipal principal,
                       @Valid @RequestBody OrderRequest request,
                       @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        return portfolioService.sell(principal, request, authorization);
    }

    @GetMapping("/history")
    List<OrderResponse> history(@AuthenticationPrincipal JwtPrincipal principal) {
        return portfolioService.orderHistory(principal);
    }
}
