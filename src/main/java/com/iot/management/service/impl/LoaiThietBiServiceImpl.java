package com.iot.management.service.impl;

import com.iot.management.model.entity.LoaiThietBi;
import com.iot.management.model.repository.LoaiThietBiRepository;
import com.iot.management.service.LoaiThietBiService;
import org.springframework.stereotype.Service;

import java.util.List;

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
}