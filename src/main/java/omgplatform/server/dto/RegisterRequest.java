package omgplatform.server.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Represents a request object for account registration.
 *
 * @authors Clement Luo,
 * @date May 10, 2025
 * @edited June 27, 2025
 * @since 1.0
 */
@Data
public class RegisterRequest {
    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    private String fullName;

    @NotNull(message = "Date of birth is required")
    private java.time.LocalDate dateOfBirth;
}
