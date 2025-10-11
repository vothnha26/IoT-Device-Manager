package com.iot.management.model.repository;

import com.iot.management.model.entity.LenhDieuKhien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LenhDieuKhienRepository extends JpaRepository<LenhDieuKhien, Long> {

    // Tìm tất cả các lệnh đang chờ xử lý cho một thiết bị
    List<LenhDieuKhien> findByThietBi_MaThietBiAndTrangThai(Long maThietBi, String trangThai);
}