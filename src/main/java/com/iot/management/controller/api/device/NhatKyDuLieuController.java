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
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Arrays;
import java.security.Principal;

@RestController
@RequestMapping("/api/data-logs")
public class NhatKyDuLieuController {

    private final NhatKyDuLieuService nhatKyDuLieuService;
    private final ThietBiRepository thietBiRepository;

    public NhatKyDuLieuController(NhatKyDuLieuService nhatKyDuLieuService, ThietBiRepository thietBiRepository) {
        this.nhatKyDuLieuService = nhatKyDuLieuService;
        this.thietBiRepository = thietBiRepository;
    }

    // Endpoint cho thiết bị gửi dữ liệu lên (hỗ trợ cả deviceId và deviceToken)
    @PostMapping("/{deviceIdentifier}")
    public ResponseEntity<?> saveDataLog(@PathVariable String deviceIdentifier,
                                         @RequestBody NhatKyDuLieu dataLog,
                                         Principal principal) {
        try {
            // Thử parse deviceIdentifier thành Long (deviceId)
            try {
                Long deviceId = Long.parseLong(deviceIdentifier);
                ThietBi device = thietBiRepository.findById(deviceId)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy thiết bị với id: " + deviceId));
                
                dataLog.setThietBi(device);
                if (dataLog.getThoiGian() == null) {
                    dataLog.setThoiGian(LocalDateTime.now());
                }
                nhatKyDuLieuService.saveDataLog(device.getTokenThietBi(), dataLog);
                return ResponseEntity.ok().build();
            } catch (NumberFormatException e) {
                // Nếu không phải số, coi như deviceToken
                nhatKyDuLieuService.saveDataLog(deviceIdentifier, dataLog);
                return ResponseEntity.ok().build();
            }
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
    
    // Endpoint lấy dữ liệu cảm biến theo khoảng thời gian
    @GetMapping("/device/{deviceId}/range")
    public ResponseEntity<List<NhatKyDuLieu>> getDeviceDataByTimeRange(
            @PathVariable Long deviceId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(required = false, defaultValue = "100") int limit) {
        
        if (startTime == null) {
            startTime = LocalDateTime.now().minusDays(7); // Mặc định 7 ngày trước
        }
        if (endTime == null) {
            endTime = LocalDateTime.now();
        }
        
        List<NhatKyDuLieu> data = nhatKyDuLieuService.getHistory(deviceId, startTime, endTime);
        
        // Giới hạn số lượng kết quả
        if (data.size() > limit) {
            data = data.subList(0, limit);
        }
        
        return ResponseEntity.ok(data);
    }

    // API timeseries: gộp dữ liệu theo bucket phút (mặc định 2 phút) cho một hoặc nhiều trường
    // Trả về cấu trúc phù hợp biểu đồ: { bucketMinutes, startTime, endTime, series: [ { field, data: [[timestamp,value], ...] } ] }
    @GetMapping("/device/{deviceId}/timeseries")
    public ResponseEntity<?> getDeviceTimeSeries(
            @PathVariable Long deviceId,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false, defaultValue = "2") int bucketMinutes,
            @RequestParam(required = false, defaultValue = "avg") String agg,
            @RequestParam(required = false) String fields
    ) {
        if (bucketMinutes < 1) bucketMinutes = 2;
        LocalDateTime start = startTime != null ? parseToLocalDateTime(startTime) : LocalDateTime.now().minusHours(2);
        LocalDateTime end = endTime != null ? parseToLocalDateTime(endTime) : LocalDateTime.now();
        if (end.isBefore(start)) {
            return ResponseEntity.badRequest().body("endTime must be after startTime");
        }

        // Danh sách trường cần lấy. Mặc định hai trường phổ biến
        List<String> fieldList = new ArrayList<>();
        if (fields != null && !fields.isBlank()) {
            Arrays.stream(fields.split(",")).map(String::trim).filter(s -> !s.isEmpty()).forEach(fieldList::add);
        }
        if (fieldList.isEmpty()) {
            fieldList = List.of("nhiet_do", "do_am");
        }

        long bucketMs = bucketMinutes * 60L * 1000L;
    long startMs = java.sql.Timestamp.valueOf(start).getTime();
    long endMs = java.sql.Timestamp.valueOf(end).getTime();
        int bucketCount = (int) Math.max(1, Math.ceil((endMs - startMs) / (double) bucketMs));

        // Chuẩn bị mốc thời gian cho tất cả bucket
        List<Long> timestamps = new ArrayList<>(bucketCount);
        for (int i = 0; i < bucketCount; i++) {
            timestamps.add(startMs + i * bucketMs);
        }

        List<Map<String, Object>> series = new ArrayList<>();

        for (String rawField : fieldList) {
            String field = rawField.toLowerCase();
            // Aggregator: sum và count theo bucket
            double[] sum = new double[bucketCount];
            int[] count = new int[bucketCount];

            // Lấy dữ liệu theo trường, sắp xếp tăng dần theo thời gian
        List<NhatKyDuLieu> rows = nhatKyDuLieuService
            .getHistory(deviceId, start, end); // lấy tất cả rồi lọc theo field để giữ tương thích

            for (NhatKyDuLieu log : rows) {
                if (log.getTenTruong() == null) continue;
                if (!log.getTenTruong().equalsIgnoreCase(field)) continue;
                // Chỉ lấy số
                if (log.getGiaTriSo() == null) continue;
                long t = java.sql.Timestamp.valueOf(log.getThoiGian()).getTime();
                int idx = (int) ((t - startMs) / bucketMs);
                if (idx < 0 || idx >= bucketCount) continue;
                sum[idx] += log.getGiaTriSo().doubleValue();
                count[idx] += 1;
            }

            // Tính giá trị theo phép gộp
            List<List<Object>> data = new ArrayList<>(bucketCount);
            for (int i = 0; i < bucketCount; i++) {
                Double val = null;
                if (count[i] > 0) {
                    switch (agg.toLowerCase()) {
                        case "sum":
                            val = sum[i];
                            break;
                        case "avg":
                        default:
                            val = sum[i] / count[i];
                    }
                }
                // Dùng Arrays.asList để cho phép giá trị null (List.of không cho phép null)
                data.add(Arrays.asList(timestamps.get(i), val));
            }

            Map<String, Object> s = new HashMap<>();
            s.put("field", field);
            s.put("data", data);
            series.add(s);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("bucketMinutes", bucketMinutes);
        response.put("startTime", start.toString());
        response.put("endTime", end.toString());
        response.put("series", series);
        return ResponseEntity.ok(response);
    }

    // API tính tổng thời gian hoạt động của thiết bị (dành cho công tắc/relay)
    // Trả về: { deviceId, deviceName, totalOnHours, totalOnMinutes, startTime, endTime }
    @GetMapping("/device/{deviceId}/operating-time")
    public ResponseEntity<?> getDeviceOperatingTime(
            @PathVariable Long deviceId,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime
    ) {
        LocalDateTime start = startTime != null ? parseToLocalDateTime(startTime) : LocalDateTime.now().minusHours(2);
        LocalDateTime end = endTime != null ? parseToLocalDateTime(endTime) : LocalDateTime.now();
        
        if (end.isBefore(start)) {
            return ResponseEntity.badRequest().body("endTime must be after startTime");
        }

        // Lấy thông tin thiết bị
        ThietBi device = thietBiRepository.findById(deviceId).orElse(null);
        if (device == null) {
            return ResponseEntity.notFound().build();
        }

        // Lấy logs trạng thái (boolean) trong khoảng thời gian
        List<NhatKyDuLieu> logs = nhatKyDuLieuService.getHistory(deviceId, start, end);
        
        // Lọc chỉ lấy logs có kiểu boolean (kieuGiaTri = 2)
        logs = logs.stream()
                .filter(log -> {
                    Byte kieuGiaTri = log.getKieuGiaTri();
                    return kieuGiaTri != null && kieuGiaTri.intValue() == 2;
                })
                .sorted((a, b) -> a.getThoiGian().compareTo(b.getThoiGian()))
                .toList();

        // Tính tổng thời gian ON
        long totalOnMs = 0;
        boolean currentState = false; // Giả sử thiết bị OFF ở đầu khoảng thời gian
        LocalDateTime lastTimestamp = start;
        
        long rangeStartMs = java.sql.Timestamp.valueOf(start).getTime();
        long rangeEndMs = java.sql.Timestamp.valueOf(end).getTime();

        for (NhatKyDuLieu log : logs) {
            long logTime = java.sql.Timestamp.valueOf(log.getThoiGian()).getTime();
            
            if (logTime < rangeStartMs) continue;
            
            // Tích lũy thời gian ON từ lastTimestamp đến logTime
            if (currentState) {
                totalOnMs += (logTime - java.sql.Timestamp.valueOf(lastTimestamp).getTime());
            }
            
            // Cập nhật trạng thái
            currentState = log.getGiaTriLogic() != null && log.getGiaTriLogic();
            lastTimestamp = log.getThoiGian();
        }
        
        // Tính phần còn lại đến endTime nếu thiết bị đang ON
        if (currentState && lastTimestamp.isBefore(end)) {
            totalOnMs += (rangeEndMs - java.sql.Timestamp.valueOf(lastTimestamp).getTime());
        }

        // Chuyển đổi sang giờ và phút
        double totalOnHours = totalOnMs / 3600000.0;
        long totalOnMinutes = totalOnMs / 60000;

        Map<String, Object> response = new HashMap<>();
        response.put("deviceId", deviceId);
        response.put("deviceName", device.getTenThietBi());
        response.put("totalOnHours", Math.round(totalOnHours * 100.0) / 100.0); // 2 chữ số thập phân
        response.put("totalOnMinutes", totalOnMinutes);
        response.put("totalOnMs", totalOnMs);
        response.put("startTime", start.toString());
        response.put("endTime", end.toString());
        
        return ResponseEntity.ok(response);
    }

    // API tính tổng thời gian hoạt động cho nhiều thiết bị (dùng cho biểu đồ tròn)
    // Trả về: [ { deviceId, deviceName, totalOnHours }, ... ]
    @GetMapping("/devices/operating-time")
    public ResponseEntity<?> getMultipleDevicesOperatingTime(
            @RequestParam String deviceIds, // Chuỗi id cách nhau bởi dấu phẩy, vd: "2,3"
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime
    ) {
        LocalDateTime start = startTime != null ? parseToLocalDateTime(startTime) : LocalDateTime.now().minusHours(2);
        LocalDateTime end = endTime != null ? parseToLocalDateTime(endTime) : LocalDateTime.now();
        
        if (end.isBefore(start)) {
            return ResponseEntity.badRequest().body("endTime must be after startTime");
        }

        // Parse danh sách device IDs
        List<Long> ids = new ArrayList<>();
        try {
            for (String id : deviceIds.split(",")) {
                ids.add(Long.parseLong(id.trim()));
            }
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Invalid deviceIds format");
        }

        List<Map<String, Object>> results = new ArrayList<>();
        
        for (Long deviceId : ids) {
            ThietBi device = thietBiRepository.findById(deviceId).orElse(null);
            if (device == null) continue;

            // Lấy logs trạng thái
            List<NhatKyDuLieu> logs = nhatKyDuLieuService.getHistory(deviceId, start, end);
            logs = logs.stream()
                    .filter(log -> {
                        Byte kieuGiaTri = log.getKieuGiaTri();
                        return kieuGiaTri != null && kieuGiaTri.intValue() == 2;
                    })
                    .sorted((a, b) -> a.getThoiGian().compareTo(b.getThoiGian()))
                    .toList();

            // Tính tổng thời gian ON
            long totalOnMs = 0;
            boolean currentState = false;
            LocalDateTime lastTimestamp = start;
            
            long rangeStartMs = java.sql.Timestamp.valueOf(start).getTime();
            long rangeEndMs = java.sql.Timestamp.valueOf(end).getTime();

            for (NhatKyDuLieu log : logs) {
                long logTime = java.sql.Timestamp.valueOf(log.getThoiGian()).getTime();
                if (logTime < rangeStartMs) continue;
                
                if (currentState) {
                    totalOnMs += (logTime - java.sql.Timestamp.valueOf(lastTimestamp).getTime());
                }
                
                currentState = log.getGiaTriLogic() != null && log.getGiaTriLogic();
                lastTimestamp = log.getThoiGian();
            }
            
            if (currentState && lastTimestamp.isBefore(end)) {
                totalOnMs += (rangeEndMs - java.sql.Timestamp.valueOf(lastTimestamp).getTime());
            }

            double totalOnHours = totalOnMs / 3600000.0;

            Map<String, Object> result = new HashMap<>();
            result.put("deviceId", deviceId);
            result.put("deviceName", device.getTenThietBi());
            result.put("totalOnHours", Math.round(totalOnHours * 100.0) / 100.0);
            results.add(result);
        }

        return ResponseEntity.ok(results);
    }

    // Hỗ trợ parse ISO8601 với hoặc không có 'Z' (UTC) hoặc offset, fallback LocalDateTime
    private LocalDateTime parseToLocalDateTime(String s) {
        try {
            // Try instant with offset or Z
            java.time.Instant inst = java.time.Instant.parse(s);
            return java.time.LocalDateTime.ofInstant(inst, java.time.ZoneId.systemDefault());
        } catch (Exception ignored) {}
        try {
            // Try OffsetDateTime
            return java.time.OffsetDateTime.parse(s).toLocalDateTime();
        } catch (Exception ignored) {}
        try {
            // Try LocalDateTime
            return java.time.LocalDateTime.parse(s);
        } catch (Exception ex) {
            // Fallback: now
            return LocalDateTime.now();
        }
    }
}