package com.tradex.portfolio.repository;

import com.tradex.portfolio.entity.TradeOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TradeOrderRepository extends JpaRepository<TradeOrder, Long> {
    List<TradeOrder> findByUserIdOrderByCreatedAtDesc(Long userId);
}
