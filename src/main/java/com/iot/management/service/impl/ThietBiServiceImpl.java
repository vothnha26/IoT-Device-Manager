package com.iot.management.service.impl;

import com.iot.management.model.entity.KhuVuc;
import com.iot.management.model.entity.LoaiThietBi;
import com.iot.management.model.entity.NguoiDung;
import com.iot.management.model.entity.ThietBi;
import com.iot.management.model.enums.DuAnRole;
import com.iot.management.repository.KhuVucRepository;
import com.iot.management.repository.LoaiThietBiRepository;
import com.iot.management.repository.NguoiDungRepository;
import com.iot.management.repository.ThietBiRepository;
import com.iot.management.service.ThietBiService;
import com.iot.management.service.ThietBiAuthorizationService;
import com.iot.management.service.DuAnAuthorizationService;
import com.iot.management.websocket.DeviceSessionRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ThietBiServiceImpl implements ThietBiService {
    private static final Logger logger = LoggerFactory.getLogger(ThietBiServiceImpl.class);

    private final ThietBiRepository thietBiRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final LoaiThietBiRepository loaiThietBiRepository;
    private final KhuVucRepository khuVucRepository;
    private final DeviceSessionRegistry deviceSessionRegistry;

    @Autowired
    private ThietBiAuthorizationService thietBiAuthorizationService;

    @Autowired
    private DuAnAuthorizationService duAnAuthorizationService;

    public ThietBiServiceImpl(ThietBiRepository thietBiRepository,
            NguoiDungRepository nguoiDungRepository,
            LoaiThietBiRepository loaiThietBiRepository,
            KhuVucRepository khuVucRepository,
            DeviceSessionRegistry deviceSessionRegistry) {
        this.thietBiRepository = thietBiRepository;
        this.nguoiDungRepository = nguoiDungRepository;
        this.loaiThietBiRepository = loaiThietBiRepository;
        this.khuVucRepository = khuVucRepository;
        this.deviceSessionRegistry = deviceSessionRegistry;
    }

    @Override
    public ThietBi createDevice(Long ownerId, ThietBi thietBi) {
        // 1. Tìm và gán chủ sở hữu
        NguoiDung owner = nguoiDungRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + ownerId));
        thietBi.setChuSoHuu(owner);

        // 2. (Tùy chọn) Tìm và gán Loại thiết bị
        if (thietBi.getLoaiThietBi() != null && thietBi.getLoaiThietBi().getMaLoaiThietBi() != null) {
            Long loaiThietBiId = thietBi.getLoaiThietBi().getMaLoaiThietBi();

            // --- SỬA LỖI Ở ĐÂY ---
            // Bỏ .intValue() và truyền trực tiếp biến Long
            LoaiThietBi type = loaiThietBiRepository.findById(loaiThietBiId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy loại thiết bị với ID: " + loaiThietBiId));
            thietBi.setLoaiThietBi(type);
        }

        // 3. (Tùy chọn) Tìm và gán Khu vực
        if (thietBi.getKhuVuc() != null && thietBi.getKhuVuc().getMaKhuVuc() != null) {
            Long khuVucId = thietBi.getKhuVuc().getMaKhuVuc();
            KhuVuc location = khuVucRepository.findById(khuVucId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy khu vực với ID: " + khuVucId));
            thietBi.setKhuVuc(location);
        }

        // 4. Các logic khác
        thietBi.setTokenThietBi(UUID.randomUUID().toString());

        // Nếu không có trạng thái được set từ client, mặc định là "hoat_dong"
        if (thietBi.getTrangThai() == null || thietBi.getTrangThai().trim().isEmpty()) {
            thietBi.setTrangThai("hoat_dong");
        }

        // Nếu không có ngày lắp đặt, set ngày hiện tại
        if (thietBi.getNgayLapDat() == null) {
            thietBi.setNgayLapDat(LocalDate.now());
        }

        thietBi.setLanHoatDongCuoi(LocalDateTime.now());

        return thietBiRepository.save(thietBi);
    }

    @Override
    public List<ThietBi> findDevicesByOwner(Long ownerId) {
        return thietBiRepository.findByChuSoHuu_MaNguoiDung(ownerId);
    }

    @Override
    public List<ThietBi> findDevicesByKhuVuc(Long maKhuVuc) {
        return thietBiRepository.findByKhuVuc_MaKhuVuc(maKhuVuc);
    }

    @Override
    public List<ThietBi> findThietBiCoQuyenXemTrongKhuVuc(Long maKhuVuc, Long maNguoiDung) {
        // Lấy tất cả thiết bị trong khu vực
        List<ThietBi> allDevices = thietBiRepository.findByKhuVuc_MaKhuVuc(maKhuVuc);

        if (allDevices.isEmpty()) {
            return allDevices;
        }

        // Lấy maDuAn từ thiết bị đầu tiên (tất cả thiết bị trong cùng khu vực thuộc
        // cùng dự án)
        Long maDuAn = allDevices.get(0).getKhuVuc().getDuAn().getMaDuAn();

        // Kiểm tra vai trò
        DuAnRole role = duAnAuthorizationService.layVaiTroTrongDuAn(maDuAn, maNguoiDung);

        // CHU_SO_HUU và QUAN_LY thấy tất cả thiết bị
        if (role == DuAnRole.CHU_SO_HUU || role == DuAnRole.QUAN_LY) {
            return allDevices;
        }

        // NGUOI_DUNG chỉ thấy thiết bị có bất kỳ quyền nào (VIEW, CONTROL, MANAGE)
        // Kiểm tra qua coQuyenTruyCapThietBi (trả về true nếu có bất kỳ quyền nào)
        return allDevices.stream()
                .filter(thietBi -> thietBiAuthorizationService.coQuyenTruyCapThietBi(thietBi.getMaThietBi(),
                        maNguoiDung))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ThietBi> findDeviceById(Long deviceId) {
        return thietBiRepository.findById(deviceId);
    }

    @Override
    public void deleteDevice(Long deviceId) {
        ThietBi thietBi = thietBiRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thiết bị với ID: " + deviceId));

        logger.info("🗑️ Xóa thiết bị ID: {} - Cascade sẽ tự động xóa lịch trình và nhật ký dữ liệu", deviceId);

        // Cascade sẽ tự động xóa:
        // - LichTrinh (cascade = CascadeType.ALL, orphanRemoval = true)
        // - NhatKyDuLieu (cascade = CascadeType.ALL, orphanRemoval = true)
        thietBiRepository.delete(thietBi);

        logger.info("✅ Đã xóa thiết bị ID: {} thành công", deviceId);
    }

    @Override
    public void capNhatTrangThaiThietBi(Long deviceId, String trangThai) {
        ThietBi thietBi = thietBiRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thiết bị với ID: " + deviceId));

        // Chuẩn hóa trạng thái về 2 giá trị: hoat_dong | tat
        String normalized = (trangThai == null ? "" : trangThai.trim().toLowerCase());
        String finalState;
        switch (normalized) {
            case "hoat_dong":
            case "on":
            case "bat":
                finalState = "hoat_dong";
                break;
            case "tat":
            case "off":
                finalState = "tat";
                break;
            default:
                // Nếu gửi linh tinh thì mặc định tắt
                finalState = "tat";
                break;
        }

        thietBi.setTrangThai(finalState);
        thietBi.setLanHoatDongCuoi(LocalDateTime.now());
        thietBiRepository.save(thietBi);

        // GỬI LỆNH ĐIỀU KHIỂN ĐẾN THIẾT BỊ THẬT QUA RAW WEBSOCKET
        try {
            boolean sent = deviceSessionRegistry.sendCommand(deviceId, finalState);
            if (sent) {
                logger.info("✅ Control command sent to device {}: {}", deviceId, finalState);
            } else {
                logger.warn("⚠️  Device {} is offline, command not sent", deviceId);
            }
        } catch (Exception e) {
            logger.error("❌ Error sending command to device {}: {}", deviceId, e.getMessage(), e);
        }
    }

    @Override
    public ThietBi updateDevice(Long deviceId, ThietBi thietBiMoi) {
        ThietBi thietBiCu = thietBiRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thiết bị với ID: " + deviceId));

        // Cập nhật thông tin có thể thay đổi
        if (thietBiMoi.getTenThietBi() != null) {
            thietBiCu.setTenThietBi(thietBiMoi.getTenThietBi());
        }

        // Cập nhật loại thiết bị nếu có thay đổi
        if (thietBiMoi.getLoaiThietBi() != null && thietBiMoi.getLoaiThietBi().getMaLoaiThietBi() != null) {
            LoaiThietBi loaiThietBi = loaiThietBiRepository.findById(thietBiMoi.getLoaiThietBi().getMaLoaiThietBi())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy loại thiết bị"));
            thietBiCu.setLoaiThietBi(loaiThietBi);
        }

        // Cập nhật khu vực nếu có thay đổi
        if (thietBiMoi.getKhuVuc() != null && thietBiMoi.getKhuVuc().getMaKhuVuc() != null) {
            KhuVuc khuVuc = khuVucRepository.findById(thietBiMoi.getKhuVuc().getMaKhuVuc())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy khu vực"));
            thietBiCu.setKhuVuc(khuVuc);
        } else if (thietBiMoi.getKhuVuc() != null && thietBiMoi.getKhuVuc().getMaKhuVuc() == null) {
            // Nếu client gửi khuVuc = null, có nghĩa là muốn bỏ khu vực
            thietBiCu.setKhuVuc(null);
        }

        // Cập nhật trạng thái nếu có
        if (thietBiMoi.getTrangThai() != null && !thietBiMoi.getTrangThai().trim().isEmpty()) {
            thietBiCu.setTrangThai(thietBiMoi.getTrangThai());
        }

        // Cập nhật ngày lắp đặt nếu có
        if (thietBiMoi.getNgayLapDat() != null) {
            thietBiCu.setNgayLapDat(thietBiMoi.getNgayLapDat());
        }

        thietBiCu.setLanHoatDongCuoi(LocalDateTime.now());

        return thietBiRepository.save(thietBiCu);
    }
}