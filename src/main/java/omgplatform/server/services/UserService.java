package omgplatform.server.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import omgplatform.server.dto.LoginRequest;
import omgplatform.server.dto.RegisterRequest;
import omgplatform.server.entities.User;
import omgplatform.server.repositories.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Contains business logic for user accounts.
 *
 * @authors Clement Luo,
 * @date April 15, 2025
 * @edited June 29, 2025
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    // Dependencies
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    //METHODS

    /**
     * Get list of all users
     *
     * @return A list of all the users
     */
    public List<User> getUsers() {
        log.debug("Method entry: getUsers");
        long startTime = System.currentTimeMillis();
        
        try {
            List<User> users = userRepository.findAll();
            long duration = System.currentTimeMillis() - startTime;
            
            log.debug("DB operation: SELECT users - {}ms", duration);
            log.info("Method exit: getUsers - Retrieved {} users", users.size());
            
            return users;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("Failed to retrieve users from database", e);
            log.debug("DB operation: SELECT users - {}ms", duration);
            throw e;
        }
    }

    /**
     * Check if a username is available
     *
     * @param username the username to check
     * @return If the username is available
     */
    public boolean isUsernameAvailable(String username) {
        log.debug("Method entry: isUsernameAvailable - username: {}", username);
        long startTime = System.currentTimeMillis();
        
        try {
            boolean available = !userRepository.existsByUsername(username);
            long duration = System.currentTimeMillis() - startTime;
            
            log.debug("DB operation: SELECT users - {}ms", duration);
            log.debug("Method exit: isUsernameAvailable - available: {}", available);
            
            return available;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("Failed to check username availability for: {}", username, e);
            log.debug("DB operation: SELECT users - {}ms", duration);
            throw e;
        }
    }

    /**
     * Remove a user
     *
     * @param id the id of the user
     */
    public void removeUser(Long id) {
        log.debug("Method entry: removeUser - userId: {}", id);
        long startTime = System.currentTimeMillis();
        
        try {
            userRepository.deleteById(id);
            long duration = System.currentTimeMillis() - startTime;
            
            log.debug("DB operation: DELETE users - {}ms", duration);
            log.info("Method exit: removeUser - User with ID {} removed", id);
            
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("Failed to remove user with ID: {}", id, e);
            log.debug("DB operation: DELETE users - {}ms", duration);
            throw e;
        }
    }

    /**
     * Register a user
     *
     * @param request the response entity to be registered
     * @return the user object
     */
    public User register(RegisterRequest request) {
        log.debug("Method entry: register - username: {}", request.getUsername());

        //Check to see if username or password is empty
        if(request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            log.warn("Registration attempt with empty username");
            throw new IllegalArgumentException("Username Cannot Be Empty");
        }

        if(request.getPassword() == null || request.getPassword().trim().isEmpty()){
            log.warn("Registration attempt with empty password for username: {}", request.getUsername());
            throw new IllegalArgumentException("Password Cannot Be Empty");
        }

        //Check if username is taken
        if(userRepository.findByUsername(request.getUsername()).isPresent()) {
            log.warn("Registration attempt with taken username: {}", request.getUsername());
            throw new IllegalArgumentException("Username Is Already Taken");
        }

        //Check username and password conditions
        if(!checkUsername()){
            log.warn("Registration attempt with invalid username: {}", request.getUsername());
            throw new IllegalArgumentException("Username Is Invalid");
        }

        if(!checkPassword()){
            log.warn("Registration attempt with invalid password for username: {}", request.getUsername());
            throw new IllegalArgumentException("Password Is Invalid");
        }

        //Hash password and save user
        long startTime = System.currentTimeMillis();
        try {
            User user = new User();
            user.setUsername(request.getUsername());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setFullName(request.getFullName());
            user.setDateOfBirth(request.getDateOfBirth());
            
            User savedUser = userRepository.save(user);
            long duration = System.currentTimeMillis() - startTime;
            
            log.debug("DB operation: INSERT users - {}ms", duration);
            log.info("Method exit: register - User registered successfully: {}", savedUser.getUsername());
            
            return savedUser;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("Failed to register user: {}", request.getUsername(), e);
            log.debug("DB operation: INSERT users - {}ms", duration);
            throw e;
        }
    }

    /**
     *
     */
    public User login(LoginRequest request) throws Exception {
        log.debug("Method entry: login - username: {}", request.getUsername());
        
        //Check to see if username or password is empty
        if(request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            log.warn("Login attempt with empty username");
            throw new IllegalArgumentException("Username Cannot Be Empty");
        }

        if(request.getPassword() == null || request.getPassword().trim().isEmpty()){
            log.warn("Login attempt with empty password for username: {}", request.getUsername());
            throw new IllegalArgumentException("Password Cannot Be Empty");
        }

        long startTime = System.currentTimeMillis();
        try {
            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new Exception("User Not Found"));
            
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                log.warn("Login attempt with invalid password for username: {}", request.getUsername());
                throw new Exception("Invalid credentials");
            }
            
            // Update last login timestamp
            user.setLastLogin(java.time.OffsetDateTime.now());
            userRepository.save(user);
            
            long duration = System.currentTimeMillis() - startTime;
            log.debug("DB operation: SELECT users - {}ms", duration);
            log.debug("DB operation: UPDATE users - {}ms", System.currentTimeMillis() - startTime - duration);
            
            log.info("Method exit: login - User logged in successfully: {}", user.getUsername());
            return user;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("Login failed for user: {}", request.getUsername(), e);
            log.debug("DB operation: SELECT users - {}ms", duration);
            throw e;
        }
    }

    /**
     *
     */
    private boolean checkUsername() {
        log.debug("Checking username validation rules");
        return true;
    }

    /**
     *
     */
    private boolean checkPassword() {
        log.debug("Checking password validation rules");
        return true;
    }

    /**
     * Find user by username
     *
     * @param username the username to search for
     * @return Optional containing the user if found
     */
    public java.util.Optional<User> findByUsername(String username) {
        log.debug("Method entry: findByUsername - username: {}", username);
        long startTime = System.currentTimeMillis();
        
        try {
            java.util.Optional<User> user = userRepository.findByUsername(username);
            long duration = System.currentTimeMillis() - startTime;
            
            log.debug("DB operation: SELECT users - {}ms", duration);
            log.debug("Method exit: findByUsername - {}", user.isPresent() ? "User found" : "User not found");
            
            return user;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("Failed to find user by username: {}", username, e);
            log.debug("DB operation: SELECT users - {}ms", duration);
            throw e;
        }
    }
}
