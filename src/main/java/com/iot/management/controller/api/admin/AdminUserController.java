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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iot.management.model.entity.KhuVuc;
import com.iot.management.model.entity.NguoiDung;
import com.iot.management.model.entity.ThietBi;
import com.iot.management.service.KhuVucService;
import com.iot.management.service.NguoiDungService;
import com.iot.management.service.ThietBiService;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    @Autowired
    private NguoiDungService nguoiDungService;
    
    @Autowired
    private KhuVucService khuVucService;
    
    @Autowired
    private ThietBiService thietBiService;

    /**
     * Lấy danh sách tất cả người dùng trong hệ thống
     */
    @GetMapping
    public ResponseEntity<List<NguoiDung>> getAllUsers() {
        List<NguoiDung> users = nguoiDungService.findAllUsers();
        return ResponseEntity.ok(users);
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
     * Lấy danh sách khu vực của người dùng
     */
    @GetMapping("/{userId}/areas")
    public ResponseEntity<List<KhuVuc>> getUserAreas(@PathVariable Long userId) {
        List<KhuVuc> areas = khuVucService.getAllKhuVucsByUser(userId);
        return ResponseEntity.ok(areas);
    }

    /**
     * Lấy danh sách thiết bị của người dùng
     */
    @GetMapping("/{userId}/devices")
    public ResponseEntity<List<ThietBi>> getUserDevices(@PathVariable Long userId) {
        List<ThietBi> devices = thietBiService.findDevicesByOwner(userId);
        return ResponseEntity.ok(devices);
    }

    /**
     * Lấy thống kê của người dùng
     */
    @GetMapping("/{userId}/statistics")
    public ResponseEntity<Map<String, Object>> getUserStatistics(@PathVariable Long userId) {
        List<KhuVuc> areas = khuVucService.getAllKhuVucsByUser(userId);
        List<ThietBi> devices = thietBiService.findDevicesByOwner(userId);
        
        long activeDevices = devices.stream()
            .filter(d -> "hoat dong".equalsIgnoreCase(d.getTrangThai()))
            .count();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalAreas", areas.size());
        stats.put("totalDevices", devices.size());
        stats.put("activeDevices", activeDevices);
        stats.put("inactiveDevices", devices.size() - activeDevices);
        
        return ResponseEntity.ok(stats);
    }
}
