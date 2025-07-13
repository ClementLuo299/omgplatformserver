package omgplatform.server.config;

import lombok.experimental.UtilityClass;

/**
 * JWT configuration constants for the OMG Platform Server.
 * Contains only static constants, not meant to be instantiated or used as a Spring bean.
 *
 * @authors Clement Luo,
 * @date June 29, 2025
 * @edited July 13, 2025
 * @since 1.0
 */
@UtilityClass
public class JWTConfig {
    // Default values
    public static final int DEFAULT_EXPIRY_MINUTES = 1440;
    public static final String DEFAULT_SECRET = "yourSuperSecretKeyThatIsAtLeast256BitsLongForHS256Algorithm123!@#";
    public static final String DEFAULT_SIGNATURE_ALGORITHM = "HS256";

    // Configuration keys
    public static final String EXPIRY_MINUTES_KEY = "jwt.expiry.minutes";
    public static final String SECRET_KEY = "jwt.secret";
    public static final String SIGNATURE_ALGORITHM_KEY = "jwt.signature.algorithm";
}
