package omgplatform.server.controllers;

import omgplatform.server.entities.User;
import omgplatform.server.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Contains HTTP routes and associated logic.
 *
 * @authors Clement Luo,
 * @date April 15, 2025
 */
@RestController
public class HomeController {

    //SERVICES

    //User account service
    @Autowired
    UserService userService;

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
     * @param user a json object containing the necessary information
     * @return returns a request code
     */
    @PostMapping("adduser")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            User registeredUser = userService.register(user);
            return new ResponseEntity<>(registeredUser, HttpStatus.CREATED); // More appropriate status for creation
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(Map.of("error", ex.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return new ResponseEntity<>(Map.of("error", "Internal server error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Account login
     * URL: /login
     *
     * @param user a json object containing the nece
     * @return returns a request code
     */
    @PostMapping("login")
    public ResponseEntity<User> login(@RequestBody User user) {
        User savedUser = userService.login(user);
        return ResponseEntity.ok(savedUser);
    }
}
