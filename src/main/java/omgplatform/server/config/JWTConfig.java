package omgplatform.server.config;

import omgplatform.server.utils.LoggingUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Configuration class for JWT settings.
 * Reads properties from application.properties or application.yml.
 *
 * @authors Clement Luo,
 * @date June 29, 2025
 * @edited June 29, 2025
 * @since 1.0
 */
@Configuration
public class JWTConfig {
    @Value("${jwt.expiry.minutes:1440}")
    private int expiryMinutes;

    @Value("${jwt.secret:yourSuperSecretKeyThatIsAtLeast256BitsLongForHS256Algorithm123!@#}")
    private String secret;

    @Value("${jwt.signature.algorithm:HS256}")
    private String signatureAlgorithm;

    public JWTConfig() {
        LoggingUtil.info("Initializing JWT configuration");
    }

    public int getExpiryMinutes() {
        LoggingUtil.debug("JWT expiry minutes requested: " + expiryMinutes);
        return expiryMinutes;
    }

    public String getSecret() {
        LoggingUtil.debug("JWT secret requested (length: " + (secret != null ? secret.length() : 0) + ")");
        return secret;
    }

    public String getSignatureAlgorithm() {
        LoggingUtil.debug("JWT signature algorithm requested: " + signatureAlgorithm);
        return signatureAlgorithm;
    }

    /**
     * Log JWT configuration details (without exposing sensitive data)
     */
    public void logConfiguration() {
        LoggingUtil.info("JWT Configuration loaded", Map.of(
            "expiryMinutes", expiryMinutes,
            "signatureAlgorithm", signatureAlgorithm,
            "secretLength", secret != null ? secret.length() : 0
        ));
    }
}
