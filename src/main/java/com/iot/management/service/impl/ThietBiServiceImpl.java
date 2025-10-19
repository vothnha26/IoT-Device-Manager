package com.iot.management.service.impl;

import com.iot.management.model.entity.KhuVuc;
import com.iot.management.model.entity.LoaiThietBi;
import com.iot.management.model.entity.NguoiDung;
import com.iot.management.model.entity.ThietBi;
import com.iot.management.model.repository.KhuVucRepository;
import com.iot.management.model.repository.LoaiThietBiRepository;
import com.iot.management.model.repository.NguoiDungRepository;
import com.iot.management.model.repository.ThietBiRepository;
import com.iot.management.service.ThietBiService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ThietBiServiceImpl implements ThietBiService {

    private final ThietBiRepository thietBiRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final LoaiThietBiRepository loaiThietBiRepository;
    private final KhuVucRepository khuVucRepository;

    public ThietBiServiceImpl(ThietBiRepository thietBiRepository,
                              NguoiDungRepository nguoiDungRepository,
                              LoaiThietBiRepository loaiThietBiRepository,
                              KhuVucRepository khuVucRepository) {
        this.thietBiRepository = thietBiRepository;
        this.nguoiDungRepository = nguoiDungRepository;
        this.loaiThietBiRepository = loaiThietBiRepository;
        this.khuVucRepository = khuVucRepository;
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
        
        // Nếu không có trạng thái được set từ client, mặc định là "hoat dong"
        if (thietBi.getTrangThai() == null || thietBi.getTrangThai().trim().isEmpty()) {
            thietBi.setTrangThai("hoat dong");
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
    public Optional<ThietBi> findDeviceById(Long deviceId) {
        return thietBiRepository.findById(deviceId);
    }

    @Override
    public void deleteDevice(Long deviceId) {
        if (!thietBiRepository.existsById(deviceId)) {
            throw new RuntimeException("Không tìm thấy thiết bị với ID: " + deviceId);
        }
        thietBiRepository.deleteById(deviceId);
    }

    @Override
    public void capNhatTrangThaiThietBi(Long deviceId, String trangThai) {
        ThietBi thietBi = thietBiRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thiết bị với ID: " + deviceId));
                
        thietBi.setTrangThai(trangThai);
        thietBi.setLanHoatDongCuoi(LocalDateTime.now());
        thietBiRepository.save(thietBi);
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