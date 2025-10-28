package com.iot.management.repository;

import com.iot.management.model.entity.LoiMoiDuAn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoiMoiDuAnRepository extends JpaRepository<LoiMoiDuAn, Long> {
    Optional<LoiMoiDuAn> findByToken(String token);
    Optional<LoiMoiDuAn> findByEmailNguoiNhanAndDuAnMaDuAnAndTrangThai(String email, Long maDuAn, String trangThai);
}
