package com.iot.management.controller.api.device;

import com.iot.management.model.entity.NhatKyDuLieu;
import com.iot.management.model.entity.ThietBi;
import com.iot.management.model.entity.KhuVuc;
import com.iot.management.model.repository.ThietBiRepository;
import com.iot.management.model.repository.KhuVucRepository;
import com.iot.management.service.NhatKyDuLieuService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    private final KhuVucRepository khuVucRepository;

    public NhatKyDuLieuController(NhatKyDuLieuService nhatKyDuLieuService, 
                                  ThietBiRepository thietBiRepository,
                                  KhuVucRepository khuVucRepository) {
        this.nhatKyDuLieuService = nhatKyDuLieuService;
        this.thietBiRepository = thietBiRepository;
        this.khuVucRepository = khuVucRepository;
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
        // Mặc định: từ 00:00:00 hôm nay đến hiện tại
        LocalDateTime start = startTime != null ? parseToLocalDateTime(startTime) : LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
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
        // Mặc định: từ 00:00:00 hôm nay đến hiện tại
        LocalDateTime start = startTime != null ? parseToLocalDateTime(startTime) : LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
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

    // API xuất báo cáo Excel theo khu vực và khoảng thời gian
    @GetMapping("/export-report")
    public ResponseEntity<byte[]> exportReport(
            @RequestParam Long khuVucId,
            @RequestParam String startTime,
            @RequestParam String endTime
    ) {
        try {
            LocalDateTime start = parseToLocalDateTime(startTime);
            LocalDateTime end = parseToLocalDateTime(endTime);
            
            // Lấy thông tin khu vực
            KhuVuc khuVuc = khuVucRepository.findById(khuVucId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy khu vực"));
            
            // Lấy danh sách thiết bị trong khu vực
            List<ThietBi> devices = thietBiRepository.findByKhuVuc_MaKhuVuc(khuVucId);
            
            // Tạo workbook Excel
            Workbook workbook = new XSSFWorkbook();
            
            // Sheet 1: Tổng quan
            Sheet summarySheet = workbook.createSheet("Tổng quan");
            createSummarySheet(summarySheet, khuVuc, devices, start, end);
            
            // Sheet 2: Dữ liệu cảm biến
            Sheet sensorSheet = workbook.createSheet("Dữ liệu cảm biến");
            createSensorDataSheet(sensorSheet, devices, start, end);
            
            // Sheet 3: Thời gian hoạt động thiết bị
            Sheet deviceTimeSheet = workbook.createSheet("Thời gian hoạt động");
            createDeviceOperatingTimeSheet(deviceTimeSheet, devices, start, end);
            
            // Xuất file
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();
            
            String fileName = String.format("BaoCao_%s_%s.xlsx", 
                    khuVuc.getTenKhuVuc().replaceAll("[^a-zA-Z0-9]", "_"),
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")));
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", fileName);
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(outputStream.toByteArray());
                    
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // API xuất dữ liệu thô (raw data) từ nhật ký dữ liệu
    @GetMapping("/export-raw-data")
    public ResponseEntity<byte[]> exportRawData(
            @RequestParam Long khuVucId,
            @RequestParam String startTime,
            @RequestParam String endTime
    ) {
        try {
            LocalDateTime start = parseToLocalDateTime(startTime);
            LocalDateTime end = parseToLocalDateTime(endTime);
            
            // Lấy thông tin khu vực
            KhuVuc khuVuc = khuVucRepository.findById(khuVucId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy khu vực"));
            
            // Lấy danh sách thiết bị trong khu vực
            List<ThietBi> devices = thietBiRepository.findByKhuVuc_MaKhuVuc(khuVucId);
            
            // Tạo workbook Excel
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Dữ liệu thô");
            
            // Style cho header
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            
            int rowNum = 0;
            
            // Header row
            Row headerRow = sheet.createRow(rowNum++);
            String[] columnHeaders = {"Thời gian", "Thiết bị", "Loại thiết bị", "Trường dữ liệu", "Giá trị số", "Giá trị chuỗi", "Giá trị logic", "Kiểu dữ liệu"};
            for (int i = 0; i < columnHeaders.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columnHeaders[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Date format style
            CellStyle dateStyle = workbook.createCellStyle();
            CreationHelper createHelper = workbook.getCreationHelper();
            dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/mm/yyyy hh:mm:ss"));
            
            // Lấy tất cả dữ liệu từ các thiết bị
            for (ThietBi device : devices) {
                List<NhatKyDuLieu> logs = nhatKyDuLieuService.getHistory(device.getMaThietBi(), start, end);
                
                for (NhatKyDuLieu log : logs) {
                    Row row = sheet.createRow(rowNum++);
                    
                    // Thời gian
                    Cell timeCell = row.createCell(0);
                    timeCell.setCellValue(log.getThoiGian().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
                    
                    // Thiết bị
                    row.createCell(1).setCellValue(device.getTenThietBi());
                    
                    // Loại thiết bị
                    row.createCell(2).setCellValue(device.getLoaiThietBi() != null ? 
                            device.getLoaiThietBi().getTenLoai() : "");
                    
                    // Trường dữ liệu
                    row.createCell(3).setCellValue(log.getTenTruong() != null ? log.getTenTruong() : "");
                    
                    // Giá trị số
                    if (log.getGiaTriSo() != null) {
                        row.createCell(4).setCellValue(log.getGiaTriSo().doubleValue());
                    } else {
                        row.createCell(4).setCellValue("");
                    }
                    
                    // Giá trị chuỗi
                    row.createCell(5).setCellValue(log.getGiaTriChuoi() != null ? log.getGiaTriChuoi() : "");
                    
                    // Giá trị logic
                    if (log.getGiaTriLogic() != null) {
                        row.createCell(6).setCellValue(log.getGiaTriLogic() ? "TRUE" : "FALSE");
                    } else {
                        row.createCell(6).setCellValue("");
                    }
                    
                    // Kiểu dữ liệu
                    String dataType = "";
                    Byte kieuGiaTri = log.getKieuGiaTri();
                    if (kieuGiaTri != null) {
                        switch (kieuGiaTri) {
                            case 0: dataType = "Số"; break;
                            case 1: dataType = "Chuỗi"; break;
                            case 2: dataType = "Logic"; break;
                            default: dataType = "Không xác định";
                        }
                    }
                    row.createCell(7).setCellValue(dataType);
                }
            }
            
            // Auto-size columns
            for (int i = 0; i < columnHeaders.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // Xuất file
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();
            
            String fileName = String.format("DuLieuTho_%s_%s.xlsx", 
                    khuVuc.getTenKhuVuc().replaceAll("[^a-zA-Z0-9]", "_"),
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")));
            
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            responseHeaders.setContentDispositionFormData("attachment", fileName);
            
            return ResponseEntity.ok()
                    .headers(responseHeaders)
                    .body(outputStream.toByteArray());
                    
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    private void createSummarySheet(Sheet sheet, KhuVuc khuVuc, List<ThietBi> devices, 
                                     LocalDateTime start, LocalDateTime end) {
        // Header
        CellStyle headerStyle = sheet.getWorkbook().createCellStyle();
        Font headerFont = sheet.getWorkbook().createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 14);
        headerStyle.setFont(headerFont);
        
        int rowNum = 0;
        
        // Tiêu đề báo cáo
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("BÁO CÁO THỐNG KÊ KHU VỰC");
        titleCell.setCellStyle(headerStyle);
        
        rowNum++; // Dòng trống
        
        // Thông tin khu vực
        createInfoRow(sheet, rowNum++, "Khu vực:", khuVuc.getTenKhuVuc());
        createInfoRow(sheet, rowNum++, "Mô tả:", khuVuc.getMoTa() != null ? khuVuc.getMoTa() : "");
        createInfoRow(sheet, rowNum++, "Từ ngày:", start.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        createInfoRow(sheet, rowNum++, "Đến ngày:", end.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        createInfoRow(sheet, rowNum++, "Số lượng thiết bị:", String.valueOf(devices.size()));
        
        rowNum++; // Dòng trống
        
        // Danh sách thiết bị
        Row deviceHeaderRow = sheet.createRow(rowNum++);
        deviceHeaderRow.createCell(0).setCellValue("STT");
        deviceHeaderRow.createCell(1).setCellValue("Tên thiết bị");
        deviceHeaderRow.createCell(2).setCellValue("Loại thiết bị");
        deviceHeaderRow.createCell(3).setCellValue("Trạng thái");
        
        for (int i = 0; i < devices.size(); i++) {
            ThietBi device = devices.get(i);
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(i + 1);
            row.createCell(1).setCellValue(device.getTenThietBi());
            row.createCell(2).setCellValue(device.getLoaiThietBi() != null ? 
                    device.getLoaiThietBi().getTenLoai() : "");
            row.createCell(3).setCellValue(device.getTrangThai() != null ? 
                    device.getTrangThai().toString() : "");
        }
        
        // Auto-size columns
        for (int i = 0; i < 4; i++) {
            sheet.autoSizeColumn(i);
        }
    }
    
    private void createSensorDataSheet(Sheet sheet, List<ThietBi> devices, 
                                        LocalDateTime start, LocalDateTime end) {
        // Lọc thiết bị sensor
        List<ThietBi> sensors = devices.stream()
                .filter(d -> d.getLoaiThietBi() != null && 
                        d.getLoaiThietBi().getNhomThietBi() != null &&
                        d.getLoaiThietBi().getNhomThietBi().name().equals("SENSOR"))
                .toList();
        
        int rowNum = 0;
        
        // Header
        Row headerRow = sheet.createRow(rowNum++);
        headerRow.createCell(0).setCellValue("Thời gian");
        headerRow.createCell(1).setCellValue("Thiết bị");
        headerRow.createCell(2).setCellValue("Trường dữ liệu");
        headerRow.createCell(3).setCellValue("Giá trị");
        
        // Lấy dữ liệu từng sensor
        for (ThietBi sensor : sensors) {
            List<NhatKyDuLieu> logs = nhatKyDuLieuService.getHistory(sensor.getMaThietBi(), start, end);
            
            for (NhatKyDuLieu log : logs) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(log.getThoiGian().format(
                        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
                row.createCell(1).setCellValue(sensor.getTenThietBi());
                row.createCell(2).setCellValue(log.getTenTruong() != null ? log.getTenTruong() : "");
                
                // Giá trị
                String value = "";
                if (log.getGiaTriSo() != null) {
                    value = String.format("%.2f", log.getGiaTriSo().doubleValue());
                } else if (log.getGiaTriChuoi() != null) {
                    value = log.getGiaTriChuoi();
                } else if (log.getGiaTriLogic() != null) {
                    value = log.getGiaTriLogic() ? "ON" : "OFF";
                }
                row.createCell(3).setCellValue(value);
            }
        }
        
        // Auto-size columns
        for (int i = 0; i < 4; i++) {
            sheet.autoSizeColumn(i);
        }
    }
    
    private void createDeviceOperatingTimeSheet(Sheet sheet, List<ThietBi> devices, 
                                                 LocalDateTime start, LocalDateTime end) {
        // Lọc thiết bị controller (switch/relay)
        List<ThietBi> controllers = devices.stream()
                .filter(d -> d.getLoaiThietBi() != null && 
                        d.getLoaiThietBi().getNhomThietBi() != null &&
                        d.getLoaiThietBi().getNhomThietBi().name().equals("CONTROLLER"))
                .toList();
        
        int rowNum = 0;
        
        // Header
        Row headerRow = sheet.createRow(rowNum++);
        headerRow.createCell(0).setCellValue("Thiết bị");
        headerRow.createCell(1).setCellValue("Tổng thời gian bật (giờ)");
        headerRow.createCell(2).setCellValue("Tổng thời gian bật (phút)");
        headerRow.createCell(3).setCellValue("Tỷ lệ hoạt động (%)");
        
        long totalRangeMs = java.sql.Timestamp.valueOf(end).getTime() - 
                           java.sql.Timestamp.valueOf(start).getTime();
        
        // Tính toán cho từng thiết bị
        for (ThietBi device : controllers) {
            List<NhatKyDuLieu> logs = nhatKyDuLieuService.getHistory(device.getMaThietBi(), start, end);
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
            long totalOnMinutes = totalOnMs / 60000;
            double percentage = totalRangeMs > 0 ? (totalOnMs * 100.0 / totalRangeMs) : 0;
            
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(device.getTenThietBi());
            row.createCell(1).setCellValue(String.format("%.2f", totalOnHours));
            row.createCell(2).setCellValue(totalOnMinutes);
            row.createCell(3).setCellValue(String.format("%.2f%%", percentage));
        }
        
        // Auto-size columns
        for (int i = 0; i < 4; i++) {
            sheet.autoSizeColumn(i);
        }
    }
    
    private void createInfoRow(Sheet sheet, int rowNum, String label, String value) {
        Row row = sheet.createRow(rowNum);
        Cell labelCell = row.createCell(0);
        labelCell.setCellValue(label);
        
        CellStyle boldStyle = sheet.getWorkbook().createCellStyle();
        Font boldFont = sheet.getWorkbook().createFont();
        boldFont.setBold(true);
        boldStyle.setFont(boldFont);
        labelCell.setCellStyle(boldStyle);
        
        row.createCell(1).setCellValue(value);
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