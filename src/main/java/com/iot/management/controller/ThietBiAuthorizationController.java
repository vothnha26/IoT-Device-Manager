package com.iot.management.controller;

import com.iot.management.service.ThietBiAuthorizationService;
import com.iot.management.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/thiet-bi-authorization")
public class ThietBiAuthorizationController {

    @Autowired
    private ThietBiAuthorizationService authorizationService;

    /**
     * Kiểm tra tất cả quyền của người dùng đối với thiết bị
     */
    @GetMapping("/{maThietBi}/kiem-tra-tat-ca")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> kiemTraTatCaQuyen(@PathVariable Long maThietBi) {
        Long maNguoiDung = SecurityUtils.getCurrentUserId();
        
        Map<String, Object> quyens = new HashMap<>();
        
        // Thông tin quyền
        String loaiQuyen = authorizationService.layQuyenThietBi(maThietBi, maNguoiDung);
        quyens.put("loaiQuyen", loaiQuyen);
        quyens.put("moTa", getPermissionDescription(loaiQuyen));
        quyens.put("coQuyenTruyCap", authorizationService.coQuyenTruyCapThietBi(maThietBi, maNguoiDung));
        
        // Quyền xem (VIEW)
        Map<String, Boolean> quyenXem = new HashMap<>();
        quyenXem.put("thongTin", authorizationService.coQuyenXemThongTin(maThietBi, maNguoiDung));
        quyenXem.put("duLieu", authorizationService.coQuyenXemDuLieu(maThietBi, maNguoiDung));
        quyenXem.put("lichSu", authorizationService.coQuyenXemLichSu(maThietBi, maNguoiDung));
        quyens.put("view", quyenXem);
        
        // Quyền điều khiển (CONTROL)
        Map<String, Boolean> quyenDieuKhien = new HashMap<>();
        quyenDieuKhien.put("dieuKhien", authorizationService.coQuyenDieuKhien(maThietBi, maNguoiDung));
        quyenDieuKhien.put("guiLenh", authorizationService.coQuyenGuiLenh(maThietBi, maNguoiDung));
        quyens.put("control", quyenDieuKhien);
        
        // Quyền quản lý (MANAGE)
        Map<String, Boolean> quyenQuanLy = new HashMap<>();
        quyenQuanLy.put("chinhSuaThongTin", authorizationService.coQuyenChinhSuaThongTin(maThietBi, maNguoiDung));
        quyenQuanLy.put("cauHinh", authorizationService.coQuyenCauHinh(maThietBi, maNguoiDung));
        quyenQuanLy.put("xoa", authorizationService.coQuyenXoa(maThietBi, maNguoiDung));
        quyenQuanLy.put("chiaSe", authorizationService.coQuyenChiaSe(maThietBi, maNguoiDung));
        quyenQuanLy.put("xemDanhSachNguoiDung", authorizationService.coQuyenXemDanhSachNguoiDung(maThietBi, maNguoiDung));
        quyenQuanLy.put("quanLyLuat", authorizationService.coQuyenQuanLyLuat(maThietBi, maNguoiDung));
        quyenQuanLy.put("quanLyLichTrinh", authorizationService.coQuyenQuanLyLichTrinh(maThietBi, maNguoiDung));
        quyens.put("manage", quyenQuanLy);
        
        // Quyền đặc biệt
        Map<String, Boolean> quyenDacBiet = new HashMap<>();
        quyenDacBiet.put("thongQuaKhuVuc", authorizationService.coQuyenQuanLyThongQuaKhuVuc(maThietBi, maNguoiDung));
        quyenDacBiet.put("thongQuaDuAn", authorizationService.coQuyenQuanLyThongQuaDuAn(maThietBi, maNguoiDung));
        quyens.put("dacBiet", quyenDacBiet);
        
        return ResponseEntity.ok(quyens);
    }

    /**
     * Lấy loại quyền của người dùng
     */
    @GetMapping("/{maThietBi}/loai-quyen")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> layLoaiQuyen(@PathVariable Long maThietBi) {
        Long maNguoiDung = SecurityUtils.getCurrentUserId();
        String loaiQuyen = authorizationService.layQuyenThietBi(maThietBi, maNguoiDung);
        
        Map<String, Object> result = new HashMap<>();
        result.put("loaiQuyen", loaiQuyen);
        result.put("moTa", getPermissionDescription(loaiQuyen));
        
        return ResponseEntity.ok(result);
    }

    // ==================== QUYỀN VIEW ====================

    @GetMapping("/{maThietBi}/quyen/xem-thong-tin")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> kiemTraQuyenXemThongTin(@PathVariable Long maThietBi) {
        Long maNguoiDung = SecurityUtils.getCurrentUserId();
        boolean coQuyen = authorizationService.coQuyenXemThongTin(maThietBi, maNguoiDung);
        return ResponseEntity.ok(Map.of("coQuyen", coQuyen));
    }

    @GetMapping("/{maThietBi}/quyen/xem-du-lieu")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> kiemTraQuyenXemDuLieu(@PathVariable Long maThietBi) {
        Long maNguoiDung = SecurityUtils.getCurrentUserId();
        boolean coQuyen = authorizationService.coQuyenXemDuLieu(maThietBi, maNguoiDung);
        return ResponseEntity.ok(Map.of("coQuyen", coQuyen));
    }

    @GetMapping("/{maThietBi}/quyen/xem-lich-su")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> kiemTraQuyenXemLichSu(@PathVariable Long maThietBi) {
        Long maNguoiDung = SecurityUtils.getCurrentUserId();
        boolean coQuyen = authorizationService.coQuyenXemLichSu(maThietBi, maNguoiDung);
        return ResponseEntity.ok(Map.of("coQuyen", coQuyen));
    }

    // ==================== QUYỀN CONTROL ====================

    @GetMapping("/{maThietBi}/quyen/dieu-khien")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> kiemTraQuyenDieuKhien(@PathVariable Long maThietBi) {
        Long maNguoiDung = SecurityUtils.getCurrentUserId();
        boolean coQuyen = authorizationService.coQuyenDieuKhien(maThietBi, maNguoiDung);
        return ResponseEntity.ok(Map.of("coQuyen", coQuyen));
    }

    @GetMapping("/{maThietBi}/quyen/gui-lenh")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> kiemTraQuyenGuiLenh(@PathVariable Long maThietBi) {
        Long maNguoiDung = SecurityUtils.getCurrentUserId();
        boolean coQuyen = authorizationService.coQuyenGuiLenh(maThietBi, maNguoiDung);
        return ResponseEntity.ok(Map.of("coQuyen", coQuyen));
    }

    // ==================== QUYỀN MANAGE ====================

    @GetMapping("/{maThietBi}/quyen/chinh-sua-thong-tin")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> kiemTraQuyenChinhSuaThongTin(@PathVariable Long maThietBi) {
        Long maNguoiDung = SecurityUtils.getCurrentUserId();
        boolean coQuyen = authorizationService.coQuyenChinhSuaThongTin(maThietBi, maNguoiDung);
        return ResponseEntity.ok(Map.of("coQuyen", coQuyen));
    }

    @GetMapping("/{maThietBi}/quyen/cau-hinh")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> kiemTraQuyenCauHinh(@PathVariable Long maThietBi) {
        Long maNguoiDung = SecurityUtils.getCurrentUserId();
        boolean coQuyen = authorizationService.coQuyenCauHinh(maThietBi, maNguoiDung);
        return ResponseEntity.ok(Map.of("coQuyen", coQuyen));
    }

    @GetMapping("/{maThietBi}/quyen/xoa")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> kiemTraQuyenXoa(@PathVariable Long maThietBi) {
        Long maNguoiDung = SecurityUtils.getCurrentUserId();
        boolean coQuyen = authorizationService.coQuyenXoa(maThietBi, maNguoiDung);
        return ResponseEntity.ok(Map.of("coQuyen", coQuyen));
    }

    @GetMapping("/{maThietBi}/quyen/chia-se")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> kiemTraQuyenChiaSe(@PathVariable Long maThietBi) {
        Long maNguoiDung = SecurityUtils.getCurrentUserId();
        boolean coQuyen = authorizationService.coQuyenChiaSe(maThietBi, maNguoiDung);
        return ResponseEntity.ok(Map.of("coQuyen", coQuyen));
    }

    @GetMapping("/{maThietBi}/quyen/xem-danh-sach-nguoi-dung")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> kiemTraQuyenXemDanhSachNguoiDung(@PathVariable Long maThietBi) {
        Long maNguoiDung = SecurityUtils.getCurrentUserId();
        boolean coQuyen = authorizationService.coQuyenXemDanhSachNguoiDung(maThietBi, maNguoiDung);
        return ResponseEntity.ok(Map.of("coQuyen", coQuyen));
    }

    @GetMapping("/{maThietBi}/quyen/quan-ly-luat")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> kiemTraQuyenQuanLyLuat(@PathVariable Long maThietBi) {
        Long maNguoiDung = SecurityUtils.getCurrentUserId();
        boolean coQuyen = authorizationService.coQuyenQuanLyLuat(maThietBi, maNguoiDung);
        return ResponseEntity.ok(Map.of("coQuyen", coQuyen));
    }

    @GetMapping("/{maThietBi}/quyen/quan-ly-lich-trinh")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> kiemTraQuyenQuanLyLichTrinh(@PathVariable Long maThietBi) {
        Long maNguoiDung = SecurityUtils.getCurrentUserId();
        boolean coQuyen = authorizationService.coQuyenQuanLyLichTrinh(maThietBi, maNguoiDung);
        return ResponseEntity.ok(Map.of("coQuyen", coQuyen));
    }

    /**
     * Lấy mô tả quyền
     */
    private String getPermissionDescription(String permission) {
        if (permission == null) {
            return "Không có quyền truy cập thiết bị này";
        }
        
        switch (permission) {
            case "VIEW":
                return "Chỉ xem: Xem thông tin, dữ liệu cảm biến, lịch sử (không được điều khiển)";
            case "CONTROL":
                return "Điều khiển: Xem đầy đủ thông tin + Điều khiển thiết bị, gửi lệnh (không được chỉnh sửa cấu hình)";
            case "MANAGE":
                return "Quản lý toàn quyền: Chỉnh sửa thông tin, cấu hình, xóa, chia sẻ, quản lý luật và lịch trình";
            default:
                return "Loại quyền không xác định";
        }
    }
}
