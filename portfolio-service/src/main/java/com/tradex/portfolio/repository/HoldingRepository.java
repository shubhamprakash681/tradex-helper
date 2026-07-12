package com.tradex.portfolio.repository;

import com.tradex.portfolio.entity.Holding;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HoldingRepository extends JpaRepository<Holding, Long> {
    List<Holding> findByUserIdOrderBySymbolAsc(Long userId);

    Optional<Holding> findByUserIdAndSymbol(Long userId, String symbol);
}
