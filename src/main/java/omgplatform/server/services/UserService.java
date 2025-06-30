package omgplatform.server.services;

import omgplatform.server.dto.LoginRequest;
import omgplatform.server.dto.RegisterRequest;
import omgplatform.server.entities.User;
import omgplatform.server.repositories.UserRepository;
import omgplatform.server.utils.LoggingUtil;
import org.springframework.beans.factory.annotation.Autowired;
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
        LoggingUtil.info("UserService initialized with dependencies");
    }

    //METHODS

    /**
     * Get list of all users
     *
     * @return A list of all the users
     */
    public List<User> getUsers() {
        LoggingUtil.methodEntry("getUsers");
        long startTime = System.currentTimeMillis();
        
        try {
            List<User> users = userRepository.findAll();
            long duration = System.currentTimeMillis() - startTime;
            
            LoggingUtil.dbOperation("SELECT", "users", duration);
            LoggingUtil.methodExit("getUsers", "Retrieved " + users.size() + " users");
            
            return users;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            LoggingUtil.error("Failed to retrieve users from database", e);
            LoggingUtil.dbOperation("SELECT", "users", duration);
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
        LoggingUtil.methodEntry("isUsernameAvailable", Map.of("username", username));
        long startTime = System.currentTimeMillis();
        
        try {
            boolean available = !userRepository.existsByUsername(username);
            long duration = System.currentTimeMillis() - startTime;
            
            LoggingUtil.dbOperation("SELECT", "users", duration);
            LoggingUtil.methodExit("isUsernameAvailable", available);
            
            return available;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            LoggingUtil.error("Failed to check username availability for: " + username, e);
            LoggingUtil.dbOperation("SELECT", "users", duration);
            throw e;
        }
    }

    /**
     * Remove a user
     *
     * @param id the id of the user
     */
    public void removeUser(Long id) {
        LoggingUtil.methodEntry("removeUser", Map.of("userId", id));
        long startTime = System.currentTimeMillis();
        
        try {
            userRepository.deleteById(id);
            long duration = System.currentTimeMillis() - startTime;
            
            LoggingUtil.dbOperation("DELETE", "users", duration);
            LoggingUtil.methodExit("removeUser", "User with ID " + id + " removed");
            
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            LoggingUtil.error("Failed to remove user with ID: " + id, e);
            LoggingUtil.dbOperation("DELETE", "users", duration);
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
        LoggingUtil.methodEntry("register", Map.of("username", request.getUsername()));

        //Check to see if username or password is empty
        if(request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            LoggingUtil.warn("Registration attempt with empty username");
            throw new IllegalArgumentException("Username Cannot Be Empty");
        }

        if(request.getPassword() == null || request.getPassword().trim().isEmpty()){
            LoggingUtil.warn("Registration attempt with empty password for username: " + request.getUsername());
            throw new IllegalArgumentException("Password Cannot Be Empty");
        }

        //Check if username is taken
        if(userRepository.findByUsername(request.getUsername()).isPresent()) {
            LoggingUtil.warn("Registration attempt with taken username: " + request.getUsername());
            throw new IllegalArgumentException("Username Is Already Taken");
        }

        //Check username and password conditions
        if(!checkUsername()){
            LoggingUtil.warn("Registration attempt with invalid username: " + request.getUsername());
            throw new IllegalArgumentException("Username Is Invalid");
        }

        if(!checkPassword()){
            LoggingUtil.warn("Registration attempt with invalid password for username: " + request.getUsername());
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
            
            LoggingUtil.dbOperation("INSERT", "users", duration);
            LoggingUtil.methodExit("register", "User registered successfully: " + savedUser.getUsername());
            
            return savedUser;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            LoggingUtil.error("Failed to register user: " + request.getUsername(), e);
            LoggingUtil.dbOperation("INSERT", "users", duration);
            throw e;
        }
    }

    /**
     *
     */
    public User login(LoginRequest request) throws Exception {
        LoggingUtil.methodEntry("login", Map.of("username", request.getUsername()));
        
        //Check to see if username or password is empty
        if(request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            LoggingUtil.warn("Login attempt with empty username");
            throw new IllegalArgumentException("Username Cannot Be Empty");
        }

        if(request.getPassword() == null || request.getPassword().trim().isEmpty()){
            LoggingUtil.warn("Login attempt with empty password for username: " + request.getUsername());
            throw new IllegalArgumentException("Password Cannot Be Empty");
        }

        long startTime = System.currentTimeMillis();
        try {
            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new Exception("User Not Found"));
            
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                LoggingUtil.warn("Login attempt with invalid password for username: " + request.getUsername());
                throw new Exception("Invalid credentials");
            }
            
            // Update last login timestamp
            user.setLastLogin(java.time.OffsetDateTime.now());
            userRepository.save(user);
            
            long duration = System.currentTimeMillis() - startTime;
            LoggingUtil.dbOperation("SELECT", "users", duration);
            LoggingUtil.dbOperation("UPDATE", "users", System.currentTimeMillis() - startTime - duration);
            
            LoggingUtil.methodExit("login", "User logged in successfully: " + user.getUsername());
            return user;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            LoggingUtil.error("Login failed for user: " + request.getUsername(), e);
            LoggingUtil.dbOperation("SELECT", "users", duration);
            throw e;
        }
    }

    /**
     *
     */
    private boolean checkUsername() {
        LoggingUtil.debug("Checking username validation rules");
        return true;
    }

    /**
     *
     */
    private boolean checkPassword() {
        LoggingUtil.debug("Checking password validation rules");
        return true;
    }

    /**
     * Find user by username
     *
     * @param username the username to search for
     * @return Optional containing the user if found
     */
    public java.util.Optional<User> findByUsername(String username) {
        LoggingUtil.methodEntry("findByUsername", Map.of("username", username));
        long startTime = System.currentTimeMillis();
        
        try {
            java.util.Optional<User> user = userRepository.findByUsername(username);
            long duration = System.currentTimeMillis() - startTime;
            
            LoggingUtil.dbOperation("SELECT", "users", duration);
            LoggingUtil.methodExit("findByUsername", user.isPresent() ? "User found" : "User not found");
            
            return user;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            LoggingUtil.error("Failed to find user by username: " + username, e);
            LoggingUtil.dbOperation("SELECT", "users", duration);
            throw e;
        }
    }
}
