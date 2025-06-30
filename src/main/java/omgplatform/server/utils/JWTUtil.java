package omgplatform.server.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import omgplatform.server.config.JwtConfig;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

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
    private final JwtConfig jwtConfig;

    public JWTUtil(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    /**
     *
     */
    public String generateToken(String username) {
        int expiryMinutes = jwtConfig.getExpiryMinutes();
        String secret = jwtConfig.getSecret();
        String alg = jwtConfig.getSignatureAlgorithm();
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.forName(alg);
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plus(expiryMinutes, ChronoUnit.MINUTES)))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()), signatureAlgorithm)
                .compact();
    }

    public void validateToken(String token) {
        String secret = jwtConfig.getSecret();
        Jwts.parserBuilder()
                .setSigningKey(secret.getBytes())
                .build()
                .parseClaimsJws(token);
    }

    public String getUsernameFromToken(String token) {
        String secret = jwtConfig.getSecret();
        return Jwts.parserBuilder()
                .setSigningKey(secret.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean isTokenExpired(String token) {
        String secret = jwtConfig.getSecret();
        Date expiration = Jwts.parserBuilder()
                .setSigningKey(secret.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return expiration.before(new Date());
    }
}
