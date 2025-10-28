package com.iot.management.controller.api.user;

import com.iot.management.controller.ControllerHelper;
import com.iot.management.model.entity.NguoiDung;
import com.iot.management.service.NguoiDungService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * Controller for user-related APIs (profile retrieval and update).
 */

@RestController
@RequestMapping("/api/users")
// @PreAuthorize("hasRole('USER') or hasRole('MANAGER')")  // Tạm thời bỏ kiểm tra quyền
public class NguoiDungController {

    private final ControllerHelper controllerHelper;
    private final NguoiDungService nguoiDungService;

    public NguoiDungController(ControllerHelper controllerHelper, NguoiDungService nguoiDungService) {
        this.controllerHelper = controllerHelper;
        this.nguoiDungService = nguoiDungService;
    }

    // Lấy thông tin profile của người dùng đang đăng nhập
    @GetMapping({"/me", "/profile"})
    public ResponseEntity<NguoiDung> getCurrentUser(Principal principal) {
        NguoiDung currentUser = controllerHelper.getUserFromPrincipal(principal);
        // Cần xóa mật khẩu trước khi trả về để đảm bảo an toàn
        currentUser.setMatKhauBam(null);
        return ResponseEntity.ok(currentUser);
    }

    /**
     * Cho phép cập nhật thông tin profile nhưng KHÔNG cho phép thay đổi email.
     * Các trường có thể cập nhật: tenDangNhap, matKhau (plain), kichHoat.
     */
    @PutMapping({"/profile"})
    public ResponseEntity<?> updateProfile(Principal principal, @RequestBody ProfileUpdateRequest req) {
        NguoiDung currentUser = controllerHelper.getUserFromPrincipal(principal);

        // Never allow changing email through this endpoint
        // If client sends an email field in payload (we don't accept it), ignore it.

        boolean updated = false;
        if (req.getTenDangNhap() != null && !req.getTenDangNhap().isBlank()) {
            currentUser.setTenDangNhap(req.getTenDangNhap());
            updated = true;
        }
        if (req.getMatKhau() != null && !req.getMatKhau().isBlank()) {
            // delegate password encoding to service
            nguoiDungService.updatePassword(currentUser.getMaNguoiDung(), req.getMatKhau());
            updated = true;
        }
        if (req.getKichHoat() != null) {
            currentUser.setKichHoat(req.getKichHoat());
            updated = true;
        }

        if (updated) {
            // save other updatable fields (username, kichHoat). Password already handled.
            nguoiDungService.save(currentUser);
        }

        currentUser.setMatKhauBam(null);
        return ResponseEntity.ok(currentUser);
    }
}