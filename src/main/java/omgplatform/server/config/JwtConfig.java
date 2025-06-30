package omgplatform.server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

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
public class JwtConfig {
    @Value("${jwt.expiry.minutes:1440}")
    private int expiryMinutes;

    @Value("${jwt.secret:yourSuperSecretKeyThatIsAtLeast256BitsLongForHS256Algorithm123!@#}")
    private String secret;

    @Value("${jwt.signature.algorithm:HS256}")
    private String signatureAlgorithm;

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
