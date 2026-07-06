package com.tradex.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "revoked_tokens")
public class RevokedToken {
    @Id
    @Column(length = 128)
    private String tokenHash;

    @Column(nullable = false)
    private Instant revokedAt = Instant.now();

    protected RevokedToken() {
    }

    public RevokedToken(String tokenHash) {
        this.tokenHash = tokenHash;
    }
}
