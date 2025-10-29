package com.iot.management.controller.api.admin;

import com.iot.management.model.dto.request.GoiCuocRequest;
import com.iot.management.model.entity.DangKyGoi;
import com.iot.management.model.entity.GoiCuoc;
import com.iot.management.model.entity.NguoiDung;
import com.iot.management.repository.DangKyGoiRepository;
import com.iot.management.repository.KhuVucRepository;
import com.iot.management.repository.ThietBiRepository;
import com.iot.management.service.GoiCuocService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/packages")
@PreAuthorize("hasRole('ADMIN')")
public class AdminGoiCuocController {

    @Autowired
    private GoiCuocService goiCuocService;

    @Autowired
    private DangKyGoiRepository dangKyGoiRepository;

    @Autowired
    private ThietBiRepository thietBiRepository;

    @Autowired
    private KhuVucRepository khuVucRepository;

    @GetMapping
    public ResponseEntity<List<GoiCuoc>> getAllPackages() {
        List<GoiCuoc> packages = goiCuocService.findAll();
        return ResponseEntity.ok(packages);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPackageById(@PathVariable Integer id) {
        Optional<GoiCuoc> packageOpt = goiCuocService.findById(id);
        if (packageOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(packageOpt.get());
    }

    @PostMapping
    public ResponseEntity<?> createPackage(@RequestBody GoiCuocRequest request) {
        try {
            GoiCuoc created = goiCuocService.save(request);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Tạo gói cước thành công");
            response.put("data", created);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Lỗi khi tạo gói cước: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePackage(@PathVariable Integer id, @RequestBody GoiCuocRequest request) {
        try {
            GoiCuoc updated = goiCuocService.update(id, request);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Cập nhật gói cước thành công");
            response.put("data", updated);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Lỗi khi cập nhật gói cước: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePackage(@PathVariable Integer id) {
        try {
            goiCuocService.deleteById(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Xóa gói cước thành công");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Lỗi khi xóa gói cước: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * API: Lấy danh sách người dùng đang sử dụng một gói cụ thể
     */
    @GetMapping("/{id}/users")
    public ResponseEntity<?> getPackageUsers(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<DangKyGoi> registrations = dangKyGoiRepository.findAll().stream()
                .filter(dk -> dk.getGoiCuoc() != null && 
                            dk.getGoiCuoc().getMaGoiCuoc().equals(id) &&
                            DangKyGoi.TRANG_THAI_ACTIVE.equals(dk.getTrangThai()))
                .toList();
            
            List<Map<String, Object>> users = new ArrayList<>();
            for (DangKyGoi dk : registrations) {
                if (dk.getNguoiDung() != null) {
                    Map<String, Object> userInfo = new HashMap<>();
                    NguoiDung user = dk.getNguoiDung();
                    
                    userInfo.put("maNguoiDung", user.getMaNguoiDung());
                    userInfo.put("tenDangNhap", user.getTenDangNhap());
                    userInfo.put("email", user.getEmail());
                    userInfo.put("ngayBatDau", dk.getNgayBatDau());
                    userInfo.put("ngayKetThuc", dk.getNgayKetThuc());
                    userInfo.put("trangThai", dk.getTrangThai());
                    
                    // Đếm số thiết bị của người dùng
                    long deviceCount = thietBiRepository.findByChuSoHuu_MaNguoiDung(user.getMaNguoiDung()).size();
                    userInfo.put("soThietBi", deviceCount);
                    
                    // Đếm số khu vực của người dùng
                    long areaCount = khuVucRepository.countByChuSoHuu_MaNguoiDung(user.getMaNguoiDung());
                    userInfo.put("soKhuVuc", areaCount);
                    
                    users.add(userInfo);
                }
            }
            
            response.put("success", true);
            response.put("users", users);
            response.put("total", users.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}