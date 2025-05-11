package omgplatform.server.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Represents a request object for account registration.
 *
 * @authors Clement Luo,
 * @date May 10, 2025
 */
@Data
public class RegisterRequest {
    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;
}
