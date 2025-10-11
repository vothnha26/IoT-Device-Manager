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
        // validate required fields
        if (request.getMaThietBi() == null) {
            return ResponseEntity.badRequest().body("maThietBi is required");
        }
        if (request.getTenTruong() == null || request.getTenTruong().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("tenTruong is required");
        }
        if (request.getPhepToan() == null || request.getPhepToan().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("phepToan is required");
        }
        if (request.getGiaTriNguong() == null || request.getGiaTriNguong().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("giaTriNguong is required");
        }

        // map to entity
        LuatNguong luat = new LuatNguong();
        luat.setTenTruong(request.getTenTruong());
        luat.setPhepToan(request.getPhepToan());
        // support numeric "giaTri" from clients or string giaTriNguong
        if (request.getGiaTri() != null) {
            luat.setGiaTriNguong(String.valueOf(request.getGiaTri()));
        } else {
            luat.setGiaTriNguong(request.getGiaTriNguong());
        }

        // support alias fields
        if (request.getHanhDong() != null && !request.getHanhDong().isBlank()) {
            luat.setLenhHanhDong(request.getHanhDong());
        } else {
            luat.setLenhHanhDong(request.getLenhHanhDong());
        }
        luat.setKichHoat(request.getKichHoat() != null ? request.getKichHoat() : true);

        // find device and set relation
        java.util.Optional<ThietBi> maybeDevice = thietBiService.findDeviceById(request.getMaThietBi());
        if (maybeDevice.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Device with id " + request.getMaThietBi() + " not found");
        }
        luat.setThietBi(maybeDevice.get());

        // TODO: verify user permission on device
        LuatNguong savedRule = tuDongHoaService.saveRule(luat);
        return new ResponseEntity<>(savedRule, HttpStatus.CREATED);
    }

    @GetMapping("/device/{deviceId}")
    public ResponseEntity<?> getRulesForDevice(@PathVariable Long deviceId, Principal principal) {
        // TODO: check permissions
        java.util.List<LuatNguong> rules = tuDongHoaService.findRulesByDevice(deviceId);
        return ResponseEntity.ok(rules);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRule(@PathVariable Long id, Principal principal) {
        // Cần thêm logic kiểm tra quyền sở hữu trước khi xóa
        tuDongHoaService.deleteRule(id);
        return ResponseEntity.ok("Xóa luật thành công!");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRule(@PathVariable Long id, @RequestBody RuleRequest request, Principal principal) {
        java.util.Optional<LuatNguong> maybe = tuDongHoaService.findRuleById(id);
        if (maybe.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Rule not found");
        }

        LuatNguong luat = maybe.get();

        // update fields if provided
        if (request.getTenTruong() != null && !request.getTenTruong().isBlank()) {
            luat.setTenTruong(request.getTenTruong());
        }
        if (request.getPhepToan() != null && !request.getPhepToan().isBlank()) {
            luat.setPhepToan(request.getPhepToan());
        }
        if (request.getGiaTri() != null) {
            luat.setGiaTriNguong(String.valueOf(request.getGiaTri()));
        } else if (request.getGiaTriNguong() != null && !request.getGiaTriNguong().isBlank()) {
            luat.setGiaTriNguong(request.getGiaTriNguong());
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
    }
}