package omgplatform.server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents a response object for account registration.
 *
 * @authors Clement Luo,
 * @date May 10, 2025
 * @edited May 11, 2025
 * @since 1.0
 */
@Getter
@AllArgsConstructor
public class RegisterResponse {
    private String username;
}
