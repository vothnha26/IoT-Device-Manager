package com.iot.management.controller.api.limit;

import com.iot.management.exception.PackageExpiredException;
import com.iot.management.exception.PackageLimitExceededException;
import com.iot.management.model.entity.DangKyGoi;
import com.iot.management.model.entity.DuAn;
import com.iot.management.model.entity.NguoiDung;
import com.iot.management.model.repository.NguoiDungRepository;
import com.iot.management.security.JwtUtil;
import com.iot.management.service.DangKyGoiService;
import com.iot.management.service.DuAnService;
import com.iot.management.service.PackageLimitService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/package-limit")
public class PackageLimitController {

    private final PackageLimitService packageLimitService;
    private final DuAnService duAnService;
    private final DangKyGoiService dangKyGoiService;
    private final JwtUtil jwtUtil = new JwtUtil();
    private final NguoiDungRepository userRepository;

    public PackageLimitController(PackageLimitService packageLimitService,
                                  DuAnService duAnService,
                                  DangKyGoiService dangKyGoiService,
                                  NguoiDungRepository userRepository) {
        this.packageLimitService = packageLimitService;
        this.duAnService = duAnService;
        this.dangKyGoiService = dangKyGoiService;
        this.userRepository = userRepository;
    }

    // 🔹 Helper chung để lấy người dùng từ cookie JWT
    private NguoiDung getNguoiDungFromToken(HttpServletRequest request) {
        String token = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("authToken".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }
        if (token == null || token.isEmpty()) {
            throw new RuntimeException("Không tìm thấy token, vui lòng đăng nhập lại.");
        }

        String email = jwtUtil.extractUsername(token);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng."));
    }

    // ✅ 1. Kiểm tra toàn bộ giới hạn
    @GetMapping("/validate/{duAnId}")
    public ResponseEntity<?> validateAllLimits(@PathVariable Long duAnId, HttpServletRequest request) {
        try {
            NguoiDung nguoiDung = getNguoiDungFromToken(request);
            DuAn duAn = duAnService.findByIdAndNguoiDung(duAnId, nguoiDung)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy dự án hoặc không thuộc quyền sở hữu"));

            packageLimitService.validatePackageStatus(duAn);
            packageLimitService.validateZoneLimit(duAn);
            packageLimitService.validateDeviceLimit(duAn);
            packageLimitService.validateRuleLimit(duAn);

            return ResponseEntity.ok("✅ Dự án hợp lệ, chưa đạt giới hạn gói cước");
        } catch (PackageLimitExceededException | PackageExpiredException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (RuntimeException ex) {
            return ResponseEntity.status(404).body(ex.getMessage());
        }
    }

    // ✅ 2. Lấy gói hiện tại (đã có sẵn)
    @GetMapping("/current-package")
    public ResponseEntity<?> getCurrentPackage(HttpServletRequest request) {
        try {
            NguoiDung nguoiDung = getNguoiDungFromToken(request);

            DangKyGoi dangKyGoi = nguoiDung.getDangKyGois().stream()
                    .filter(dkg -> "DA_THANH_TOAN".equals(dkg.getTrangThai()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Người dùng chưa đăng ký gói cước hoặc không có gói đang hoạt động"));

            if (dangKyGoi.getNgayKetThuc() == null && dangKyGoi.getGoiCuoc() != null) {
                Integer soNgayLuuDuLieu = dangKyGoi.getGoiCuoc().getSoNgayLuuDuLieu();
                if (soNgayLuuDuLieu != null && soNgayLuuDuLieu > 0 && dangKyGoi.getNgayBatDau() != null) {
                    LocalDateTime ngayKetThuc = dangKyGoi.getNgayBatDau().plusDays(soNgayLuuDuLieu);
                    dangKyGoi.setNgayKetThuc(ngayKetThuc);
                    dangKyGoiService.save(dangKyGoi);
                }
            }

            Map<String, Object> data = new HashMap<>();
            data.put("maGoi", dangKyGoi.getGoiCuoc().getMaGoiCuoc());
            data.put("tenGoi", dangKyGoi.getGoiCuoc().getTenGoi());
            data.put("giaTien", dangKyGoi.getGoiCuoc().getGiaTien());
            data.put("ngayBatDau", dangKyGoi.getNgayBatDau());
            data.put("ngayKetThuc", dangKyGoi.getNgayKetThuc());
            data.put("trangThai", dangKyGoi.getTrangThai());

            return ResponseEntity.ok(data);

        } catch (RuntimeException ex) {
            return ResponseEntity.status(404).body(ex.getMessage());
        }
    }

    // ✅ 3. Các giới hạn riêng (zone, device, rule)
    @GetMapping("/validate-zone/{duAnId}")
    public ResponseEntity<?> validateZoneLimit(@PathVariable Long duAnId, HttpServletRequest request) {
        return validateSingleLimit(duAnId, request, "zone");
    }

    @GetMapping("/validate-device/{duAnId}")
    public ResponseEntity<?> validateDeviceLimit(@PathVariable Long duAnId, HttpServletRequest request) {
        return validateSingleLimit(duAnId, request, "device");
    }

    @GetMapping("/validate-rule/{duAnId}")
    public ResponseEntity<?> validateRuleLimit(@PathVariable Long duAnId, HttpServletRequest request) {
        return validateSingleLimit(duAnId, request, "rule");
    }

    private ResponseEntity<?> validateSingleLimit(Long duAnId, HttpServletRequest request, String type) {
        try {
            NguoiDung nguoiDung = getNguoiDungFromToken(request);
            DuAn duAn = duAnService.findByIdAndNguoiDung(duAnId, nguoiDung)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy dự án hoặc không thuộc quyền sở hữu"));

            packageLimitService.validatePackageStatus(duAn);

            switch (type) {
                case "zone" -> packageLimitService.validateZoneLimit(duAn);
                case "device" -> packageLimitService.validateDeviceLimit(duAn);
                case "rule" -> packageLimitService.validateRuleLimit(duAn);
            }

            return ResponseEntity.ok("✅ Chưa đạt giới hạn cho " + type);
        } catch (PackageLimitExceededException | PackageExpiredException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (RuntimeException ex) {
            return ResponseEntity.status(404).body(ex.getMessage());
        }
    }

    // ✅ 4. Kiểm tra gói hết hạn
    @GetMapping("/expired/{duAnId}")
    public ResponseEntity<?> checkExpiredPackage(@PathVariable Long duAnId, HttpServletRequest request) {
        try {
            NguoiDung nguoiDung = getNguoiDungFromToken(request);
            DuAn duAn = duAnService.findByIdAndNguoiDung(duAnId, nguoiDung)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy dự án hoặc không thuộc quyền sở hữu"));

            DangKyGoi dangKyGoi = packageLimitService.getCurrentPackage(duAn);
            boolean expired = packageLimitService.isPackageExpired(dangKyGoi);

            Map<String, Object> result = new HashMap<>();
            result.put("tenGoi", dangKyGoi.getGoiCuoc().getTenGoi());
            result.put("expired", expired);
            result.put("ngayKetThuc", dangKyGoi.getNgayKetThuc());
            result.put("message", expired ? "⛔ Gói cước đã hết hạn" : "✅ Gói cước vẫn còn hiệu lực");

            return ResponseEntity.ok(result);

        } catch (RuntimeException ex) {
            return ResponseEntity.status(404).body(ex.getMessage());
        }
    }
}
