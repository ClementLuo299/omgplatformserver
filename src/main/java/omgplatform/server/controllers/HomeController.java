package omgplatform.server.controllers;

import omgplatform.server.entities.User;
import omgplatform.server.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
     * Gets all of the users
     * URL: /getusers
     *
     * @return a list of all of the users
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
    public ResponseEntity<User> register(@RequestBody User user) {
        User savedUser = userService.register(user);
        return ResponseEntity.ok(savedUser);
    }
}
