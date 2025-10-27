package com.iot.management.service.impl;

import com.iot.management.model.entity.LuatNguong;
import com.iot.management.model.entity.NhatKyDuLieu;
import com.iot.management.model.entity.LichTrinh;
import com.iot.management.model.repository.LuatNguongRepository;
import com.iot.management.model.repository.LichTrinhRepository;
import com.iot.management.service.TuDongHoaService;
import com.iot.management.service.ThietBiService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

import java.util.List;

@Service
public class TuDongHoaServiceImpl implements TuDongHoaService {
    private static final Logger logger = LoggerFactory.getLogger(TuDongHoaServiceImpl.class);

    private final LuatNguongRepository luatNguongRepository;
    private final LichTrinhRepository lichTrinhRepository;
    private final ThietBiService thietBiService;

    public TuDongHoaServiceImpl(
            LuatNguongRepository luatNguongRepository,
            LichTrinhRepository lichTrinhRepository,
            ThietBiService thietBiService) {
        this.luatNguongRepository = luatNguongRepository;
        this.lichTrinhRepository = lichTrinhRepository;
        this.thietBiService = thietBiService;
    }

    @Override
    @Transactional
    public LuatNguong saveRule(LuatNguong luatNguong) {
        try {
            return luatNguongRepository.save(luatNguong);
        } catch (Exception e) {
            logger.error("Lỗi khi lưu luật ngưỡng: {}", e.getMessage());
            throw new RuntimeException("Không thể lưu luật ngưỡng", e);
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
            // Lấy tất cả các luật áp dụng cho thiết bị này
            List<LuatNguong> rules = luatNguongRepository.findByThietBi_MaThietBiAndKichHoatIsTrue(
                dataLog.getThietBi().getMaThietBi()
            );
            
            for (LuatNguong rule : rules) {
                // So sánh giá trị với ngưỡng và thực hiện hành động
                processRule(rule, dataLog);
            }
        } catch (Exception e) {
            logger.error("Lỗi khi xử lý luật tự động cho dữ liệu ID {}: {}", 
                dataLog.getMaNhatKy(), e.getMessage());
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

    private void processRule(LuatNguong rule, NhatKyDuLieu dataLog) {
        try {
            if (!rule.getTenTruong().equals(dataLog.getTenTruong())) {
                return; // Bỏ qua nếu không phải trường cần kiểm tra
            }

            Double currentValue = null;
            Double thresholdValue = null;

            // Lấy giá trị hiện tại
            if (dataLog.getGiaTriSo() != null) {
                currentValue = dataLog.getGiaTriSo().doubleValue();
            }

            // Parse giá trị ngưỡng
            try {
                thresholdValue = Double.parseDouble(rule.getGiaTriNguong());
            } catch (NumberFormatException e) {
                logger.warn("Giá trị ngưỡng không hợp lệ cho luật {}: {}", 
                    rule.getMaLuat(), rule.getGiaTriNguong());
                return;
            }

            if (currentValue != null && thresholdValue != null) {
                boolean shouldTrigger = false;
                switch (rule.getPhepToan()) {
                    case ">" -> shouldTrigger = currentValue > thresholdValue;
                    case "<" -> shouldTrigger = currentValue < thresholdValue;
                    case "=" -> shouldTrigger = currentValue.equals(thresholdValue);
                    case ">=" -> shouldTrigger = currentValue >= thresholdValue;
                    case "<=" -> shouldTrigger = currentValue <= thresholdValue;
                }

                if (shouldTrigger) {
                    // Gửi lệnh hành động cho thiết bị
                    thietBiService.capNhatTrangThaiThietBi(
                        rule.getThietBi().getMaThietBi(),
                        rule.getLenhHanhDong()
                    );
                    
                    logger.info("Đã kích hoạt luật {} cho thiết bị {}: {} {} {}", 
                        rule.getMaLuat(), 
                        rule.getThietBi().getMaThietBi(),
                        currentValue,
                        rule.getPhepToan(),
                        thresholdValue);
                }
            }
        } catch (Exception e) {
            logger.error("Lỗi khi xử lý luật {}: {}", rule.getMaLuat(), e.getMessage());
            throw new RuntimeException("Không thể xử lý luật", e);
        }
    }
}