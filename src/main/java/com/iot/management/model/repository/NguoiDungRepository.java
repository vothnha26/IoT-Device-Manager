package com.iot.management.model.repository;

import com.iot.management.model.entity.NguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NguoiDungRepository extends JpaRepository<NguoiDung, Long> {
    Optional<NguoiDung> findByEmail(String email);
    Optional<NguoiDung> findByTenDangNhap(String tenDangNhap);
    boolean existsByEmail(String email);
    boolean existsByTenDangNhap(String tenDangNhap);
}