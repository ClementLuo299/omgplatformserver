package omgplatform.server.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import omgplatform.server.config.JWTConfig;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

/**
 * JWT token utilities.
 *
 * @authors Clement Luo,
 * @date May 11, 2025
 * @edited June 29, 2025
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JWTUtil {
    private final JWTConfig jwtConfig;

    /**
     *
     */
    public String generateToken(String username) {
        long startTime = System.currentTimeMillis();
        
        try {
            int expiryMinutes = jwtConfig.getExpiryMinutes();
            String secret = jwtConfig.getSecret();
            String alg = jwtConfig.getSignatureAlgorithm();
            SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.forName(alg);
            
            String token = Jwts.builder()
                    .setSubject(username)
                    .setIssuedAt(new Date())
                    .setExpiration(Date.from(Instant.now().plus(expiryMinutes, ChronoUnit.MINUTES)))
                    .signWith(Keys.hmacShaKeyFor(secret.getBytes()), signatureAlgorithm)
                    .compact();
            
            long duration = System.currentTimeMillis() - startTime;
            log.info("JWT token generated successfully", Map.of(
                "username", username,
                "expiryMinutes", expiryMinutes,
                "algorithm", alg,
                "tokenLength", token.length(),
                "duration", duration
            ));
            
            return token;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("Failed to generate JWT token for user: " + username, e);
            throw e;
        }
    }

    public void validateToken(String token) {
        long startTime = System.currentTimeMillis();
        
        try {
            String secret = jwtConfig.getSecret();
            Jwts.parserBuilder()
                    .setSigningKey(secret.getBytes())
                    .build()
                    .parseClaimsJws(token);
            
            long duration = System.currentTimeMillis() - startTime;
            log.debug("JWT token validation successful", Map.of(
                "tokenLength", token.length(),
                "duration", duration
            ));
            
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("JWT token validation failed", e);
            throw e;
        }
    }

    public String getUsernameFromToken(String token) {
        long startTime = System.currentTimeMillis();
        
        try {
            String secret = jwtConfig.getSecret();
            String username = Jwts.parserBuilder()
                    .setSigningKey(secret.getBytes())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
            
            long duration = System.currentTimeMillis() - startTime;
            log.debug("Username extracted from JWT token", Map.of(
                "username", username,
                "tokenLength", token.length(),
                "duration", duration
            ));
            
            return username;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("Failed to extract username from JWT token", e);
            throw e;
        }
    }

    public boolean isTokenExpired(String token) {
        long startTime = System.currentTimeMillis();
        
        try {
            String secret = jwtConfig.getSecret();
            Date expiration = Jwts.parserBuilder()
                    .setSigningKey(secret.getBytes())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();
            
            boolean expired = expiration.before(new Date());
            long duration = System.currentTimeMillis() - startTime;
            
            log.debug("JWT token expiration check", Map.of(
                "expired", expired,
                "expirationDate", expiration.toString(),
                "currentDate", new Date().toString(),
                "duration", duration
            ));
            
            return expired;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("Failed to check JWT token expiration", e);
            throw e;
        }
    }
}
