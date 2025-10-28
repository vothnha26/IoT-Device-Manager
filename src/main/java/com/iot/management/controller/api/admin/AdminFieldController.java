package com.iot.management.controller.api.admin;

import com.iot.management.model.entity.CauHinhTruongDuLieu;
import com.iot.management.model.entity.LoaiThietBi;
import com.iot.management.service.CauHinhTruongDuLieuService;
import com.iot.management.service.LoaiThietBiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminFieldController {

    @Autowired
    private CauHinhTruongDuLieuService cauHinhTruongDuLieuService;

    @Autowired
    private LoaiThietBiService loaiThietBiService;

    @GetMapping("/device-types/{id}/fields")
    public ResponseEntity<List<CauHinhTruongDuLieu>> listByDeviceType(@PathVariable Long id) {
        List<CauHinhTruongDuLieu> list = cauHinhTruongDuLieuService.layTatCaTruongTheoLoaiThietBi(id);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/fields/{id}")
    public ResponseEntity<?> getField(@PathVariable Long id) {
        Optional<CauHinhTruongDuLieu> opt = cauHinhTruongDuLieuService.layChiTietTruongDuLieu(id);
        return opt.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/device-types/{id}/fields")
    public ResponseEntity<?> createField(@PathVariable Long id, @RequestBody CauHinhTruongDuLieu field) {
        try {
            Optional<LoaiThietBi> lt = loaiThietBiService.findById(id);
            if (lt.isEmpty()) return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Loại thiết bị không tồn tại"));
            field.setLoaiThietBi(lt.get());
            CauHinhTruongDuLieu created = cauHinhTruongDuLieuService.taoMoiTruongDuLieu(field);
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", true);
            resp.put("message", "Tạo trường thành công");
            resp.put("data", created);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PutMapping("/fields/{id}")
    public ResponseEntity<?> updateField(@PathVariable Long id, @RequestBody CauHinhTruongDuLieu field) {
        try {
            CauHinhTruongDuLieu updated = cauHinhTruongDuLieuService.capNhatTruongDuLieu(id, field);
            return ResponseEntity.ok(Map.of("success", true, "message", "Cập nhật thành công", "data", updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @DeleteMapping("/fields/{id}")
    public ResponseEntity<?> deleteField(@PathVariable Long id) {
        try {
            cauHinhTruongDuLieuService.xoaTruongDuLieu(id);
            return ResponseEntity.ok(Map.of("success", true, "message", "Xóa thành công"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
