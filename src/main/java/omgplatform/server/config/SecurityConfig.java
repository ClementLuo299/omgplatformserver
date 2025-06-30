package omgplatform.server.config;

import omgplatform.server.utils.JWTFilter;
import omgplatform.server.utils.LoggingUtil;
import org.springframework.beans.factory.annotation.Autowired;
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
 * Configuration for security features.
 *
 * @authors Clement Luo,
 * @date April 15, 2025
 * @edited June 29, 2025
 * @since 1.0
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JWTFilter jwtFilter;

    /**
     * Set password encoder
     *
     * @return the password encoder
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        LoggingUtil.info("Initializing BCrypt password encoder");
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        LoggingUtil.info("BCrypt password encoder initialized successfully");
        return encoder;
    }

    /**
     * Secure production filter chain
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        LoggingUtil.info("Configuring security filter chain (production mode)");
        try {
            http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/users/register").permitAll() // Allow registration
                    .requestMatchers("/users/login").permitAll() // Allow login
                    .requestMatchers("/game").permitAll() // Allow WebSocket handshake
                    .requestMatchers("/users/**").authenticated() // Secure all other user endpoints
                    .requestMatchers("/api/**").authenticated() // Secure all other API endpoints
                    .anyRequest().permitAll() // Allow other requests (for development)
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

            LoggingUtil.info("Security filter chain configured successfully (production mode)");
            return http.build();
        } catch (Exception e) {
            LoggingUtil.error("Failed to configure security filter chain", e);
            throw e;
        }
    }

    /**
     * CORS configuration for trusted origins
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
            "http://localhost:3000",
            "https://localhost:3000",
            "http://localhost:3001",
            "https://localhost:3001",
            "http://localhost:8080",
            "https://localhost:8080"
        )); // Set your frontend origin(s) - supporting both HTTP and HTTPS
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of(
            "Authorization", 
            "Content-Type", 
            "Accept", 
            "Origin", 
            "X-Requested-With",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers"
        ));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L); // 1 hour cache for preflight requests
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
