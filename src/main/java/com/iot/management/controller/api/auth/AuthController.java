package com.iot.management.controller.api.auth;

import com.iot.management.model.dto.auth.AuthRequest;
import com.iot.management.model.dto.auth.AuthResponse;
import com.iot.management.model.dto.auth.ForgotPasswordRequest;
import com.iot.management.model.dto.auth.ResetPasswordRequest;
import com.iot.management.model.dto.auth.UserRegisterRequest;
import com.iot.management.model.dto.auth.VerifyAccountRequest;
import com.iot.management.model.entity.VaiTro;
import com.iot.management.model.entity.NguoiDung;
import com.iot.management.model.repository.VaiTroRepository;
import com.iot.management.model.repository.NguoiDungRepository;
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

    private final NguoiDungRepository userRepository;
    private final VaiTroRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final EmailService emailService;

    public AuthController(NguoiDungRepository userRepository, VaiTroRepository roleRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, UserDetailsService userDetailsService, EmailService emailService) {
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

    NguoiDung user = new NguoiDung();
    user.setEmail(registerRequest.getEmail());
    user.setMatKhauBam(passwordEncoder.encode(registerRequest.getPassword()));
    user.setTenDangNhap(registerRequest.getEmail());
    user.setKichHoat(false); // Đăng ký xong chưa active, chờ xác thực OTP

    Optional<VaiTro> userRole = roleRepository.findByName("ROLE_USER");
    user.setVaiTro(new HashSet<>(Collections.singletonList(userRole.orElseThrow(() -> new RuntimeException("ROLE_USER role not found.")))));

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
    // Support both /verify-account and /verify-email (some clients use verify-email)
    @PostMapping({"/verify-account", "/verify-email"})
    public ResponseEntity<String> verifyAccount(@RequestBody VerifyAccountRequest request) {
    NguoiDung user = userRepository.findByEmail(request.getEmail())
        .orElse(null);

    if (user == null || user.getKichHoat()) {
            return ResponseEntity.badRequest().body("Invalid request or user is already active.");
        }

        if (user.getVerificationCode() == null || !user.getVerificationCode().equals(request.getCode())) {
            return ResponseEntity.badRequest().body("Invalid verification code.");
        }

        if (LocalDateTime.now().isAfter(user.getVerificationCodeExpiry())) {
            return ResponseEntity.badRequest().body("Verification code has expired.");
        }

        // Kích hoạt tài khoản
    user.setKichHoat(true);
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
        try {
            if (authRequest.getEmail() == null || authRequest.getPassword() == null) {
                return ResponseEntity.badRequest()
                    .body(new AuthResponse(null, "Email and password are required"));
            }

            UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getEmail());

            if (!userDetails.isEnabled()) {
                return ResponseEntity.status(401)
                    .body(new AuthResponse(null, "Account is not active. Please verify your email."));
            }

            if (passwordEncoder.matches(authRequest.getPassword(), userDetails.getPassword())) {
                String token = jwtUtil.generateToken(userDetails);  // Truyền UserDetails để có thông tin về roles
                return ResponseEntity.ok(new AuthResponse(token, "Login successful"));
            } else {
                return ResponseEntity.status(401)
                    .body(new AuthResponse(null, "Invalid credentials"));
            }
        } catch (org.springframework.security.core.userdetails.UsernameNotFoundException e) {
            return ResponseEntity.status(401)
                .body(new AuthResponse(null, "Invalid credentials"));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(new AuthResponse(null, "An error occurred during authentication"));
    }
}
    
    // Xử lý yêu cầu quên mật khẩu (gửi OTP)
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        NguoiDung user = userRepository.findByEmail(request.getEmail()).orElse(null);
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
    NguoiDung user = userRepository.findByEmail(request.getEmail()).orElse(null);

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
    user.setMatKhauBam(passwordEncoder.encode(request.getNewPassword()));
        user.setVerificationCode(null);
        user.setVerificationCodeExpiry(null);
        userRepository.save(user);

        return ResponseEntity.ok("Password has been reset successfully.");
    }
}