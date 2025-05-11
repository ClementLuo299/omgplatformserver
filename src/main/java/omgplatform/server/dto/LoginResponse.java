package omgplatform.server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents a response object for account login.
 *
 * @authors Clement Luo,
 * @date May 11, 2025
 */
@Getter
@AllArgsConstructor
public class LoginResponse {
    private String username;
}
