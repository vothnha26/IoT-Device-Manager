package com.iot.management.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JwtAuthFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
        throws ServletException, IOException {
        try {
            final String authHeader = request.getHeader("Authorization");
            final String requestURI = request.getRequestURI();
            
            // Skip authentication for non-secured paths
            if (!isAuthenticationRequired(request)) {
                filterChain.doFilter(request, response);
                return;
            }

            // No auth header or invalid format
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                handleAuthError(response, "Unauthorized - No valid token provided");
                return;
            }

            // Extract and validate token
            String token = authHeader.substring(7).trim();
            if (token.isEmpty()) {
                handleAuthError(response, "Unauthorized - Empty token");
                return;
            }

            String email = jwtUtil.extractUsername(token);
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                if (jwtUtil.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    filterChain.doFilter(request, response);
                } else {
                    handleAuthError(response, "Invalid token");
                }
            } else {
                handleAuthError(response, "Invalid token");
            }
        } catch (Exception e) {
            // Log full stacktrace for debugging
            logger.error("Authentication error", e);
            String msg = e.getMessage() == null ? "Authentication failed" : e.getMessage();
            handleAuthError(response, msg);
        }
    }

    private boolean isAuthenticationRequired(HttpServletRequest request) {
        String path = request.getRequestURI();
        return !(
            path.startsWith("/api/auth/") || 
            path.startsWith("/api/public/") || 
            path.startsWith("/h2-console/") ||
            path.equals("/error")
        );
    }

    private void handleAuthError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        // Return a consistent error field and include the specific message for easier debugging/client handling.
        String safeMessage = message == null ? "Authentication failed" : message.replace("\"", "\\\"");
        String json = String.format("{\"error\": \"Authentication failed\", \"message\": \"%s\"}", safeMessage);
        response.getWriter().write(json);
    }
}