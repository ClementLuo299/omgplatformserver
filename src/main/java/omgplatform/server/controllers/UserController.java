package omgplatform.server.controllers;

import jakarta.validation.Valid;
import omgplatform.server.dto.LoginRequest;
import omgplatform.server.dto.LoginResponse;
import omgplatform.server.dto.RegisterRequest;
import omgplatform.server.dto.RegisterResponse;
import omgplatform.server.entities.User;
import omgplatform.server.services.UserService;
import omgplatform.server.utils.JWTUtil;
import omgplatform.server.utils.LoggingUtil;
import org.springframework.beans.factory.annotation.Autowired;
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
public class UserController {

    //SERVICES

    //User account service
    @Autowired
    UserService userService;

    @Autowired
    private JWTUtil jwtUtil;

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
        LoggingUtil.apiRequest("GET", "/users/getusers", requestId);
        
        long startTime = System.currentTimeMillis();
        try {
            List<User> users = userService.getUsers();
            long responseTime = System.currentTimeMillis() - startTime;
            LoggingUtil.apiResponse("GET", "/users/getusers", 200, requestId, responseTime);
            LoggingUtil.info("Retrieved {} users", Map.of("userCount", users.size()));
            return users;
        } catch (Exception e) {
            long responseTime = System.currentTimeMillis() - startTime;
            LoggingUtil.apiResponse("GET", "/users/getusers", 500, requestId, responseTime);
            LoggingUtil.error("Failed to retrieve users", e);
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
        LoggingUtil.apiRequest("POST", "/users/register", requestId);
        
        long startTime = System.currentTimeMillis();
        try {
            LoggingUtil.methodEntry("register", Map.of("username", request.getUsername()));
            
            User user = userService.register(request);
            RegisterResponse response = new RegisterResponse(user.getUsername());
            
            long responseTime = System.currentTimeMillis() - startTime;
            LoggingUtil.apiResponse("POST", "/users/register", 201, requestId, responseTime);
            LoggingUtil.authEvent(request.getUsername(), "REGISTER", true);
            LoggingUtil.methodExit("register", user.getUsername());
            
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            long responseTime = System.currentTimeMillis() - startTime;
            LoggingUtil.apiResponse("POST", "/users/register", 400, requestId, responseTime);
            LoggingUtil.authEvent(request.getUsername(), "REGISTER", false);
            LoggingUtil.error("Registration failed for user: " + request.getUsername(), e);
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
        LoggingUtil.apiRequest("POST", "/users/login", requestId);
        
        long startTime = System.currentTimeMillis();
        try {
            LoggingUtil.methodEntry("login", Map.of("username", request.getUsername()));
            
            User user = userService.login(request);
            String token = jwtUtil.generateToken(user.getUsername());
            
            long responseTime = System.currentTimeMillis() - startTime;
            LoggingUtil.apiResponse("POST", "/users/login", 200, requestId, responseTime);
            LoggingUtil.authEvent(request.getUsername(), "LOGIN", true);
            LoggingUtil.methodExit("login", "Token generated successfully");
            
            return ResponseEntity.ok(new LoginResponse(token));
        }
        catch (Exception e) {
            long responseTime = System.currentTimeMillis() - startTime;
            LoggingUtil.apiResponse("POST", "/users/login", 401, requestId, responseTime);
            LoggingUtil.authEvent(request.getUsername(), "LOGIN", false);
            LoggingUtil.error("Login failed for user: " + request.getUsername(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}
