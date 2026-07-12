package com.tradex.portfolio.repository;

import com.tradex.portfolio.entity.LedgerTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LedgerTransactionRepository extends JpaRepository<LedgerTransaction, Long> {
    List<LedgerTransaction> findByUserIdOrderByCreatedAtDesc(Long userId);
}
