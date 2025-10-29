package com.iot.management.service.impl;

import com.iot.management.model.entity.LoaiThietBi;
import com.iot.management.model.entity.PhienBanFirmware;
import com.iot.management.repository.LoaiThietBiRepository;
import com.iot.management.repository.PhienBanFirmwareRepository;
import com.iot.management.service.PhienBanFirmwareService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PhienBanFirmwareServiceImpl implements PhienBanFirmwareService {

    private final PhienBanFirmwareRepository phienBanFirmwareRepository;
    private final LoaiThietBiRepository loaiThietBiRepository;

    public PhienBanFirmwareServiceImpl(PhienBanFirmwareRepository phienBanFirmwareRepository, LoaiThietBiRepository loaiThietBiRepository) {
        this.phienBanFirmwareRepository = phienBanFirmwareRepository;
        this.loaiThietBiRepository = loaiThietBiRepository;
    }

    @Override
    public PhienBanFirmware save(PhienBanFirmware firmware) {
        Long deviceTypeId = firmware.getLoaiThietBi().getMaLoaiThietBi();
        
        // Bây giờ câu lệnh này sẽ hợp lệ vì repository đã nhận vào Long
        LoaiThietBi deviceType = loaiThietBiRepository.findById(deviceTypeId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy loại thiết bị với ID: " + deviceTypeId));

        firmware.setLoaiThietBi(deviceType);

        return phienBanFirmwareRepository.save(firmware);
    }

    @Override
    public List<PhienBanFirmware> findAllByDeviceType(Long deviceTypeId) {
        return phienBanFirmwareRepository.findByLoaiThietBi_MaLoaiThietBiOrderByNgayPhatHanhDesc(deviceTypeId);
    }

    @Override
    public Optional<PhienBanFirmware> findLatestByDeviceType(Long deviceTypeId) {
        return phienBanFirmwareRepository.findTopByLoaiThietBi_MaLoaiThietBiOrderByNgayPhatHanhDesc(deviceTypeId);
    }

    @Override
    public void deleteById(Long id) {
        if (!phienBanFirmwareRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy phiên bản firmware với ID: " + id);
        }
        phienBanFirmwareRepository.deleteById(id);
    }
}