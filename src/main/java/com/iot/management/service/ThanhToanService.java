package com.iot.management.service;

import com.iot.management.model.entity.ThanhToan;
import java.util.Optional;

public interface ThanhToanService {
    ThanhToan save(ThanhToan thanhToan);
    Optional<ThanhToan> findByMaGiaoDichCongThanhToan(String maGiaoDich);
}