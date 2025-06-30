package omgplatform.server.utils;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import omgplatform.server.utils.LoggingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Filter for JWT tokens.
 *
 * @authors Clement Luo,
 * @date May 11, 2025
 * @edited May 11, 2025
 * @since 1.0
 */
@Component
public class JWTFilter extends OncePerRequestFilter {

    @Autowired
    private JWTUtil jwtutil;

    public JWTFilter() {
        LoggingUtil.info("JWT Filter initialized");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        String authHeader = request.getHeader("Authorization");
        
        LoggingUtil.debug("JWT Filter processing request", Map.of(
            "method", method,
            "uri", requestURI,
            "hasAuthHeader", authHeader != null,
            "authHeaderLength", authHeader != null ? authHeader.length() : 0
        ));

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);
            LoggingUtil.debug("JWT token found in request", Map.of(
                "tokenLength", jwt.length(),
                "method", method,
                "uri", requestURI
            ));
            
            try {
                jwtutil.validateToken(jwt); // Only validates, throws if invalid
                String username = jwtutil.getUsernameFromToken(jwt); // Extract username
                
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(username, null, List.of());
                SecurityContextHolder.getContext().setAuthentication(auth);
                
                LoggingUtil.info("JWT authentication successful", Map.of(
                    "username", username,
                    "method", method,
                    "uri", requestURI
                ));
                
            } catch (JwtException e) {
                LoggingUtil.warn("JWT token validation failed", Map.of(
                    "method", method,
                    "uri", requestURI,
                    "error", e.getMessage()
                ));
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            } catch (Exception e) {
                LoggingUtil.error("Unexpected error during JWT authentication", e);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        } else {
            LoggingUtil.debug("No JWT token found in request", Map.of(
                "method", method,
                "uri", requestURI
            ));
        }

        LoggingUtil.debug("JWT Filter completed, continuing filter chain", Map.of(
            "method", method,
            "uri", requestURI
        ));
        filterChain.doFilter(request, response);
    }
}
