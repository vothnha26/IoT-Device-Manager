package com.iot.management.service;

import com.iot.management.model.entity.DangKyGoi;
import com.iot.management.repository.DangKyGoiRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SubscriptionExpiryChecker {

    private static final Logger logger = LoggerFactory.getLogger(SubscriptionExpiryChecker.class);

    private final DangKyGoiRepository dangKyGoiRepository;

    public SubscriptionExpiryChecker(DangKyGoiRepository dangKyGoiRepository) {
        this.dangKyGoiRepository = dangKyGoiRepository;
    }

    /**
     * Chạy mỗi 30 phút để kiểm tra và cập nhật các gói đã hết hạn
     */
    @Scheduled(cron = "0 */30 * * * ?")
    @Transactional
    public void checkAndUpdateExpiredSubscriptions() {
        LocalDateTime now = LocalDateTime.now();

        logger.info("=== BẮT ĐẦU KIỂM TRA GÓI HẾT HẠN ===");
        logger.info("Thời gian hiện tại: {}", now);

        // Tìm các gói đã hết hạn nhưng vẫn còn trạng thái ACTIVE
        List<DangKyGoi> expiredPackages = dangKyGoiRepository
                .findByTrangThaiAndNgayKetThucBefore(DangKyGoi.TRANG_THAI_ACTIVE, now);

        logger.info("Tìm thấy {} gói đã hết hạn", expiredPackages.size());

        int updatedCount = 0;
        for (DangKyGoi dk : expiredPackages) {
            try {
                String oldStatus = dk.getTrangThai();
                dk.setTrangThai("EXPIRED");
                dangKyGoiRepository.save(dk);

                logger.info("✓ Đã cập nhật gói ID={} từ {} -> EXPIRED (User ID={}, Hết hạn: {})",
                        dk.getMaDangKy(),
                        oldStatus,
                        dk.getNguoiDung().getMaNguoiDung(),
                        dk.getNgayKetThuc());

                updatedCount++;

                // notificationService.sendExpiryNotification(dk);

            } catch (Exception e) {
                logger.error("✗ Lỗi khi cập nhật gói ID={}: {}", dk.getMaDangKy(), e.getMessage());
            }
        }

        logger.info("=== KẾT THÚC: Đã cập nhật {}/{} gói ===\n", updatedCount, expiredPackages.size());
    }

    /**
     * Chạy mỗi 30 phút để cảnh báo các gói sắp hết hạn (còn 7 ngày)
     */
    @Scheduled(cron = "0 */30 * * * ?")
    @Transactional
    public void checkExpiringSubscriptions() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sevenDaysLater = now.plusDays(7);

        logger.info("=== KIỂM TRA GÓI SẮP HẾT HẠN ===");
        logger.info("Tìm gói hết hạn từ {} đến {}", now, sevenDaysLater);

        // Tìm các gói sắp hết hạn trong 7 ngày tới
        List<DangKyGoi> expiringPackages = dangKyGoiRepository
                .findByTrangThaiAndNgayKetThucBetween(
                        DangKyGoi.TRANG_THAI_ACTIVE,
                        now,
                        sevenDaysLater);

        logger.info("Tìm thấy {} gói sắp hết hạn trong 7 ngày tới", expiringPackages.size());

        for (DangKyGoi dk : expiringPackages) {
            long daysRemaining = java.time.temporal.ChronoUnit.DAYS.between(now, dk.getNgayKetThuc());

            logger.info("⚠ Gói ID={} (User ID={}) sẽ hết hạn sau {} ngày ({})",
                    dk.getMaDangKy(),
                    dk.getNguoiDung().getMaNguoiDung(),
                    daysRemaining,
                    dk.getNgayKetThuc());

            // notificationService.sendExpiryWarning(dk, daysRemaining);
        }

        logger.info("=== KẾT THÚC KIỂM TRA SẮP HẾT HẠN ===\n");
    }

    /**
     * Chạy mỗi giờ để log thống kê (optional - có thể tắt nếu không cần)
     */
    @Scheduled(cron = "0 0 * * * ?")
    @Transactional(readOnly = true)
    public void logSubscriptionStats() {
        LocalDateTime now = LocalDateTime.now();

        long activeCount = dangKyGoiRepository.findByTrangThai(DangKyGoi.TRANG_THAI_ACTIVE).size();
        long expiredCount = dangKyGoiRepository.countByTrangThaiAndExpired(DangKyGoi.TRANG_THAI_ACTIVE, now);

        if (expiredCount > 0) {
            logger.warn("📊 Thống kê: {} gói ACTIVE | {} gói cần cập nhật EXPIRED", activeCount, expiredCount);
        }
    }
}
