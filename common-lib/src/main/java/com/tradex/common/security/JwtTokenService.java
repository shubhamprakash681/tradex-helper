package com.tradex.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;

public class JwtTokenService {
    private final JwtProperties properties;
    private final SecretKey signingKey;

    public JwtTokenService(JwtProperties properties) {
        this.properties = properties;
        this.signingKey = Keys.hmacShaKeyFor(padSecret(properties.getSecret()).getBytes(StandardCharsets.UTF_8));
    }

    public String createAccessToken(Long userId, String email, List<String> roles) {
        return createToken(userId, email, roles, Duration.ofMinutes(properties.getAccessTokenMinutes()), "access");
    }

    public String createRefreshToken(Long userId, String email, List<String> roles) {
        return createToken(userId, email, roles, Duration.ofDays(properties.getRefreshTokenDays()), "refresh");
    }

    public JwtPrincipal parse(String token) {
        Claims claims = parseClaims(token);
        Object rolesValue = claims.get("roles");
        List<String> roles = rolesValue instanceof List<?> values
                ? values.stream().map(String::valueOf).toList()
                : List.of();
        return new JwtPrincipal(Long.valueOf(claims.getSubject()), claims.get("email", String.class), roles);
    }

    public boolean isRefreshToken(String token) {
        return "refresh".equals(parseClaims(token).get("typ", String.class));
    }

    private String createToken(Long userId, String email, List<String> roles, Duration ttl, String type) {
        Instant now = Instant.now();
        return Jwts.builder()
                .issuer(properties.getIssuer())
                .subject(String.valueOf(userId))
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(ttl)))
                .claim("email", email)
                .claim("roles", roles)
                .claim("typ", type)
                .signWith(signingKey)
                .compact();
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .requireIssuer(properties.getIssuer())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private String padSecret(String secret) {
        if (secret.length() >= 32) {
            return secret;
        }
        return secret + "0".repeat(32 - secret.length());
    }
}
