package omgplatform.server.config;

import omgplatform.server.utils.LoggingUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Spring configuration class for JWT settings.
 * Contains JWT configuration values from application properties.
 *
 * @authors Clement Luo,
 * @date June 29, 2025
 * @edited July 13, 2025
 * @since 1.0
 */
@Configuration
public class JWTConfig {
    
    // Default values
    public static final int DEFAULT_EXPIRY_MINUTES = 1440;
    public static final String DEFAULT_SECRET = "yourSuperSecretKeyThatIsAtLeast256BitsLongForHS256Algorithm123!@#";
    public static final String DEFAULT_SIGNATURE_ALGORITHM = "HS256";
    
    // Configuration keys
    public static final String EXPIRY_MINUTES_KEY = "jwt.expiry.minutes";
    public static final String SECRET_KEY = "jwt.secret";
    public static final String SIGNATURE_ALGORITHM_KEY = "jwt.signature.algorithm";
    
    @Value("${" + EXPIRY_MINUTES_KEY + ":" + DEFAULT_EXPIRY_MINUTES + "}")
    private int expiryMinutes;

    @Value("${" + SECRET_KEY + ":" + DEFAULT_SECRET + "}")
    private String secret;

    @Value("${" + SIGNATURE_ALGORITHM_KEY + ":" + DEFAULT_SIGNATURE_ALGORITHM + "}")
    private String signatureAlgorithm;

    public JWTConfig() {
        LoggingUtil.info("Initializing JWT configuration");
        
        // Log configuration details (without exposing sensitive data)
        LoggingUtil.info("JWT Configuration loaded", Map.of(
            "expiryMinutes", expiryMinutes,
            "signatureAlgorithm", signatureAlgorithm,
            "secretLength", secret != null ? secret.length() : 0
        ));
    }

    public int getExpiryMinutes() {
        return expiryMinutes;
    }

    public String getSecret() {
        return secret;
    }

    public String getSignatureAlgorithm() {
        return signatureAlgorithm;
    }
}
