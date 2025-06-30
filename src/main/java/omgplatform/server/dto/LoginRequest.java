package omgplatform.server.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Represents a request object for account login.
 *
 * @authors Clement Luo,
 * @date May 11, 2025
 * @edited May 11, 2025
 * @since 1.0
 */
@Data
public class LoginRequest {
    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;
}
