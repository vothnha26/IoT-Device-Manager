package com.iot.management.controller.api.automation;

import com.iot.management.model.entity.LichTrinh;
import com.iot.management.service.TuDongHoaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/schedules")
// @PreAuthorize("hasRole('USER') or hasRole('MANAGER')")  // Tạm thời bỏ kiểm tra quyền
public class LichTrinhController {

    private final TuDongHoaService tuDongHoaService;

    public LichTrinhController(TuDongHoaService tuDongHoaService) {
        this.tuDongHoaService = tuDongHoaService;
    }

    @PostMapping
    public ResponseEntity<?> createSchedule(@RequestBody ScheduleRequest req, Principal principal) {
        try {
            // Map DTO -> entity
            LichTrinh lichTrinh = new LichTrinh();

            // Map device
            if (req.getMaThietBi() == null) {
                return ResponseEntity.badRequest().body("maThietBi is required");
            }
            com.iot.management.model.entity.ThietBi device = new com.iot.management.model.entity.ThietBi();
            device.setMaThietBi(req.getMaThietBi());
            lichTrinh.setThietBi(device);

            lichTrinh.setTenLichTrinh(req.getTenLichTrinh());

            // Parse times: support HH:mm or full ISO datetime
            java.time.LocalTime start = null;
            java.time.LocalTime end = null;
            if (req.getThoiGianBatDau() != null) {
                start = parseToLocalTime(req.getThoiGianBatDau());
            }
            if (req.getThoiGianKetThuc() != null) {
                end = parseToLocalTime(req.getThoiGianKetThuc());
            }
            lichTrinh.setThoiGianBatDau(start != null ? start : java.time.LocalTime.of(0,0));
            lichTrinh.setThoiGianKetThuc(end != null ? end : java.time.LocalTime.of(23,59));

            // Commands: prefer explicit fields, otherwise use hanhDong for both start/end
            String cmdStart = req.getLenhKhiBatDau();
            String cmdEnd = req.getLenhKhiKetThuc();
            if ((cmdStart == null || cmdStart.isBlank()) && req.getHanhDong() != null) {
                cmdStart = req.getHanhDong();
            }
            if ((cmdEnd == null || cmdEnd.isBlank()) && req.getHanhDong() != null) {
                cmdEnd = req.getHanhDong();
            }
            // Ensure not null for DB
            lichTrinh.setLenhKhiBatDau(cmdStart != null ? cmdStart : "NO_OP");
            lichTrinh.setLenhKhiKetThuc(cmdEnd != null ? cmdEnd : "NO_OP");

            lichTrinh.setNgayTrongTuan(req.getNgayTrongTuan() != null ? req.getNgayTrongTuan() : "*");
            lichTrinh.setKichHoat(req.getKichHoat() != null ? req.getKichHoat() : true);

            LichTrinh savedSchedule = tuDongHoaService.saveSchedule(lichTrinh);
            return new ResponseEntity<>(savedSchedule, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Không thể lưu lịch trình: " + e.getMessage());
        }
    }

    private java.time.LocalTime parseToLocalTime(String s) {
        try {
            if (s.contains("T")) {
                return java.time.LocalDateTime.parse(s).toLocalTime();
            }
            return java.time.LocalTime.parse(s);
        } catch (Exception ex) {
            return null;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSchedule(@PathVariable Long id, Principal principal) {
        // Cần thêm logic kiểm tra quyền sở hữu
        tuDongHoaService.deleteSchedule(id);
        return ResponseEntity.ok("Xóa lịch trình thành công!");
    }

    // Lấy lịch trình theo thiết bị
    @GetMapping("/device/{deviceId}")
    public ResponseEntity<?> getSchedulesByDevice(@PathVariable Long deviceId, Principal principal) {
        // Cần thêm kiểm tra quyền nếu cần
        java.util.List<LichTrinh> schedules = tuDongHoaService.findSchedulesByDevice(deviceId);
        return ResponseEntity.ok(schedules);
    }

    // Lấy một lịch trình theo ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getScheduleById(@PathVariable Long id, Principal principal) {
        try {
            java.util.Optional<LichTrinh> opt = tuDongHoaService.findScheduleById(id);
            if (opt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(opt.get());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Không thể lấy thông tin lịch trình: " + e.getMessage());
        }
    }

    // Cập nhật một lịch trình (cập nhật một số trường được cung cấp)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateSchedule(@PathVariable Long id, @RequestBody ScheduleRequest req, Principal principal) {
        try {
            java.util.Optional<LichTrinh> opt = tuDongHoaService.findScheduleById(id);
            if (opt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            LichTrinh existing = opt.get();

            if (req.getTenLichTrinh() != null) existing.setTenLichTrinh(req.getTenLichTrinh());
            if (req.getMaThietBi() != null) {
                com.iot.management.model.entity.ThietBi device = new com.iot.management.model.entity.ThietBi();
                device.setMaThietBi(req.getMaThietBi());
                existing.setThietBi(device);
            }
            if (req.getThoiGianBatDau() != null) {
                java.time.LocalTime t = parseToLocalTime(req.getThoiGianBatDau());
                if (t != null) existing.setThoiGianBatDau(t);
            }
            if (req.getThoiGianKetThuc() != null) {
                java.time.LocalTime t = parseToLocalTime(req.getThoiGianKetThuc());
                if (t != null) existing.setThoiGianKetThuc(t);
            }
            if (req.getLenhKhiBatDau() != null) existing.setLenhKhiBatDau(req.getLenhKhiBatDau());
            if (req.getLenhKhiKetThuc() != null) existing.setLenhKhiKetThuc(req.getLenhKhiKetThuc());
            if (req.getHanhDong() != null) {
                // update both commands if provided as hanhDong
                existing.setLenhKhiBatDau(req.getHanhDong());
                existing.setLenhKhiKetThuc(req.getHanhDong());
            }
            if (req.getNgayTrongTuan() != null) existing.setNgayTrongTuan(req.getNgayTrongTuan());
            if (req.getKichHoat() != null) existing.setKichHoat(req.getKichHoat());

            LichTrinh saved = tuDongHoaService.saveSchedule(existing);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Không thể cập nhật lịch trình: " + e.getMessage());
        }
    }
}