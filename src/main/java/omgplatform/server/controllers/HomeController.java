package omgplatform.server.controllers;

import omgplatform.server.entities.User;
import omgplatform.server.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contains web logic for user accounts.
 *
 * @authors Clement Luo,
 * @date April 15, 2025
 */
@RestController
public class HomeController {

    //SERVICES

    @Autowired
    UserService userService;

    //ROUTES

    @GetMapping("hello")
    public String sayHello(){
        return "Hello";
    }

    @GetMapping("getusers")
    public List<User> getUsers() {
        return userService.getUsers();
    }

    // Endpoint for user registration
    @PostMapping("adduser")
    public ResponseEntity<User> register(@RequestBody User user) {
        User savedUser = userService.register(user);
        return ResponseEntity.ok(savedUser);
    }
}
