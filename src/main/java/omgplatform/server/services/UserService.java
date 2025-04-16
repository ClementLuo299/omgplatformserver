package omgplatform.server.services;

import omgplatform.server.entities.User;
import omgplatform.server.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Contains general logic for user accounts.
 *
 * @authors Clement Luo,
 * @date April 15, 2025
 */
@Service
public class UserService {

    //ATTRIBUTES

    //User repository
    @Autowired
    UserRepository userRepository;

    public UserService() {}

    //GETTERS

    //METHODS

    /**
     *
     */
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    /**
     *
     */
    public User addUser(User user) {
        return userRepository.save(user);
    }
}
