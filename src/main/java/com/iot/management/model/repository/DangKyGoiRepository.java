package com.iot.management.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.iot.management.model.entity.DangKyGoi;
import java.util.Optional;

@Repository
public interface DangKyGoiRepository extends JpaRepository<DangKyGoi, Long> {
    Optional<DangKyGoi> findByNguoiDung_MaNguoiDungAndTrangThai(Long maNguoiDung, String trangThai);

}