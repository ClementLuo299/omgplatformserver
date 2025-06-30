package omgplatform.server.controllers;

import jakarta.validation.Valid;
import omgplatform.server.dto.LoginRequest;
import omgplatform.server.dto.LoginResponse;
import omgplatform.server.dto.RegisterRequest;
import omgplatform.server.dto.RegisterResponse;
import omgplatform.server.entities.User;
import omgplatform.server.services.UserService;
import omgplatform.server.utils.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
        return userService.getUsers();
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
        User user = userService.register(request);
        RegisterResponse response = new RegisterResponse(user.getUsername());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
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
        try {
            User user = userService.login(request);
            String token = jwtUtil.generateToken(user.getUsername());
            return ResponseEntity.ok(new LoginResponse(token));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}
