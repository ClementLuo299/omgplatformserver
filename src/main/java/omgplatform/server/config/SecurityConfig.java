package omgplatform.server.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import omgplatform.server.utils.JWTFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Spring Security configuration for the OMG Platform server.
 * 
 * This class configures:
 * - Password encoding using BCrypt
 * - HTTP security with JWT authentication
 * - CORS settings for cross-origin requests
 * - URL-based authorization rules
 * 
 * @authors Clement Luo,
 * @date April 15, 2025
 * @edited July 14, 2025
 * @since 1.0
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    private final JWTFilter jwtFilter;

    /**
     * Creates and configures the BCrypt password encoder bean.
     * 
     * BCrypt is used for password hashing because it:
     * - Automatically generates salt for each password
     * - Is computationally intensive, making brute force attacks harder
     * - Is the recommended password hashing algorithm for Spring Security
     *
     * @return BCryptPasswordEncoder configured for password hashing
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        log.info("Initializing BCrypt password encoder");
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        log.info("BCrypt password encoder initialized successfully");
        return encoder;
    }

    /**
     * Configures the main security filter chain for HTTP requests.
     * 
     * This method sets up:
     * - CSRF protection (disabled for API endpoints)
     * - CORS configuration for cross-origin requests
     * - URL-based authorization rules
     * - JWT filter integration
     * 
     * Authorization rules:
     * - /users/register and /users/login: Public access for authentication
     * - /users/**: Requires authentication for user management
     * - /api/**: Requires authentication for API endpoints
     * - All other requests: Permitted (for development flexibility)
     *
     * @param http HttpSecurity object to configure
     * @return SecurityFilterChain with all security configurations applied
     * @throws Exception if security configuration fails
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("Configuring security filter chain (production mode)");
        try {
            http
                // Disable CSRF for API endpoints since we're using JWT tokens
                .csrf(AbstractHttpConfigurer::disable)
                // Configure CORS to allow cross-origin requests from trusted domains
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // Define authorization rules for different URL patterns
                .authorizeHttpRequests(auth -> auth
                    // Public endpoints - no authentication required
                    .requestMatchers("/users/register").permitAll() // User registration endpoint
                    .requestMatchers("/users/login").permitAll() // User login endpoint
                    // Protected endpoints - require valid JWT token
                    .requestMatchers("/users/**").authenticated() // All other user management endpoints
                    .requestMatchers("/api/**").authenticated() // All API endpoints
                    // Development fallback - allow other requests for flexibility
                    .anyRequest().permitAll() // Allow other requests (for development)
                )
                // Add JWT filter before the default username/password authentication filter
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

            log.info("Security filter chain configured successfully (production mode)");
            return http.build();
        } catch (Exception e) {
            log.error("Failed to configure security filter chain", e);
            throw e;
        }
    }

    /**
     * Configures CORS (Cross-Origin Resource Sharing) settings for the application.
     * 
     * This configuration allows:
     * - Specific trusted origins (localhost variants for development)
     * - Common HTTP methods (GET, POST, PUT, DELETE, OPTIONS, PATCH)
     * - Essential headers including Authorization for JWT tokens
     * - Credentials (cookies, authorization headers) to be sent with requests
     * - Preflight request caching for 1 hour to improve performance
     * 
     * Note: In production, replace localhost origins with actual frontend domain(s)
     *
     * @return CorsConfigurationSource with CORS settings applied to all endpoints
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Define trusted origins that can access the API
        // In production, replace with actual frontend domain(s)
        configuration.setAllowedOrigins(List.of(
            "http://localhost:3000",  // React development server (HTTP)
            "https://localhost:3000", // React development server (HTTPS)
            "http://localhost:3001",  // Alternative React port (HTTP)
            "https://localhost:3001", // Alternative React port (HTTPS)
            "http://localhost:8080",  // Spring Boot default port (HTTP)
            "https://localhost:8080"  // Spring Boot default port (HTTPS)
        ));
        
        // Allow common HTTP methods for API operations
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        
        // Allow essential headers including Authorization for JWT tokens
        configuration.setAllowedHeaders(List.of(
            "Authorization",           // For JWT token authentication
            "Content-Type",           // For request body content type
            "Accept",                 // For response content negotiation
            "Origin",                 // For CORS origin information
            "X-Requested-With",       // For AJAX request identification
            "Access-Control-Request-Method",  // For CORS preflight requests
            "Access-Control-Request-Headers"  // For CORS preflight requests
        ));
        
        // Expose Authorization header to client for JWT token access
        configuration.setExposedHeaders(List.of("Authorization"));
        
        // Allow credentials (cookies, authorization headers) to be sent with requests
        configuration.setAllowCredentials(true);
        
        // Cache preflight requests for 1 hour to improve performance
        configuration.setMaxAge(3600L);
        
        // Apply CORS configuration to all endpoints
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
