package com.iot.management.controller.api.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iot.management.model.entity.DangKyGoi;
import com.iot.management.model.entity.GoiCuoc;
import com.iot.management.model.entity.KhuVuc;
import com.iot.management.model.entity.NguoiDung;
import com.iot.management.model.entity.ThanhToan;
import com.iot.management.model.entity.ThietBi;
import com.iot.management.model.repository.DangKyGoiRepository;
import com.iot.management.model.repository.GoiCuocRepository;
import com.iot.management.model.repository.ThanhToanRepository;
import com.iot.management.service.NguoiDungService;
import com.iot.management.service.ThietBiService;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    @Autowired
    private NguoiDungService nguoiDungService;
    
    @Autowired
    private ThietBiService thietBiService;

    @Autowired
    private DangKyGoiRepository dangKyGoiRepository;

    @Autowired
    private GoiCuocRepository goiCuocRepository;

    @Autowired
    private ThanhToanRepository thanhToanRepository;

    @Autowired
    private com.iot.management.model.repository.KhuVucRepository khuVucRepository;

    /**
     * Lấy danh sách tất cả người dùng trong hệ thống (trừ Admin)
     */
    @GetMapping
    public ResponseEntity<List<NguoiDung>> getAllUsers() {
        List<NguoiDung> users = nguoiDungService.findAllUsers();
        
        // Lọc bỏ người dùng có vai trò ADMIN
        List<NguoiDung> nonAdminUsers = users.stream()
            .filter(user -> user.getVaiTro() == null || 
                           user.getVaiTro().stream()
                               .noneMatch(role -> "ROLE_ADMIN".equals(role.getTenVaiTro())))
            .toList();
        
        return ResponseEntity.ok(nonAdminUsers);
    }

    /**
     * Lấy thông tin chi tiết một người dùng
     */
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserDetail(@PathVariable Long userId) {
        Optional<NguoiDung> userOpt = nguoiDungService.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        NguoiDung user = userOpt.get();
        
        return ResponseEntity.ok(user);
    }

    /**
     * Kích hoạt/Vô hiệu hóa người dùng
     */
    @PutMapping("/{userId}/toggle-active")
    public ResponseEntity<?> toggleUserActive(@PathVariable Long userId) {
        Optional<NguoiDung> userOpt = nguoiDungService.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        NguoiDung user = userOpt.get();
        user.setKichHoat(!user.getKichHoat());
        nguoiDungService.save(user);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("kichHoat", user.getKichHoat());
        response.put("message", user.getKichHoat() ? "Đã kích hoạt người dùng" : "Đã vô hiệu hóa người dùng");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Lấy danh sách khu vực của người dùng (bao gồm cả khu vực được chia sẻ)
     */
    @GetMapping("/{userId}/areas")
    public ResponseEntity<?> getUserAreas(@PathVariable Long userId) {
        try {
            List<KhuVuc> areas = khuVucRepository.findAllAccessibleByUser(userId);
            
            // Convert to simple DTO to avoid circular reference issues
            List<Map<String, Object>> areaData = areas.stream()
                .map(area -> {
                    Map<String, Object> data = new HashMap<>();
                    data.put("maKhuVuc", area.getMaKhuVuc());
                    data.put("tenKhuVuc", area.getTenKhuVuc());
                    data.put("loaiKhuVuc", area.getLoaiKhuVuc());
                    data.put("moTa", area.getMoTa());
                    return data;
                })
                .toList();
            
            return ResponseEntity.ok(areaData);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error loading areas: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    /**
     * Lấy danh sách thiết bị của người dùng
     */
    @GetMapping("/{userId}/devices")
    public ResponseEntity<?> getUserDevices(@PathVariable Long userId) {
        try {
            List<ThietBi> devices = thietBiService.findDevicesByOwner(userId);
            
            // Convert to simple DTO to avoid circular reference issues
            List<Map<String, Object>> deviceData = devices.stream()
                .map(device -> {
                    Map<String, Object> data = new HashMap<>();
                    data.put("maThietBi", device.getMaThietBi());
                    data.put("tenThietBi", device.getTenThietBi());
                    data.put("loaiThietBi", device.getLoaiThietBi());
                    data.put("trangThai", device.getTrangThai());
                    return data;
                })
                .toList();
            
            return ResponseEntity.ok(deviceData);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error loading devices: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    /**
     * Lấy thống kê của người dùng
     */
    @GetMapping("/{userId}/statistics")
    public ResponseEntity<Map<String, Object>> getUserStatistics(@PathVariable Long userId) {
        List<KhuVuc> areas = khuVucRepository.findAllAccessibleByUser(userId);
        List<ThietBi> devices = thietBiService.findDevicesByOwner(userId);
        
        // Đếm thiết bị đang hoạt động (trangThai = "hoat_dong")
        long activeDevices = devices.stream()
            .filter(d -> d.getTrangThai() != null && "hoat_dong".equalsIgnoreCase(d.getTrangThai().trim()))
            .count();
        
        // Đếm số dự án mà người dùng tham gia (qua khu vực)
        long projectCount = areas.stream()
            .filter(a -> a.getDuAn() != null)
            .map(a -> a.getDuAn().getMaDuAn())
            .distinct()
            .count();
        
        // Lấy thông tin gói đang sử dụng
        DangKyGoi activePackage = dangKyGoiRepository
            .findByNguoiDung_MaNguoiDungAndTrangThai(userId, DangKyGoi.TRANG_THAI_ACTIVE)
            .orElse(null);
        
        String packageName = "Chưa có gói";
        if (activePackage != null && activePackage.getGoiCuoc() != null) {
            packageName = activePackage.getGoiCuoc().getTenGoi();
        }
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalAreas", areas.size());
        stats.put("totalDevices", devices.size());
        stats.put("activeDevices", activeDevices);
        stats.put("inactiveDevices", devices.size() - activeDevices);
        stats.put("totalProjects", projectCount);
        stats.put("currentPackage", packageName);
        
        return ResponseEntity.ok(stats);
    }

    /**
     * Gán gói cước cho người dùng bởi Admin
     */
    @PostMapping("/{userId}/assign-package")
    public ResponseEntity<?> assignPackageToUser(
            @PathVariable Long userId,
            @RequestBody Map<String, Object> request) {
        try {
            // Validate input
            if (!request.containsKey("maGoiCuoc") || !request.containsKey("soThang")) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "Thiếu thông tin gói cước hoặc số tháng");
                return ResponseEntity.badRequest().body(error);
            }
            
            Long maGoiCuoc = Long.valueOf(request.get("maGoiCuoc").toString());
            int soThang = Integer.parseInt(request.get("soThang").toString());
            
            // Validate số tháng
            if (soThang < 1 || soThang > 12) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "Số tháng phải từ 1-12");
                return ResponseEntity.badRequest().body(error);
            }
            
            // Tìm user
            Optional<NguoiDung> userOpt = nguoiDungService.findById(userId);
            if (userOpt.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "Không tìm thấy người dùng");
                return ResponseEntity.notFound().build();
            }
            
            // Tìm gói cước (ID là Integer)
            Optional<GoiCuoc> packageOpt = goiCuocRepository.findById(maGoiCuoc.intValue());
            if (packageOpt.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "Không tìm thấy gói cước");
                return ResponseEntity.badRequest().body(error);
            }
            
            NguoiDung user = userOpt.get();
            GoiCuoc goiCuoc = packageOpt.get();
            
            // Hủy gói đang active (nếu có)
            Optional<DangKyGoi> activePackageOpt = dangKyGoiRepository
                .findByNguoiDung_MaNguoiDungAndTrangThai(userId, DangKyGoi.TRANG_THAI_ACTIVE);
            if (activePackageOpt.isPresent()) {
                DangKyGoi pkg = activePackageOpt.get();
                pkg.setTrangThai(DangKyGoi.TRANG_THAI_EXPIRED);
                dangKyGoiRepository.save(pkg);
            }
            
            // Tạo bản ghi đăng ký gói mới
            DangKyGoi dangKyGoi = new DangKyGoi();
            dangKyGoi.setNguoiDung(user);
            dangKyGoi.setGoiCuoc(goiCuoc);
            dangKyGoi.setNgayBatDau(java.time.LocalDateTime.now());
            dangKyGoi.setNgayKetThuc(java.time.LocalDateTime.now().plusMonths(soThang));
            dangKyGoi.setTrangThai(DangKyGoi.TRANG_THAI_ACTIVE);
            dangKyGoi = dangKyGoiRepository.save(dangKyGoi);
            
            // Tạo bản ghi thanh toán (do admin gán nên là "COMPLETED")
            ThanhToan thanhToan = new ThanhToan();
            thanhToan.setDangKyGoi(dangKyGoi);
            thanhToan.setMaNguoiDung(user.getMaNguoiDung());
            thanhToan.setMaGoiCuoc(maGoiCuoc);
            thanhToan.setSoTien(goiCuoc.getGiaTien().multiply(new java.math.BigDecimal(soThang)));
            thanhToan.setNgayThanhToan(java.time.LocalDateTime.now());
            thanhToan.setTrangThai("DA_THANH_TOAN");
            thanhToan.setPhuongThuc("ADMIN_ASSIGN");
            thanhToan.setMaGiaoDichCongThanhToan("ADMIN_" + System.currentTimeMillis());
            thanhToanRepository.save(thanhToan);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Gán gói cước thành công cho " + user.getEmail());
            response.put("dangKyGoi", dangKyGoi);
            
            return ResponseEntity.ok(response);
            
        } catch (NumberFormatException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Dữ liệu không hợp lệ: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Lỗi khi gán gói cước: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }
}
