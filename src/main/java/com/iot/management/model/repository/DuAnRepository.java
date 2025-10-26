package com.iot.management.model.repository;

import com.iot.management.model.entity.DuAn;
import com.iot.management.model.entity.NguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface DuAnRepository extends JpaRepository<DuAn, Long> {
    List<DuAn> findByNguoiDungAndTrangThai(NguoiDung nguoiDung, String trangThai);
    List<DuAn> findByNguoiDung(NguoiDung nguoiDung);
    boolean existsByTenDuAnAndNguoiDung(String tenDuAn, NguoiDung nguoiDung);
    Optional<DuAn> findByMaDuAnAndNguoiDungAndTrangThai(Long maDuAn, NguoiDung nguoiDung, String trangThai);
}