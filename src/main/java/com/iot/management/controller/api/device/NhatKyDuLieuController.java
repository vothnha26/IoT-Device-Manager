package com.iot.management.controller.api.device;

import com.iot.management.model.entity.NhatKyDuLieu;
import com.iot.management.model.entity.ThietBi;
import com.iot.management.model.repository.ThietBiRepository;
import com.iot.management.service.NhatKyDuLieuService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/data-logs")
public class NhatKyDuLieuController {

    private final NhatKyDuLieuService nhatKyDuLieuService;
    private final ThietBiRepository thietBiRepository;

    public NhatKyDuLieuController(NhatKyDuLieuService nhatKyDuLieuService, ThietBiRepository thietBiRepository) {
        this.nhatKyDuLieuService = nhatKyDuLieuService;
        this.thietBiRepository = thietBiRepository;
    }

    // Endpoint cho thiết bị gửi dữ liệu lên
    @PostMapping("/{deviceToken}")
    public ResponseEntity<?> saveDataLog(@PathVariable String deviceToken, @RequestBody NhatKyDuLieu dataLog) {
        try {
            nhatKyDuLieuService.saveDataLog(deviceToken, dataLog);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Endpoint cho người dùng tạo nhật ký dữ liệu mới
    @PostMapping
    public ResponseEntity<?> createDataLog(@RequestBody DataLogRequest request) {
        try {
            ThietBi device = null;

            // 1) If request contains maThietBi or deviceId -> load by id
            Long idToUse = request.getMaThietBi() != null ? request.getMaThietBi() : request.getDeviceId();
            if (idToUse != null) {
                device = thietBiRepository.findById(idToUse)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy thiết bị với id: " + idToUse));
            }

            // 2) If still null and request contains deviceToken -> load by token
            if (device == null && request.getDeviceToken() != null && !request.getDeviceToken().isBlank()) {
                device = thietBiRepository.findByTokenThietBi(request.getDeviceToken())
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy thiết bị với token: " + request.getDeviceToken()));
            }

            if (device == null) {
                return ResponseEntity.badRequest().body("Thiếu thông tin thiết bị. Vui lòng cung cấp 'maThietBi' (hoặc 'deviceId') hoặc 'deviceToken' trong request body.");
            }

            // Map request -> entity
            NhatKyDuLieu dataLog = new NhatKyDuLieu();
            dataLog.setThietBi(device);
            if (request.getThoiGian() != null) {
                dataLog.setThoiGian(java.time.LocalDateTime.parse(request.getThoiGian()));
            }
            // tenTruong is NOT NULL in DB -> provide a sensible default if missing
            if (request.getTenTruong() != null && !request.getTenTruong().isBlank()) {
                dataLog.setTenTruong(request.getTenTruong());
            } else {
                dataLog.setTenTruong("unknown_field");
            }
            if (request.getGiaTri() != null) dataLog.setGiaTriSo(request.getGiaTri());
            // We don't set kieuGiaTri/giaTriChuoi/giaTriLogic here — service can infer if needed

            nhatKyDuLieuService.saveDataLog(device.getTokenThietBi(), dataLog);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Endpoint cho người dùng xem lịch sử
    @GetMapping("/history/{deviceId}")
    public ResponseEntity<List<NhatKyDuLieu>> getDeviceHistory(
            @PathVariable Long deviceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        // Cần thêm logic kiểm tra quyền sở hữu thiết bị
        List<NhatKyDuLieu> history = nhatKyDuLieuService.getHistory(deviceId, startTime, endTime);
        return ResponseEntity.ok(history);
    }
    
    @GetMapping("/device/{deviceId}")
    public ResponseEntity<List<NhatKyDuLieu>> getLatestDeviceData(@PathVariable Long deviceId) {
        // Lấy dữ liệu mới nhất của thiết bị
        List<NhatKyDuLieu> latestData = nhatKyDuLieuService.getLatestData(deviceId);
        return ResponseEntity.ok(latestData);
    }
}