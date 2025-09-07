package com.iot.management.controller.api.auth;

import com.iot.management.model.dto.auth.AuthRequest;
import com.iot.management.model.dto.auth.AuthResponse;
import com.iot.management.model.dto.auth.ForgotPasswordRequest;
import com.iot.management.model.dto.auth.ResetPasswordRequest;
import com.iot.management.model.dto.auth.UserRegisterRequest;
import com.iot.management.model.dto.auth.VerifyAccountRequest;
import com.iot.management.model.entity.Role;
import com.iot.management.model.entity.User;
import com.iot.management.model.repository.RoleRepository;
import com.iot.management.model.repository.UserRepository;
import com.iot.management.security.JwtUtil;
import com.iot.management.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final EmailService emailService;

    public AuthController(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, UserDetailsService userDetailsService, EmailService emailService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.emailService = emailService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserRegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setFullName(registerRequest.getFullName());

        Optional<Role> userRole = roleRepository.findByName("ROLE_USER");
        user.setRoles(new HashSet<>(Collections.singletonList(userRole.orElseThrow(() -> new RuntimeException("ROLE_USER not found.")))));

        // Tạo và lưu mã xác thực
        String verificationCode = generateRandomCode();
        user.setVerificationCode(verificationCode);
        user.setVerificationCodeExpiry(LocalDateTime.now().plusMinutes(15)); // Mã hết hạn sau 15 phút

        userRepository.save(user);

        // Gửi email chứa mã xác thực
        emailService.sendVerificationCode(user.getEmail(), verificationCode);

        return ResponseEntity.ok("User registered successfully. A verification code has been sent to your email.");
    }

    // Endpoint mới để xác thực tài khoản
    @PostMapping("/verify-account")
    public ResponseEntity<String> verifyAccount(@RequestBody VerifyAccountRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElse(null);

        if (user == null || user.getIsActive()) {
            return ResponseEntity.badRequest().body("Invalid request or user is already active.");
        }

        if (user.getVerificationCode() == null || !user.getVerificationCode().equals(request.getCode())) {
            return ResponseEntity.badRequest().body("Invalid verification code.");
        }

        if (LocalDateTime.now().isAfter(user.getVerificationCodeExpiry())) {
            return ResponseEntity.badRequest().body("Verification code has expired.");
        }

        // Kích hoạt tài khoản
        user.setIsActive(true);
        user.setVerificationCode(null);
        user.setVerificationCodeExpiry(null);
        userRepository.save(user);

        return ResponseEntity.ok("Account verified successfully.");
    }

    // Tạo mã ngẫu nhiên 6 chữ số
    private String generateRandomCode() {
        return String.valueOf(new Random().nextInt(900000) + 100000);
    }
    
    // Đăng nhập
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getEmail());

        if (!userDetails.isEnabled()) {
            // Tùy chỉnh phản hồi cho tài khoản chưa kích hoạt
            return ResponseEntity.status(401).body(new AuthResponse(null, "Account is not active. Please verify your email."));
        }

        if (passwordEncoder.matches(authRequest.getPassword(), userDetails.getPassword())) {
            String token = jwtUtil.generateToken(userDetails.getUsername());
            AuthResponse response = new AuthResponse();
            response.setToken(token);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body(null);
        }
    }
    
    // Xử lý yêu cầu quên mật khẩu (gửi OTP)
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found.");
        }

        // Tạo mã xác thực mới
        String verificationCode = generateRandomCode();
        user.setVerificationCode(verificationCode);
        user.setVerificationCodeExpiry(LocalDateTime.now().plusMinutes(15)); // Mã hết hạn sau 15 phút
        userRepository.save(user);

        // Gửi email chứa mã xác thực
        emailService.sendVerificationCode(user.getEmail(), verificationCode);

        return ResponseEntity.ok("A password reset link has been sent to your email.");
    }

    // Đặt lại mật khẩu (sử dụng OTP)
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElse(null);

        // Kiểm tra người dùng, mã OTP và thời gian hết hạn
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found.");
        }
        
        if (user.getVerificationCode() == null || !user.getVerificationCode().equals(request.getCode())) {
            return ResponseEntity.badRequest().body("Invalid verification code.");
        }

        if (LocalDateTime.now().isAfter(user.getVerificationCodeExpiry())) {
            return ResponseEntity.badRequest().body("Verification code has expired.");
        }

        // Cập nhật mật khẩu mới và xóa mã xác thực cũ
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setVerificationCode(null);
        user.setVerificationCodeExpiry(null);
        userRepository.save(user);

        return ResponseEntity.ok("Password has been reset successfully.");
    }
}