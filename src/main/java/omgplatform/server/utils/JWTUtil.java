package omgplatform.server.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import omgplatform.server.config.JWTConfig;
import omgplatform.server.utils.LoggingUtil;
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
public class JWTUtil {
    private final JWTConfig jwtConfig;

    public JWTUtil(JWTConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
        LoggingUtil.info("JWTUtil initialized with configuration");
        
        // Log configuration details directly
        LoggingUtil.info("JWT Configuration loaded", Map.of(
            "expiryMinutes", jwtConfig.getExpiryMinutes(),
            "signatureAlgorithm", jwtConfig.getSignatureAlgorithm(),
            "secretLength", jwtConfig.getSecret() != null ? jwtConfig.getSecret().length() : 0
        ));
    }

    /**
     *
     */
    public String generateToken(String username) {
        LoggingUtil.methodEntry("generateToken", Map.of("username", username));
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
            LoggingUtil.info("JWT token generated successfully", Map.of(
                "username", username,
                "expiryMinutes", expiryMinutes,
                "algorithm", alg,
                "tokenLength", token.length(),
                "duration", duration
            ));
            
            LoggingUtil.methodExit("generateToken", "Token generated for " + username);
            return token;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            LoggingUtil.error("Failed to generate JWT token for user: " + username, e);
            throw e;
        }
    }

    public void validateToken(String token) {
        LoggingUtil.methodEntry("validateToken", Map.of("tokenLength", token != null ? token.length() : 0));
        long startTime = System.currentTimeMillis();
        
        try {
            String secret = jwtConfig.getSecret();
            Jwts.parserBuilder()
                    .setSigningKey(secret.getBytes())
                    .build()
                    .parseClaimsJws(token);
            
            long duration = System.currentTimeMillis() - startTime;
            LoggingUtil.debug("JWT token validation successful", Map.of(
                "tokenLength", token.length(),
                "duration", duration
            ));
            
            LoggingUtil.methodExit("validateToken", "Token validation successful");
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            LoggingUtil.error("JWT token validation failed", e);
            LoggingUtil.methodExit("validateToken", "Token validation failed");
            throw e;
        }
    }

    public String getUsernameFromToken(String token) {
        LoggingUtil.methodEntry("getUsernameFromToken", Map.of("tokenLength", token != null ? token.length() : 0));
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
            LoggingUtil.debug("Username extracted from JWT token", Map.of(
                "username", username,
                "tokenLength", token.length(),
                "duration", duration
            ));
            
            LoggingUtil.methodExit("getUsernameFromToken", username);
            return username;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            LoggingUtil.error("Failed to extract username from JWT token", e);
            LoggingUtil.methodExit("getUsernameFromToken", "Failed to extract username");
            throw e;
        }
    }

    public boolean isTokenExpired(String token) {
        LoggingUtil.methodEntry("isTokenExpired", Map.of("tokenLength", token != null ? token.length() : 0));
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
            
            LoggingUtil.debug("JWT token expiration check", Map.of(
                "expired", expired,
                "expirationDate", expiration.toString(),
                "currentDate", new Date().toString(),
                "duration", duration
            ));
            
            LoggingUtil.methodExit("isTokenExpired", expired);
            return expired;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            LoggingUtil.error("Failed to check JWT token expiration", e);
            LoggingUtil.methodExit("isTokenExpired", "Failed to check expiration");
            throw e;
        }
    }
}
