package com.iot.management.service.impl;

import com.iot.management.model.entity.ThanhToan;
import com.iot.management.repository.ThanhToanRepository;
import com.iot.management.service.ThanhToanService;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class ThanhToanServiceImpl implements ThanhToanService {

    private final ThanhToanRepository thanhToanRepository;

    public ThanhToanServiceImpl(ThanhToanRepository thanhToanRepository) {
        this.thanhToanRepository = thanhToanRepository;
    }

    @Override
    public ThanhToan save(ThanhToan thanhToan) {
        return thanhToanRepository.save(thanhToan);
    }

    @Override
    public Optional<ThanhToan> findByMaGiaoDichCongThanhToan(String maGiaoDich) {
        return thanhToanRepository.findByMaGiaoDichCongThanhToan(maGiaoDich);
    }
}