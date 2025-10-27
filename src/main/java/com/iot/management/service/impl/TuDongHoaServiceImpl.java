package com.iot.management.service.impl;

import com.iot.management.model.entity.LuatNguong;
import com.iot.management.model.entity.NhatKyDuLieu;
import com.iot.management.model.entity.LichTrinh;
import com.iot.management.model.repository.LuatNguongRepository;
import com.iot.management.model.repository.LichTrinhRepository;
import com.iot.management.model.repository.NhatKyDuLieuRepository;
import com.iot.management.model.repository.ThongBaoRepository;
import com.iot.management.service.TuDongHoaService;
import com.iot.management.service.ThietBiService;
import com.iot.management.service.ThongBaoService;
import com.iot.management.rules.EasyRulesEngine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@Service
public class TuDongHoaServiceImpl implements TuDongHoaService {
    private static final Logger logger = LoggerFactory.getLogger(TuDongHoaServiceImpl.class);

    private final LuatNguongRepository luatNguongRepository;
    private final LichTrinhRepository lichTrinhRepository;
    private final ThietBiService thietBiService;
    private final NhatKyDuLieuRepository nhatKyDuLieuRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ThongBaoService thongBaoService;
    private final ThongBaoRepository thongBaoRepository;
    // Theo dõi thời điểm điều kiện của luật bắt đầu được thỏa mãn để hỗ trợ "thời gian duy trì điều kiện"
    private final java.util.concurrent.ConcurrentHashMap<Long, java.time.Instant> satisfiedSince = new java.util.concurrent.ConcurrentHashMap<>();
    // Theo dõi luật nào đã trigger để tránh ghi notification trùng lặp
    private final java.util.concurrent.ConcurrentHashMap<Long, java.time.Instant> lastTriggered = new java.util.concurrent.ConcurrentHashMap<>();

    public TuDongHoaServiceImpl(
            LuatNguongRepository luatNguongRepository,
            LichTrinhRepository lichTrinhRepository,
            ThietBiService thietBiService,
            NhatKyDuLieuRepository nhatKyDuLieuRepository,
            SimpMessagingTemplate messagingTemplate,
            ThongBaoService thongBaoService,
            ThongBaoRepository thongBaoRepository) {
        this.luatNguongRepository = luatNguongRepository;
        this.lichTrinhRepository = lichTrinhRepository;
        this.thietBiService = thietBiService;
        this.nhatKyDuLieuRepository = nhatKyDuLieuRepository;
        this.messagingTemplate = messagingTemplate;
        this.thongBaoService = thongBaoService;
        this.thongBaoRepository = thongBaoRepository;
    }

    @Override
    @Transactional
    public LuatNguong saveRule(LuatNguong luatNguong) {
        try {
            logger.info("Saving rule: maThietBi={}, bieuThuc={}, lenhHanhDong={}, kichHoat={}", 
                luatNguong.getThietBi() != null ? luatNguong.getThietBi().getMaThietBi() : "null",
                luatNguong.getBieuThucLogic(),
                luatNguong.getLenhHanhDong(),
                luatNguong.isKichHoat());
            return luatNguongRepository.save(luatNguong);
        } catch (Exception e) {
            logger.error("Lỗi khi lưu luật ngưỡng: {}", e.getMessage(), e);
            throw new RuntimeException("Không thể lưu luật ngưỡng: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public LichTrinh saveSchedule(LichTrinh lichTrinh) {
        try {
            return lichTrinhRepository.save(lichTrinh);
        } catch (Exception e) {
            logger.error("Lỗi khi lưu lịch trình: {}", e.getMessage());
            throw new RuntimeException("Không thể lưu lịch trình", e);
        }
    }

    @Override
    @Transactional
    public void deleteRule(Long ruleId) {
        try {
            luatNguongRepository.deleteById(ruleId);
        } catch (Exception e) {
            logger.error("Lỗi khi xóa luật ngưỡng ID {}: {}", ruleId, e.getMessage());
            throw new RuntimeException("Không thể xóa luật ngưỡng", e);
        }
    }

    @Override
    @Transactional
    public void deleteSchedule(Long scheduleId) {
        try {
            lichTrinhRepository.deleteById(scheduleId);
        } catch (Exception e) {
            logger.error("Lỗi khi xóa lịch trình ID {}: {}", scheduleId, e.getMessage());
            throw new RuntimeException("Không thể xóa lịch trình", e);
        }
    }

    @Override
    @Transactional
    public void processRules(NhatKyDuLieu dataLog) {
        try {
            // Lấy TẤT CẢ luật đang kích hoạt của MỌI thiết bị 
            // (vì có luật của thiết bị khác phụ thuộc vào dữ liệu thiết bị này)
            List<LuatNguong> rules = luatNguongRepository.findByKichHoatIsTrue();
            
            for (LuatNguong rule : rules) {
                // Chỉ xử lý luật dựa trên biểu thức
                if (rule.getBieuThucLogic() != null && !rule.getBieuThucLogic().isBlank()) {
                    processExpressionRule(rule, dataLog);
                }
            }
        } catch (Exception e) {
            logger.error("❌ Lỗi khi xử lý luật tự động: {}", e.getMessage());
            throw new RuntimeException("Không thể xử lý luật tự động", e);
        }
    }

    @Override
    public List<LichTrinh> getLichTrinhByThietBi(Long maThietBi) {
        try {
            return lichTrinhRepository.findByThietBi_MaThietBi(maThietBi);
        } catch (Exception e) {
            logger.error("Lỗi khi lấy danh sách lịch trình của thiết bị ID {}: {}", 
                maThietBi, e.getMessage());
            throw new RuntimeException("Không thể lấy danh sách lịch trình", e);
        }
    }

    @Override
    public boolean existsById(Long id) {
        try {
            return lichTrinhRepository.existsById(id);
        } catch (Exception e) {
            logger.error("Lỗi khi kiểm tra tồn tại lịch trình ID {}: {}", 
                id, e.getMessage());
            throw new RuntimeException("Không thể kiểm tra tồn tại lịch trình", e);
        }
    }

    @Override
    @Transactional
    public LichTrinh toggleSchedule(Long id, boolean kichHoat) {
        try {
            return lichTrinhRepository.findById(id)
                .map(lichTrinh -> {
                    lichTrinh.setKichHoat(kichHoat);
                    return lichTrinhRepository.save(lichTrinh);
                })
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch trình ID: " + id));
        } catch (Exception e) {
            logger.error("Lỗi khi thay đổi trạng thái kích hoạt lịch trình ID {}: {}", 
                id, e.getMessage());
            throw new RuntimeException("Không thể thay đổi trạng thái kích hoạt lịch trình", e);
        }
    }

    @Override
    public java.util.List<LichTrinh> findSchedulesByDevice(Long deviceId) {
        try {
            return lichTrinhRepository.findByThietBi_MaThietBi(deviceId);
        } catch (Exception e) {
            logger.error("Lỗi khi lấy lịch trình cho thiết bị {}: {}", deviceId, e.getMessage());
            throw new RuntimeException("Không thể lấy lịch trình", e);
        }
    }

    @Override
    public java.util.List<LuatNguong> findRulesByDevice(Long deviceId) {
        try {
            return luatNguongRepository.findByThietBi_MaThietBi(deviceId);
        } catch (Exception e) {
            logger.error("Lỗi khi lấy luật cho thiết bị {}: {}", deviceId, e.getMessage());
            throw new RuntimeException("Không thể lấy luật", e);
        }
    }

    @Override
    public java.util.Optional<LichTrinh> findScheduleById(Long scheduleId) {
        try {
            return lichTrinhRepository.findById(scheduleId);
        } catch (Exception e) {
            logger.error("Lỗi khi lấy lịch trình ID {}: {}", scheduleId, e.getMessage());
            throw new RuntimeException("Không thể lấy lịch trình", e);
        }
    }

    @Override
    public java.util.Optional<LuatNguong> findRuleById(Long ruleId) {
        try {
            return luatNguongRepository.findById(ruleId);
        } catch (Exception e) {
            logger.error("Lỗi khi lấy luật ID {}: {}", ruleId, e.getMessage());
            throw new RuntimeException("Không thể lấy luật", e);
        }
    }

    // processThresholdRule removed: hệ thống chuyển sang dùng biểu thức hoàn toàn

    private void processExpressionRule(LuatNguong rule, NhatKyDuLieu dataLog) {
        try {
            // Thu thập facts cho tất cả biến xuất hiện trong biểu thức
            java.util.Map<String, Object> facts = new java.util.HashMap<>();
            String expr = rule.getBieuThucLogic();
            java.util.Set<String> idents = extractIdentifiers(expr);
            Long sourceDeviceId = dataLog.getThietBi().getMaThietBi();
            
            for (String field : idents) {
                Long targetDeviceId = sourceDeviceId;
                String actualField = field;
                
                // Parse field name: nếu có dạng "thiet_bi_X.field_name"
                if (field.startsWith("thiet_bi_")) {
                    String[] parts = field.split("\\.", 2);
                    if (parts.length == 2) {
                        try {
                            targetDeviceId = Long.parseLong(parts[0].replace("thiet_bi_", ""));
                            actualField = parts[1];
                        } catch (NumberFormatException e) {
                            logger.warn("⚠️ Invalid device ID format: {}", field);
                        }
                    }
                }
                
                // Lấy giá trị mới nhất
                com.iot.management.model.entity.NhatKyDuLieu latest = nhatKyDuLieuRepository
                    .findTop1ByThietBi_MaThietBiAndTenTruongIgnoreCaseOrderByThoiGianDesc(targetDeviceId, actualField);
                    
                if (latest != null) {
                    Object value = null;
                    if (latest.getGiaTriSo() != null) value = latest.getGiaTriSo();
                    else if (latest.getGiaTriLogic() != null) value = latest.getGiaTriLogic();
                    else if (latest.getGiaTriChuoi() != null) value = latest.getGiaTriChuoi();
                    facts.put(field, value);
                }
            }
            
            // Ưu tiên ghi đè bằng giá trị real-time từ dataLog
            if (dataLog.getTenTruong() != null) {
                String fieldName = dataLog.getTenTruong();
                Object value = null;
                if (dataLog.getGiaTriSo() != null) value = dataLog.getGiaTriSo();
                else if (dataLog.getGiaTriLogic() != null) value = dataLog.getGiaTriLogic();
                else if (dataLog.getGiaTriChuoi() != null) value = dataLog.getGiaTriChuoi();
                
                if (idents.contains(fieldName)) {
                    facts.put(fieldName, value);
                }
            }

            boolean match = EasyRulesEngine.evaluate(expr, facts);
            
            if (match && sustained(rule)) {
                triggerAction(rule);
            } else if (!match) {
                satisfiedSince.remove(rule.getMaLuat());
                lastTriggered.remove(rule.getMaLuat());
            }
        } catch (Exception ex) {
            logger.error("❌ Lỗi evaluate luật {}: {}", rule.getMaLuat(), ex.getMessage());
        }
    }

    private boolean sustained(LuatNguong rule) {
        Integer seconds = rule.getThoiGianDuyTriDieuKien();
        if (seconds == null || seconds <= 0) return true;
        var now = java.time.Instant.now();
        satisfiedSince.putIfAbsent(rule.getMaLuat(), now);
        var since = satisfiedSince.get(rule.getMaLuat());
        if (since.equals(now)) return false; // vừa bắt đầu thoả mãn
        long elapsed = java.time.Duration.between(since, now).getSeconds();
        return elapsed >= seconds;
    }

    private void triggerAction(LuatNguong rule) {
        String action = rule.getLenhHanhDong() == null ? "" : rule.getLenhHanhDong().trim().toLowerCase();
        
        String normalized;
        switch (action) {
            case "hoat_dong":
            case "on":
            case "bat":
                normalized = "hoat_dong";
                break;
            case "tat":
            case "off":
                normalized = "tat";
                break;
            default:
                normalized = action.isEmpty() ? "tat" : action;
        }
        
        thietBiService.capNhatTrangThaiThietBi(
            rule.getThietBi().getMaThietBi(),
            normalized
        );
        
        // Ghi cảnh báo vào lịch sử thông báo (CHỈ LẦN ĐẦU TIÊN khi luật trigger)
        try {
            var now = java.time.Instant.now();
            var lastTrigger = lastTriggered.get(rule.getMaLuat());
            
            // Chỉ ghi notification nếu đã qua ít nhất 60 giây kể từ lần trigger trước
            boolean shouldCreateNotification = lastTrigger == null || 
                java.time.Duration.between(lastTrigger, now).getSeconds() >= 60;
            
            if (shouldCreateNotification) {
                String actionText = normalized.equals("hoat_dong") ? "BẬT" : "TẮT";
                String tieuDe = "Cảnh báo: Luật tự động đã kích hoạt";
                String noiDung = String.format(
                    "Luật '%s' đã tự động %s thiết bị '%s' lúc %s",
                    rule.getBieuThucLogic(),
                    actionText,
                    rule.getThietBi().getTenThietBi(),
                    java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
                );
                
                // Tạo thông báo có cả thông tin thiết bị và khu vực
                com.iot.management.model.entity.ThongBao thongBao = thongBaoService.createDeviceNotification(
                    rule.getThietBi().getChuSoHuu(),
                    rule.getThietBi(),
                    tieuDe,
                    noiDung,
                    "WARNING"
                );
                
                // Cập nhật thêm khu vực nếu có
                if (rule.getThietBi().getKhuVuc() != null) {
                    thongBao.setKhuVuc(rule.getThietBi().getKhuVuc());
                    thongBaoRepository.save(thongBao);
                }
                
                // Đánh dấu đã trigger
                lastTriggered.put(rule.getMaLuat(), now);
                
                logger.info("📝 Rule {} triggered: {} device {}", 
                    rule.getMaLuat(), actionText, rule.getThietBi().getMaThietBi());
            }
        } catch (Exception e) {
            logger.error("❌ Lỗi ghi thông báo: {}", e.getMessage());
        }
        
        // Send WebSocket notification
        try {
            Map<String, Object> notification = new HashMap<>();
            notification.put("ruleId", rule.getMaLuat());
            notification.put("deviceId", rule.getThietBi().getMaThietBi());
            notification.put("action", normalized);
            notification.put("message", "Luật '" + rule.getBieuThucLogic() + "' đã kích hoạt: " + 
                (normalized.equals("hoat_dong") ? "BẬT" : "TẮT") + " thiết bị");
            notification.put("timestamp", java.time.LocalDateTime.now().toString());
            
            String topic = "/topic/device/" + rule.getThietBi().getMaThietBi() + "/rule-activation";
            messagingTemplate.convertAndSend(topic, notification);
        } catch (Exception e) {
            logger.error("❌ Lỗi gửi WebSocket: {}", e.getMessage());
        }
    }

    // Trích xuất danh sách identifier (tên trường) từ biểu thức để lấy giá trị mới nhất
    private java.util.Set<String> extractIdentifiers(String expr) {
        java.util.Set<String> out = new java.util.HashSet<>();
        if (expr == null) return out;
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("\\b[a-zA-Z_][a-zA-Z0-9_]*\\b").matcher(expr);
        java.util.Set<String> keywords = java.util.Set.of("and","or","not","true","false");
        while (m.find()) {
            String id = m.group();
            if (!keywords.contains(id.toLowerCase())) {
                out.add(id);
            }
        }
        return out;
    }
}