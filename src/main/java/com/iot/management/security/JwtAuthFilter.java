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
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String path = request.getRequestURI();
        
        // Skip filter cho static resources và public paths
        if (path.startsWith("/css/") || path.startsWith("/js/") || path.startsWith("/images/")
            || path.startsWith("/webjars/") || path.equals("/favicon.ico")
            || path.startsWith("/auth/") || path.startsWith("/api/auth/") || path.startsWith("/api/public/")
            || path.startsWith("/h2-console/") || path.equals("/error")
            || path.equals("/") || path.equals("/dashboard")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        try {
            String token = null;

            System.out.println("=== JWT FILTER ===");
            System.out.println("Request URI: " + request.getRequestURI());

            // 1️⃣ Lấy token từ Authorization header
            final String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7).trim();
                System.out.println("Token from Authorization header");
            }

            // 2️⃣ Nếu không có token trong header, thử lấy trong cookie
            if (token == null || token.isEmpty()) {
                Cookie[] cookies = request.getCookies();
                if (cookies != null) {
                    for (Cookie cookie : cookies) {
                        if ("authToken".equals(cookie.getName())) {
                            token = cookie.getValue();
                            System.out.println("Token found in cookie!");
                            break;
                        }
                    }
                }
            }

            // 3️⃣ Không có token -> tiếp tục chuỗi filter mà không xác thực
            if (token == null || token.isEmpty()) {
                filterChain.doFilter(request, response);
                return;
            }

            // 4️⃣ Giải mã email từ token
            String email = jwtUtil.extractUsername(token);

            // 5️⃣ Nếu hợp lệ, thiết lập Authentication
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                if (jwtUtil.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    System.out.println("Authentication set for user: " + email);
                } else {
                    System.out.println("Token validation failed");
                }
            }

            // 6️⃣ Cho phép request tiếp tục
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            System.err.println("Authentication error: " + e.getMessage());
            // Nếu là expired token và path không yêu cầu auth, chỉ cần bỏ qua
            if (e.getMessage().contains("expired") || e.getMessage().contains("JWT expired")) {
                System.out.println("Token expired, continuing without authentication");
                filterChain.doFilter(request, response);
                return;
            }
            e.printStackTrace();
            handleAuthError(response, e.getMessage());
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
        String safeMessage = message == null ? "Authentication failed" : message.replace("\"", "\\\"");
        String json = String.format("{\"error\": \"Authentication failed\", \"message\": \"%s\"}", safeMessage);
        response.getWriter().write(json);
    }
}
