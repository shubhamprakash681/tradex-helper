package com.tradex.market.controller;

import com.tradex.market.dto.StockResponse;
import com.tradex.market.service.StockService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/stocks")
public class StockController {
    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping
    Page<StockResponse> stocks(@RequestParam(required = false) String q,
                               @PageableDefault(size = 20, sort = "symbol") Pageable pageable) {
        return stockService.findAll(q, pageable);
    }

    @GetMapping("/{symbol}")
    StockResponse stock(@PathVariable String symbol) {
        return stockService.findBySymbol(symbol);
    }

    @GetMapping("/search")
    List<StockResponse> search(@RequestParam String q) {
        return stockService.search(q);
    }
}
