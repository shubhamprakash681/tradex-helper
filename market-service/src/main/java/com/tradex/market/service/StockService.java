package com.tradex.market.service;

import com.tradex.market.dto.StockResponse;
import com.tradex.market.entity.Stock;
import com.tradex.market.repository.StockRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class StockService {
    private final StockRepository stockRepository;

    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @Transactional(readOnly = true)
    public Page<StockResponse> findAll(String q, Pageable pageable) {
        Page<Stock> stocks = q == null || q.isBlank()
                ? stockRepository.findAll(pageable)
                : stockRepository.findBySymbolContainingIgnoreCaseOrNameContainingIgnoreCase(q.trim(), q.trim(), pageable);
        return stocks.map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public StockResponse findBySymbol(String symbol) {
        return stockRepository.findById(symbol.toUpperCase())
                .map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Stock not found"));
    }

    @Transactional(readOnly = true)
    public List<StockResponse> search(String q) {
        if (q == null || q.isBlank()) {
            return List.of();
        }
        return stockRepository.findTop10BySymbolContainingIgnoreCaseOrNameContainingIgnoreCaseOrderBySymbolAsc(q.trim(), q.trim())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private StockResponse toResponse(Stock stock) {
        return new StockResponse(
                stock.getSymbol(),
                stock.getName(),
                stock.getExchange(),
                stock.getSector(),
                stock.getReferencePrice(),
                stock.isSynthetic());
    }
}
