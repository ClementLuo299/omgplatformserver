package omgplatform.server.services;

import omgplatform.server.entities.User;
import omgplatform.server.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Contains business logic for user accounts.
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

    //CONSTRUCTOR

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    //METHODS

    /**
     * Get list of all users
     *
     * @return A list of all the users
     */
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    /**
     * Check if a username is available
     *
     * @param username the username to check
     * @return If the username is available
     */
    public boolean isUsernameAvailable(String username) {
        return !userRepository.existsByUsername(username);
    }

    /**
     * Remove a user
     *
     * @param id the id of the user
     */
    public void removeUser(Long id) {
        userRepository.deleteById(id);
    }

    /**
     * Register a user
     *
     * @param user the user entity to be registered
     * @return the user object
     */
    public User register(User user) {

        //Check to see if username or password is empty
        if(user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username Cannot Be Empty");
        }

        if(user.getPassword() == null || user.getPassword().trim().isEmpty()){
            throw new IllegalArgumentException("Password Cannot Be Empty");
        }

        //Check if username is taken
        if(userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username Is Already Taken");
        }

        //Check username and password conditions
        if(!checkUsername()){
            throw new IllegalArgumentException("Username Is Invalid");
        }

        if(!checkPassword()){
            throw new IllegalArgumentException("Password Is Invalid");
        }

        //Hash password and save user
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    /**
     *
     */
    public User login(User user) {
        //
        return null;
    }

    /**
     *
     */
    private boolean checkUsername() {
        return true;
    }

    /**
     *
     */
    private boolean checkPassword() {
        return true;
    }
}
