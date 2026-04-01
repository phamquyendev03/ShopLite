package com.quyen.shoplite.util;

import com.quyen.shoplite.util.error.IdInvalidException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

/**
 * JWT utility dùng Spring OAuth2 JwtEncoder / JwtDecoder (Nimbus).
 * - Sinh access token / refresh token
 * - Trích xuất current user từ SecurityContextHolder
 */
@Component
public class SecurityUtil {

    public static final MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS512;

    @Value("${shoplite.jwt.access-token-expiration}")
    private long accessTokenExpiration;     // giây

    @Value("${shoplite.jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;    // giây

    private final JwtEncoder jwtEncoder;

    public SecurityUtil(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    // ─── Generate ─────────────────────────────────────────────────────────────

    /**
     * Sinh access token với subject là username và claim "role".
     */
    public String generateAccessToken(String username, String role) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("shoplite")
                .issuedAt(now)
                .expiresAt(now.plus(accessTokenExpiration, ChronoUnit.SECONDS))
                .subject(username)
                .claim("role", role)
                .build();

        JwsHeader header = JwsHeader.with(JWT_ALGORITHM).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    /**
     * Sinh refresh token (không chứa claim role).
     */
    public String generateRefreshToken(String username) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("shoplite")
                .issuedAt(now)
                .expiresAt(now.plus(refreshTokenExpiration, ChronoUnit.SECONDS))
                .subject(username)
                .build();

        JwsHeader header = JwsHeader.with(JWT_ALGORITHM).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    // ─── Current User ─────────────────────────────────────────────────────────

    public static Optional<String> getCurrentUserLogin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()
                && !"anonymousUser".equals(auth.getPrincipal())) {
            return Optional.of(auth.getName());
        }
        return Optional.empty();
    }

    public static String requireCurrentUserLogin() {
        return getCurrentUserLogin()
                .orElseThrow(() -> new IdInvalidException("Chưa đăng nhập hoặc token không hợp lệ"));
    }
}
