package com.iot.management.service.impl;

import com.iot.management.model.entity.LoaiThietBi;
import com.iot.management.model.repository.LoaiThietBiRepository;
import com.iot.management.service.LoaiThietBiService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LoaiThietBiServiceImpl implements LoaiThietBiService {

    private final LoaiThietBiRepository loaiThietBiRepository;

    public LoaiThietBiServiceImpl(LoaiThietBiRepository loaiThietBiRepository) {
        this.loaiThietBiRepository = loaiThietBiRepository;
    }

    @Override
    public LoaiThietBi createDeviceType(LoaiThietBi loaiThietBi) {
        return loaiThietBiRepository.save(loaiThietBi);
    }

    @Override
    public List<LoaiThietBi> findAllDeviceTypes() {
        return loaiThietBiRepository.findAll();
    }

    @Override
    public Optional<LoaiThietBi> findById(Long id) {
        return loaiThietBiRepository.findById(id);
    }

    @Override
    public LoaiThietBi updateDeviceType(Long id, LoaiThietBi loaiThietBi) {
        LoaiThietBi existing = loaiThietBiRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy loại thiết bị với ID: " + id));
        
        existing.setTenLoai(loaiThietBi.getTenLoai());
        existing.setMoTa(loaiThietBi.getMoTa());
        existing.setNhomThietBi(loaiThietBi.getNhomThietBi());
        
        return loaiThietBiRepository.save(existing);
    }

    @Override
    public void deleteDeviceType(Long id) {
        if (!loaiThietBiRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy loại thiết bị với ID: " + id);
        }
        loaiThietBiRepository.deleteById(id);
    }
}