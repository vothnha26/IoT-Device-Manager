package com.iot.management.model.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.iot.management.model.entity.ThanhToan;

@Repository
public interface ThanhToanRepository extends JpaRepository<ThanhToan, Long> {
    // Phương thức tìm kiếm cần thiết cho VNPAY Return/IPN
    Optional<ThanhToan> findByMaGiaoDichCongThanhToan(String maGiaoDichCongThanhToan);
}
