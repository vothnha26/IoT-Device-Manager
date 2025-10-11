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
            logger.error("Authentication error: " + e.getMessage());
            handleAuthError(response, "Authentication failed");
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
        response.getWriter().write(String.format(
            "{\"error\": \"%s\", \"message\": \"Please login at /api/auth/login first\"}",
            message
        ));
    }
}