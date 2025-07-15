package omgplatform.server.config;

import lombok.experimental.UtilityClass;

/**
 * JWT configuration constants for the OMG Platform Server.
 *
 * @authors Clement Luo,
 * @date June 29, 2025
 * @edited July 14, 2025
 * @since 1.0
 */
@UtilityClass
public class JWTConfig {

    // Default JWT token expiration time in minutes (24 hours)
    public static final int DEFAULT_EXPIRY_MINUTES = 1440;

    // Default secret key for signing JWT tokens
    public static final String DEFAULT_SECRET = "yourSuperSecretKeyThatIsAtLeast256BitsLongForHS256Algorithm123!@#";

    // Default signature algorithm (HMAC with SHA-256)
    public static final String DEFAULT_SIGNATURE_ALGORITHM = "HS256";
}
