package com.iot.management.controller.api.admin;

import com.iot.management.model.entity.LoaiThietBi;
import com.iot.management.service.LoaiThietBiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/device-types")
@PreAuthorize("hasRole('ADMIN')")
public class AdminDeviceTypeController {

    @Autowired
    private LoaiThietBiService loaiThietBiService;

    @GetMapping
    public ResponseEntity<List<LoaiThietBi>> getAllDeviceTypes() {
        List<LoaiThietBi> deviceTypes = loaiThietBiService.findAllDeviceTypes();
        return ResponseEntity.ok(deviceTypes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDeviceTypeById(@PathVariable Long id) {
        return loaiThietBiService.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createDeviceType(@RequestBody LoaiThietBi loaiThietBi) {
        try {
            LoaiThietBi created = loaiThietBiService.createDeviceType(loaiThietBi);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Tạo loại thiết bị thành công");
            response.put("data", created);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Lỗi khi tạo loại thiết bị: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateDeviceType(@PathVariable Long id, @RequestBody LoaiThietBi loaiThietBi) {
        try {
            LoaiThietBi updated = loaiThietBiService.updateDeviceType(id, loaiThietBi);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Cập nhật loại thiết bị thành công");
            response.put("data", updated);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Lỗi khi cập nhật loại thiết bị: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDeviceType(@PathVariable Long id) {
        try {
            loaiThietBiService.deleteDeviceType(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Xóa loại thiết bị thành công");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Lỗi khi xóa loại thiết bị: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
