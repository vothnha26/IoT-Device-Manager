package com.iot.management.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter authFilter;
    private final UserDetailsService userDetailsService;

    public SecurityConfig(JwtAuthFilter authFilter, UserDetailsService userDetailsService) {
        this.authFilter = authFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/test/**")
                .ignoringRequestMatchers("/api/auth/**")
                .ignoringRequestMatchers("/du-an/**")  // Ignore CSRF for khu vuc endpoints
                .ignoringRequestMatchers("/api/du-an/**")         // Ignore CSRF for du an endpoints
                .ignoringRequestMatchers("/api/**")     
                .ignoringRequestMatchers("/thiet-bi/**")  
                .ignoringRequestMatchers("/logout")   
                .ignoringRequestMatchers("/api/schedules/**")
                .ignoringRequestMatchers("/api/package-limit/**")   // Ignore CSRF for all API endpoints
            )
            .cors(cors -> cors.disable())
            .authorizeHttpRequests(auth -> auth
                // public API for authentication and public resources
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/schedules/**").permitAll()
                .requestMatchers("/api/package-limit/**").permitAll()
                // Auth UI pages (login, register, verify, forgot-password, reset-password)
                .requestMatchers("/auth/**").permitAll()
                // H2 console and error page
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/error").permitAll()
                // Allow demo UI, root and static assets
                .requestMatchers("/", "/dashboard").permitAll()
                .requestMatchers("/static/**", "/js/**", "/css/**", "/images/**", "/webjars/**").permitAll()
                .requestMatchers("/favicon.ico").permitAll()
                .requestMatchers("/ws/**").permitAll()
                .requestMatchers("/api/test/**").permitAll()
                .requestMatchers("/videos/**").permitAll()
                // Admin routes require ADMIN role
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                // API endpoints require authentication
                .requestMatchers("/api/**").authenticated()
                .requestMatchers("/du-an/**").authenticated()
                .requestMatchers("/thiet-bi/**").permitAll()
                // everything else requires authentication
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            // cấu hình logout cho JWT
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessHandler((request, response, authentication) -> {
                    // Xóa cookie chứa token
                    Cookie cookie = new Cookie("authToken", null);
                    cookie.setHttpOnly(true);
                    cookie.setMaxAge(0);
                    cookie.setPath("/");
                    response.addCookie(cookie);

                    // Redirect về homepage
                    response.sendRedirect("/");
                })
                .permitAll()
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @SuppressWarnings("deprecation")
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }
}
