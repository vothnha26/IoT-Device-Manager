package com.iot.management.controller.api.user;

import com.iot.management.model.entity.NguoiDung;
import com.iot.management.repository.NguoiDungRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final NguoiDungRepository nguoiDungRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(NguoiDungRepository nguoiDungRepository, PasswordEncoder passwordEncoder) {
        this.nguoiDungRepository = nguoiDungRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request, Authentication authentication) {
        try {
            if (authentication == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
            }

            String email = authentication.getName();
            Optional<NguoiDung> userOpt = nguoiDungRepository.findByEmail(email);

            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            NguoiDung user = userOpt.get();

            // Verify current password
            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getMatKhauBam())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Mật khẩu hiện tại không đúng");
            }

            // Validate new password
            if (request.getNewPassword() == null || request.getNewPassword().length() < 6) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Mật khẩu mới phải có ít nhất 6 ký tự");
            }

            // Update password
            user.setMatKhauBam(passwordEncoder.encode(request.getNewPassword()));
            nguoiDungRepository.save(user);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Đổi mật khẩu thành công");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Lỗi khi đổi mật khẩu: " + e.getMessage());
        }
    }

    @PutMapping("/update-profile")
    public ResponseEntity<?> updateProfile(@RequestBody UpdateProfileRequest request, Authentication authentication) {
        try {
            if (authentication == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
            }

            String email = authentication.getName();
            Optional<NguoiDung> userOpt = nguoiDungRepository.findByEmail(email);

            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            NguoiDung user = userOpt.get();

            // Update email if provided and different
            if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
                // Check if email already exists
                Optional<NguoiDung> existingUser = nguoiDungRepository.findByEmail(request.getEmail());
                if (existingUser.isPresent() && !existingUser.get().getMaNguoiDung().equals(user.getMaNguoiDung())) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email đã được sử dụng");
                }
                user.setEmail(request.getEmail());
            }

            nguoiDungRepository.save(user);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Cập nhật thông tin thành công");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Lỗi khi cập nhật thông tin: " + e.getMessage());
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication authentication) {
        try {
            if (authentication == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
            }

            String email = authentication.getName();
            Optional<NguoiDung> userOpt = nguoiDungRepository.findByEmail(email);

            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            NguoiDung user = userOpt.get();
            
            Map<String, Object> profile = new HashMap<>();
            profile.put("maNguoiDung", user.getMaNguoiDung());
            profile.put("tenDangNhap", user.getTenDangNhap());
            profile.put("email", user.getEmail());
            profile.put("kichHoat", user.getKichHoat());
            profile.put("ngayTao", user.getNgayTao());
            profile.put("vaiTro", user.getVaiTro());

            return ResponseEntity.ok(profile);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Lỗi khi lấy thông tin: " + e.getMessage());
        }
    }
}

class ChangePasswordRequest {
    private String currentPassword;
    private String newPassword;

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}

class UpdateProfileRequest {
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
