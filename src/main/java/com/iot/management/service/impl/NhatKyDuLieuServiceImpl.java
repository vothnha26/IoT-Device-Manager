package com.iot.management.service.impl;

import com.iot.management.model.entity.NhatKyDuLieu;
import com.iot.management.model.entity.ThietBi;
import com.iot.management.model.repository.NhatKyDuLieuRepository;
import com.iot.management.model.repository.ThietBiRepository;
import com.iot.management.service.NhatKyDuLieuService;
import com.iot.management.service.TuDongHoaService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NhatKyDuLieuServiceImpl implements NhatKyDuLieuService {

    private final NhatKyDuLieuRepository nhatKyDuLieuRepository;
    private final ThietBiRepository thietBiRepository;
    private final TuDongHoaService tuDongHoaService;
    private final SimpMessagingTemplate messagingTemplate;
    // Giới hạn tần suất broadcast (ms), mặc định 12000ms ~ 12 giây
    @Value("${app.sensor.broadcast.min-interval-ms:12000}")
    private long minBroadcastIntervalMs;
    private final java.util.concurrent.ConcurrentHashMap<String, Long> lastBroadcastTime = new java.util.concurrent.ConcurrentHashMap<>();

    public NhatKyDuLieuServiceImpl(NhatKyDuLieuRepository nhatKyDuLieuRepository, 
                                   ThietBiRepository thietBiRepository, 
                                   TuDongHoaService tuDongHoaService,
                                   SimpMessagingTemplate messagingTemplate) {
        this.nhatKyDuLieuRepository = nhatKyDuLieuRepository;
        this.thietBiRepository = thietBiRepository;
        this.tuDongHoaService = tuDongHoaService;
    this.messagingTemplate = messagingTemplate;
    }

    @Override
    @Transactional
    public NhatKyDuLieu saveDataLog(String deviceToken, NhatKyDuLieu dataLog) {
        ThietBi thietBi = thietBiRepository.findByTokenThietBi(deviceToken)
                .orElseThrow(() -> new RuntimeException("Token thiết bị không hợp lệ: " + deviceToken));

        dataLog.setThietBi(thietBi);
        // Nếu đã có thoiGian từ request thì giữ nguyên, nếu chưa có thì set bây giờ
        if (dataLog.getThoiGian() == null) {
            dataLog.setThoiGian(LocalDateTime.now());
        }
        
    // Khi thiết bị gửi dữ liệu, coi như đang hoạt động
    thietBi.setTrangThai("hoat_dong");
        thietBi.setLanHoatDongCuoi(LocalDateTime.now());
        thietBiRepository.save(thietBi);

        NhatKyDuLieu savedLog = nhatKyDuLieuRepository.save(dataLog);

        // Sau khi lưu thành công, gọi bộ xử lý luật
        tuDongHoaService.processRules(savedLog);

        // Phát realtime qua WebSocket
        broadcastSensorData(savedLog);

        return savedLog;
    }

    @Override
    public List<NhatKyDuLieu> getHistory(Long deviceId, LocalDateTime startTime, LocalDateTime endTime) {
        // Ở đây không dùng phân trang, nhưng có thể thêm Pageable vào tham số
        return nhatKyDuLieuRepository.findByThietBi_MaThietBiAndThoiGianBetween(deviceId, startTime, endTime, null);
    }

    @Override
    public List<NhatKyDuLieu> getLatestData(Long deviceId) {
        return nhatKyDuLieuRepository.findTop10ByThietBi_MaThietBiOrderByThoiGianDesc(deviceId);
    }
    
    @Override
    @Transactional
    public NhatKyDuLieu saveManualControlLog(Long deviceId, String fieldName, Boolean value) {
        ThietBi thietBi = thietBiRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thiết bị với ID: " + deviceId));
        
        NhatKyDuLieu dataLog = new NhatKyDuLieu();
        dataLog.setThietBi(thietBi);
        dataLog.setThoiGian(LocalDateTime.now());
        dataLog.setTenTruong(fieldName);
        dataLog.setKieuGiaTri((byte) 2); // 2 = Boolean/Logic
        dataLog.setGiaTriLogic(value);
        
        NhatKyDuLieu savedLog = nhatKyDuLieuRepository.save(dataLog);
        
        // Sau khi lưu thành công, gọi bộ xử lý luật (nếu có)
        try {
            tuDongHoaService.processRules(savedLog);
        } catch (Exception e) {
            // Log error nhưng không làm fail transaction chính
            System.err.println("⚠️ Error processing rules after manual control: " + e.getMessage());
        }
        
        // Phát realtime qua WebSocket
        broadcastSensorData(savedLog);

        return savedLog;
    }

    /**
     * Broadcast dữ liệu cảm biến qua WebSocket STOMP
     */
    private void broadcastSensorData(NhatKyDuLieu dataLog) {
        try {
            Long deviceId = dataLog.getThietBi().getMaThietBi();
            String field = dataLog.getTenTruong() != null ? dataLog.getTenTruong() : "unknown";
            String key = deviceId + ":" + field;
            long now = System.currentTimeMillis();
            Long last = lastBroadcastTime.get(key);
            if (last != null && (now - last) < minBroadcastIntervalMs) {
                // Bỏ qua broadcast nếu chưa đủ khoảng thời gian tối thiểu
                return;
            }
            lastBroadcastTime.put(key, now);

            Map<String, Object> payload = new HashMap<>();
            payload.put("deviceId", deviceId);
            payload.put("fieldName", field);
            payload.put("timestamp", dataLog.getThoiGian().toString());

            // Lấy giá trị dựa trên kiểu dữ liệu
            switch (dataLog.getKieuGiaTri()) {
                case 0: // String
                    payload.put("value", dataLog.getGiaTriChuoi());
                    payload.put("type", "string");
                    break;
                case 1: // Number
                    payload.put("value", dataLog.getGiaTriSo());
                    payload.put("type", "number");
                    break;
                case 2: // Boolean
                    payload.put("value", dataLog.getGiaTriLogic());
                    payload.put("type", "boolean");
                    break;
                default:
                    payload.put("value", null);
                    payload.put("type", "unknown");
            }

            // Gửi tới topic cho device cụ thể
            messagingTemplate.convertAndSend("/topic/sensor/" + deviceId, payload);

            // Gửi tới topic chung cho tất cả sensor updates
            messagingTemplate.convertAndSend("/topic/sensor/all", payload);

        } catch (Exception e) {
            System.err.println("⚠️ Error broadcasting sensor data: " + e.getMessage());
        }
    }
}