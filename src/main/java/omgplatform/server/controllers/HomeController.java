package omgplatform.server.controllers;

import omgplatform.server.entities.User;
import omgplatform.server.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("adduser")
    public User addUser(@RequestBody User user){
        return userService.addUser(user);
    }
}
