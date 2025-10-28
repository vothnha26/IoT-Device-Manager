package com.iot.management.controller.api.automation;

import com.iot.management.model.entity.LuatNguong;
import com.iot.management.model.entity.ThietBi;
import com.iot.management.service.ThietBiService;
import com.iot.management.service.TuDongHoaService; // Giả sử service này đã được tạo
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/rules")
// @PreAuthorize("hasRole('USER') or hasRole('MANAGER')")  // Tạm thời bỏ kiểm tra quyền
public class LuatNguongController {

    private final TuDongHoaService tuDongHoaService;
    private final ThietBiService thietBiService;

    public LuatNguongController(TuDongHoaService tuDongHoaService, ThietBiService thietBiService) {
        this.tuDongHoaService = tuDongHoaService;
        this.thietBiService = thietBiService;
    }

    @PostMapping
    public ResponseEntity<?> createRule(@RequestBody RuleRequest request, Principal principal) {
        try {
            // validate required fields: expression-only mode
            if (request.getMaThietBi() == null) {
                return ResponseEntity.badRequest().body("maThietBi is required");
            }
            if (request.getBieuThucLogic() == null || request.getBieuThucLogic().isBlank()) {
                return ResponseEntity.badRequest().body("bieuThucLogic is required");
            }

            // map to entity (expression only)
            LuatNguong luat = new LuatNguong();
            luat.setBieuThucLogic(request.getBieuThucLogic());
            
            // Set ten_truong to avoid NULL constraint (use expression as default)
            if (request.getTenTruong() != null && !request.getTenTruong().isBlank()) {
                luat.setTenTruong(request.getTenTruong());
            } else {
                // Set a default value to satisfy NOT NULL constraint
                luat.setTenTruong("bieu_thuc");
            }

            // support alias fields - ensure lenhHanhDong is never null
            String action = null;
            if (request.getHanhDong() != null && !request.getHanhDong().isBlank()) {
                action = request.getHanhDong();
            } else if (request.getLenhHanhDong() != null && !request.getLenhHanhDong().isBlank()) {
                action = request.getLenhHanhDong();
            }
            
            // Set default action if not provided
            if (action == null || action.isBlank()) {
                action = "hoat_dong"; // Default action
            }
            luat.setLenhHanhDong(action);
            
            luat.setKichHoat(request.getKichHoat() != null ? request.getKichHoat() : true);
            if (request.getThoiGianDuyTriDieuKien() != null) {
                luat.setThoiGianDuyTriDieuKien(request.getThoiGianDuyTriDieuKien());
            }

            // find device and set relation
            java.util.Optional<ThietBi> maybeDevice = thietBiService.findDeviceById(request.getMaThietBi());
            if (maybeDevice.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Device with id " + request.getMaThietBi() + " not found");
            }
            luat.setThietBi(maybeDevice.get());

            // TODO: verify user permission on device
            LuatNguong savedRule = tuDongHoaService.saveRule(luat);
            return new ResponseEntity<>(savedRule, HttpStatus.CREATED);
        } catch (Exception e) {
            // Log the full error for debugging
            System.err.println("Error creating rule: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error creating rule: " + e.getMessage());
        }
    }

    @GetMapping("/device/{deviceId}")
    public ResponseEntity<?> getRulesForDevice(@PathVariable Long deviceId, Principal principal) {
        // TODO: check permissions
        java.util.List<LuatNguong> rules = tuDongHoaService.findRulesByDevice(deviceId);
        return ResponseEntity.ok(rules);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRuleById(@PathVariable Long id, Principal principal) {
        // TODO: check permissions
        java.util.Optional<LuatNguong> rule = tuDongHoaService.findRuleById(id);
        if (rule.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Rule not found");
        }
        return ResponseEntity.ok(rule.get());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRule(@PathVariable Long id, Principal principal) {
        // Cần thêm logic kiểm tra quyền sở hữu trước khi xóa
        tuDongHoaService.deleteRule(id);
        return ResponseEntity.ok("Xóa luật thành công!");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRule(@PathVariable Long id, @RequestBody RuleRequest request, Principal principal) {
        try {
            java.util.Optional<LuatNguong> maybe = tuDongHoaService.findRuleById(id);
            if (maybe.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Rule not found");
            }

            LuatNguong luat = maybe.get();

            // update fields if provided (expression-only)
            if (request.getTenTruong() != null && !request.getTenTruong().isBlank()) {
                luat.setTenTruong(request.getTenTruong());
            } else if (luat.getTenTruong() == null || luat.getTenTruong().isBlank()) {
                // Ensure ten_truong is never null (database constraint)
                luat.setTenTruong("bieu_thuc");
            }
            if (request.getBieuThucLogic() != null) {
                luat.setBieuThucLogic(request.getBieuThucLogic());
            }
            if (request.getThoiGianDuyTriDieuKien() != null) {
                luat.setThoiGianDuyTriDieuKien(request.getThoiGianDuyTriDieuKien());
            }
            if (request.getHanhDong() != null && !request.getHanhDong().isBlank()) {
                luat.setLenhHanhDong(request.getHanhDong());
            } else if (request.getLenhHanhDong() != null && !request.getLenhHanhDong().isBlank()) {
                luat.setLenhHanhDong(request.getLenhHanhDong());
            }
            if (request.getKichHoat() != null) {
                luat.setKichHoat(request.getKichHoat());
            }

            if (request.getMaThietBi() != null) {
                java.util.Optional<ThietBi> maybeDevice = thietBiService.findDeviceById(request.getMaThietBi());
                if (maybeDevice.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Device with id " + request.getMaThietBi() + " not found");
                }
                luat.setThietBi(maybeDevice.get());
            }

            LuatNguong saved = tuDongHoaService.saveRule(luat);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            // Log the full error for debugging
            System.err.println("Error updating rule: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error updating rule: " + e.getMessage());
        }
    }
}