package com.iot.management.service.impl;

import com.iot.management.model.entity.DangKyGoi;
import com.iot.management.repository.DangKyGoiRepository;
import com.iot.management.service.DangKyGoiService;
import org.springframework.stereotype.Service;

@Service
public class DangKyGoiServiceImpl implements DangKyGoiService {

    private final DangKyGoiRepository dangKyGoiRepository;

    public DangKyGoiServiceImpl(DangKyGoiRepository dangKyGoiRepository) {
        this.dangKyGoiRepository = dangKyGoiRepository;
    }

    @Override
    public DangKyGoi save(DangKyGoi dangKyGoi) {
        return dangKyGoiRepository.save(dangKyGoi);
    }
}