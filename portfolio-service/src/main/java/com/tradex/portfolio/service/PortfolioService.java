package com.tradex.portfolio.service;

import com.tradex.common.security.JwtPrincipal;
import com.tradex.portfolio.dto.OrderDtos.OrderRequest;
import com.tradex.portfolio.dto.OrderDtos.OrderResponse;
import com.tradex.portfolio.dto.PortfolioDtos.HoldingResponse;
import com.tradex.portfolio.dto.PortfolioDtos.PortfolioResponse;
import com.tradex.portfolio.dto.PortfolioDtos.PortfolioSummaryResponse;
import com.tradex.portfolio.dto.TransactionResponse;
import com.tradex.portfolio.entity.Holding;
import com.tradex.portfolio.entity.LedgerTransaction;
import com.tradex.portfolio.entity.OrderSide;
import com.tradex.portfolio.entity.PortfolioAccount;
import com.tradex.portfolio.entity.TradeOrder;
import com.tradex.portfolio.repository.HoldingRepository;
import com.tradex.portfolio.repository.LedgerTransactionRepository;
import com.tradex.portfolio.repository.PortfolioAccountRepository;
import com.tradex.portfolio.repository.TradeOrderRepository;
import com.tradex.portfolio.service.MarketClient.StockSnapshot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class PortfolioService {
    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    private final PortfolioAccountRepository accountRepository;
    private final HoldingRepository holdingRepository;
    private final TradeOrderRepository orderRepository;
    private final LedgerTransactionRepository transactionRepository;
    private final MarketClient marketClient;
    private final BigDecimal startingCash;

    public PortfolioService(PortfolioAccountRepository accountRepository,
                            HoldingRepository holdingRepository,
                            TradeOrderRepository orderRepository,
                            LedgerTransactionRepository transactionRepository,
                            MarketClient marketClient,
                            @Value("${tradex.trading.starting-cash:1000000.00}") BigDecimal startingCash) {
        this.accountRepository = accountRepository;
        this.holdingRepository = holdingRepository;
        this.orderRepository = orderRepository;
        this.transactionRepository = transactionRepository;
        this.marketClient = marketClient;
        this.startingCash = money(startingCash);
    }

    @Transactional
    public PortfolioResponse portfolio(JwtPrincipal principal, String authorization) {
        PortfolioAccount account = getOrCreateAccount(principal.userId());
        List<HoldingResponse> holdings = holdings(principal, authorization);
        return new PortfolioResponse(summary(account, holdings), holdings);
    }

    @Transactional
    public PortfolioSummaryResponse summary(JwtPrincipal principal, String authorization) {
        PortfolioAccount account = getOrCreateAccount(principal.userId());
        return summary(account, holdings(principal, authorization));
    }

    @Transactional(readOnly = true)
    public List<HoldingResponse> holdings(JwtPrincipal principal, String authorization) {
        return holdingRepository.findByUserIdOrderBySymbolAsc(principal.userId()).stream()
                .map(holding -> toHoldingResponse(holding, authorization))
                .toList();
    }

    @Transactional
    public OrderResponse buy(JwtPrincipal principal, OrderRequest request, String authorization) {
        String symbol = normalizeSymbol(request.symbol());
        BigDecimal quantity = quantity(request.quantity());
        StockSnapshot stock = marketClient.findStock(symbol, authorization);
        BigDecimal price = price(stock.referencePrice());
        BigDecimal totalAmount = money(price.multiply(quantity));

        PortfolioAccount account = getOrCreateAccount(principal.userId());
        if (account.getCashBalance().compareTo(totalAmount) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient virtual cash");
        }

        account.debit(totalAmount);
        Holding holding = holdingRepository.findByUserIdAndSymbol(principal.userId(), stock.symbol())
                .orElseGet(() -> new Holding(principal.userId(), stock.symbol(), stock.name(), BigDecimal.ZERO.setScale(4), price));
        holding.buy(quantity, price, stock.name());
        holdingRepository.save(holding);

        TradeOrder order = orderRepository.save(new TradeOrder(
                principal.userId(), stock.symbol(), stock.name(), OrderSide.BUY, quantity, price, totalAmount));
        transactionRepository.save(new LedgerTransaction(
                principal.userId(), order.getId(), OrderSide.BUY, totalAmount, "Bought " + quantity + " " + stock.symbol()));
        return toOrderResponse(order);
    }

    @Transactional
    public OrderResponse sell(JwtPrincipal principal, OrderRequest request, String authorization) {
        String symbol = normalizeSymbol(request.symbol());
        BigDecimal quantity = quantity(request.quantity());
        StockSnapshot stock = marketClient.findStock(symbol, authorization);
        BigDecimal price = price(stock.referencePrice());
        BigDecimal totalAmount = money(price.multiply(quantity));

        PortfolioAccount account = getOrCreateAccount(principal.userId());
        Holding holding = holdingRepository.findByUserIdAndSymbol(principal.userId(), stock.symbol())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "No holdings available for this stock"));
        if (holding.getQuantity().compareTo(quantity) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient quantity to sell");
        }

        holding.sell(quantity);
        if (holding.isEmpty()) {
            holdingRepository.delete(holding);
        }
        account.credit(totalAmount);

        TradeOrder order = orderRepository.save(new TradeOrder(
                principal.userId(), stock.symbol(), stock.name(), OrderSide.SELL, quantity, price, totalAmount));
        transactionRepository.save(new LedgerTransaction(
                principal.userId(), order.getId(), OrderSide.SELL, totalAmount, "Sold " + quantity + " " + stock.symbol()));
        return toOrderResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> orderHistory(JwtPrincipal principal) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(principal.userId()).stream()
                .map(this::toOrderResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> transactions(JwtPrincipal principal) {
        return transactionRepository.findByUserIdOrderByCreatedAtDesc(principal.userId()).stream()
                .map(this::toTransactionResponse)
                .toList();
    }

    private PortfolioAccount getOrCreateAccount(Long userId) {
        return accountRepository.findByUserId(userId)
                .orElseGet(() -> accountRepository.save(new PortfolioAccount(userId, startingCash)));
    }

    private PortfolioSummaryResponse summary(PortfolioAccount account, List<HoldingResponse> holdings) {
        BigDecimal holdingsValue = money(holdings.stream()
                .map(HoldingResponse::marketValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        BigDecimal investedValue = money(holdings.stream()
                .map(HoldingResponse::investedValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        BigDecimal unrealizedPnl = money(holdingsValue.subtract(investedValue));
        return new PortfolioSummaryResponse(
                account.getCashBalance(),
                holdingsValue,
                money(account.getCashBalance().add(holdingsValue)),
                investedValue,
                unrealizedPnl,
                percent(unrealizedPnl, investedValue));
    }

    private HoldingResponse toHoldingResponse(Holding holding, String authorization) {
        StockSnapshot stock = marketClient.findStock(holding.getSymbol(), authorization);
        BigDecimal lastPrice = price(stock.referencePrice());
        BigDecimal investedValue = money(holding.getAveragePrice().multiply(holding.getQuantity()));
        BigDecimal marketValue = money(lastPrice.multiply(holding.getQuantity()));
        BigDecimal pnl = money(marketValue.subtract(investedValue));
        return new HoldingResponse(
                holding.getSymbol(),
                stock.name(),
                holding.getQuantity(),
                holding.getAveragePrice(),
                lastPrice,
                investedValue,
                marketValue,
                pnl,
                percent(pnl, investedValue));
    }

    private OrderResponse toOrderResponse(TradeOrder order) {
        return new OrderResponse(
                order.getId(),
                order.getSymbol(),
                order.getStockName(),
                order.getSide(),
                order.getQuantity(),
                order.getPrice(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getCreatedAt());
    }

    private TransactionResponse toTransactionResponse(LedgerTransaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getOrderId(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getDescription(),
                transaction.getCreatedAt());
    }

    private String normalizeSymbol(String symbol) {
        return symbol.trim().toUpperCase();
    }

    private BigDecimal quantity(BigDecimal value) {
        return value.setScale(4, RoundingMode.HALF_UP);
    }

    private BigDecimal price(BigDecimal value) {
        return value.setScale(4, RoundingMode.HALF_UP);
    }

    private BigDecimal money(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal percent(BigDecimal amount, BigDecimal base) {
        if (base.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO.setScale(2);
        }
        return amount.multiply(ONE_HUNDRED).divide(base, 2, RoundingMode.HALF_UP);
    }
}
