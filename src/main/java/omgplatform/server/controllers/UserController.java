package omgplatform.server.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import omgplatform.server.dto.LoginRequest;
import omgplatform.server.dto.LoginResponse;
import omgplatform.server.dto.RegisterRequest;
import omgplatform.server.dto.RegisterResponse;
import omgplatform.server.entities.User;
import omgplatform.server.services.UserService;
import omgplatform.server.utils.JWTUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Contains HTTP routes and associated logic.
 * URL: /users
 *
 * @authors Clement Luo,
 * @date April 15, 2025
 * @edited June 29, 2025
 * @since 1.0
 */
@RestController
@RequestMapping("users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    // Services
    private final UserService userService;
    private final JWTUtil jwtUtil;

    //ROUTES

    /**
     * Gets all the users
     * URL: /getusers
     *
     * @return a list of all the users
     */
    @GetMapping("getusers")
    public List<User> getUsers() {
        String requestId = UUID.randomUUID().toString();
        log.info("API Request: GET /users/getusers, Request ID: {}", requestId);
        
        long startTime = System.currentTimeMillis();
        try {
            List<User> users = userService.getUsers();
            long responseTime = System.currentTimeMillis() - startTime;
            log.info("API Response: GET /users/getusers, Status: 200, Request ID: {}, Response Time: {}ms", requestId, responseTime);
            log.info("Retrieved {} users", Map.of("userCount", users.size()));
            return users;
        } catch (Exception e) {
            long responseTime = System.currentTimeMillis() - startTime;
            log.info("API Response: GET /users/getusers, Status: 500, Request ID: {}, Response Time: {}ms", requestId, responseTime);
            log.error("Failed to retrieve users", e);
            throw e;
        }
    }

    /**
     * Add a user
     * URL: /adduser
     *
     * @param request a json object containing the necessary information
     * @return returns a request code
     */
    @PostMapping("register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        String requestId = UUID.randomUUID().toString();
        log.info("API Request: POST /users/register, Request ID: {}", requestId);
        
        long startTime = System.currentTimeMillis();
        try {
            log.info("Method Entry: register, Username: {}", request.getUsername());
            
            User user = userService.register(request);
            RegisterResponse response = new RegisterResponse(user.getUsername());
            
            long responseTime = System.currentTimeMillis() - startTime;
            log.info("API Response: POST /users/register, Status: 201, Request ID: {}, Response Time: {}ms", requestId, responseTime);
            log.info("Authentication Event: User {} registered successfully", request.getUsername());
            log.info("Method Exit: register, Username: {}", user.getUsername());
            
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            long responseTime = System.currentTimeMillis() - startTime;
            log.info("API Response: POST /users/register, Status: 400, Request ID: {}, Response Time: {}ms", requestId, responseTime);
            log.info("Authentication Event: User {} registration failed", request.getUsername());
            log.error("Registration failed for user: " + request.getUsername(), e);
            throw e;
        }
    }

    /**
     * Account login
     * URL: /login
     *
     * @param request a json object containing the necessary information
     * @return returns a request code
     */
    @PostMapping("login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request){
        String requestId = UUID.randomUUID().toString();
        log.info("API Request: POST /users/login, Request ID: {}", requestId);
        
        long startTime = System.currentTimeMillis();
        try {
            log.info("Method Entry: login, Username: {}", request.getUsername());
            
            User user = userService.login(request);
            String token = jwtUtil.generateToken(user.getUsername());
            
            long responseTime = System.currentTimeMillis() - startTime;
            log.info("API Response: POST /users/login, Status: 200, Request ID: {}, Response Time: {}ms", requestId, responseTime);
            log.info("Authentication Event: User {} logged in successfully", request.getUsername());
            log.info("Method Exit: login, Message: Token generated successfully");
            
            return ResponseEntity.ok(new LoginResponse(token));
        }
        catch (Exception e) {
            long responseTime = System.currentTimeMillis() - startTime;
            log.info("API Response: POST /users/login, Status: 401, Request ID: {}, Response Time: {}ms", requestId, responseTime);
            log.info("Authentication Event: User {} login failed", request.getUsername());
            log.error("Login failed for user: " + request.getUsername(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}
