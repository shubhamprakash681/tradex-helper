package com.tradex.portfolio.repository;

import com.tradex.portfolio.entity.PortfolioAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PortfolioAccountRepository extends JpaRepository<PortfolioAccount, Long> {
    Optional<PortfolioAccount> findByUserId(Long userId);
}
