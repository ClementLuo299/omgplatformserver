package omgplatform.server.services;

import omgplatform.server.dto.LoginRequest;
import omgplatform.server.dto.RegisterRequest;
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
     * @param request the response entity to be registered
     * @return the user object
     */
    public User register(RegisterRequest request) {

        //Check to see if username or password is empty
        if(request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username Cannot Be Empty");
        }

        if(request.getPassword() == null || request.getPassword().trim().isEmpty()){
            throw new IllegalArgumentException("Password Cannot Be Empty");
        }

        //Check if username is taken
        if(userRepository.findByUsername(request.getUsername()).isPresent()) {
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
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setDateOfBirth(request.getDateOfBirth());
        return userRepository.save(user);
    }

    /**
     *
     */
    public User login(LoginRequest request) throws Exception {
        //Check to see if username or password is empty
        if(request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username Cannot Be Empty");
        }

        if(request.getPassword() == null || request.getPassword().trim().isEmpty()){
            throw new IllegalArgumentException("Password Cannot Be Empty");
        }

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new Exception("User Not Found"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new Exception("Invalid credentials");
        }
        return user;
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
