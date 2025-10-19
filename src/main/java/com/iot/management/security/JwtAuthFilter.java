package com.iot.management.security;

import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
            String token = null;
            
            System.out.println("=== JWT FILTER ===");
            System.out.println("Request URI: " + request.getRequestURI());
            
            // 1. Try to get token from Authorization header (for API calls)
            final String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7).trim();
                System.out.println("Token from Authorization header");
            }
            
            // 2. If no header token, try to get from Cookie (for browser requests)
            if (token == null || token.isEmpty()) {
                Cookie[] cookies = request.getCookies();
                System.out.println("Cookies: " + (cookies != null ? cookies.length : 0));
                if (cookies != null) {
                    for (Cookie cookie : cookies) {
                        System.out.println("Cookie: " + cookie.getName() + " = " + cookie.getValue().substring(0, Math.min(20, cookie.getValue().length())) + "...");
                        if ("authToken".equals(cookie.getName())) {
                            token = cookie.getValue();
                            System.out.println("Token found in cookie!");
                            break;
                        }
                    }
                }
            }

            // No token found - continue without authentication
            if (token == null || token.isEmpty()) {
                System.out.println("No token found - continuing without auth");
                filterChain.doFilter(request, response);
                return;
            }

            // Extract and validate token
            String email = jwtUtil.extractUsername(token);
            System.out.println("Email from token: " + email);
            
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                if (jwtUtil.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    System.out.println("Authentication set in SecurityContext for: " + email);
                } else {
                    System.out.println("Token validation failed");
                }
            }
            
            filterChain.doFilter(request, response);
            
        } catch (Exception e) {
            // Log full stacktrace for debugging
            System.out.println("Authentication error: " + e.getMessage());
            e.printStackTrace();
            filterChain.doFilter(request, response);
        }
    }
}