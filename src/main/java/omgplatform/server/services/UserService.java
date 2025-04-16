package omgplatform.server.services;

import omgplatform.server.entities.User;
import omgplatform.server.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    private final UserRepository userRepository;

    //Password hasher
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    //METHODS

    /**
     *
     */
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    // Check if the username is available
    public boolean isUsernameAvailable(String username) {
        return !userRepository.existsByUsername(username);
    }

    // Register a new user
    public boolean registerUser(String username, String password) {
        if (isUsernameAvailable(username)) {
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPassword(password);// Encrypt password
            userRepository.save(newUser);
            return true; // Registration successful
        }
        return false; // Username already taken
    }

    // Remove a user (optional, for cleanup)
    public void removeUser(Long id) {
        userRepository.deleteById(id);
    }

    public User register(User user) {
        return userRepository.save(user);
    }
}
