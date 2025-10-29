package com.iot.management.repository;

import com.iot.management.model.entity.NguoiDung;
import com.iot.management.model.entity.ThietBi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ThietBiRepository extends JpaRepository<ThietBi, Long> {

    Optional<ThietBi> findByTokenThietBi(String token);

    // Sửa: Truy vấn qua đối tượng chuSoHuu
    List<ThietBi> findByChuSoHuu_MaNguoiDung(Long maNguoiDung);
    
    long countByChuSoHuu(NguoiDung chuSoHuu);

    // Sửa: Truy vấn qua đối tượng khuVuc
    List<ThietBi> findByKhuVuc_MaKhuVuc(Long maKhuVuc);
    
    // Đếm số thiết bị trong khu vực
    long countByKhuVuc_MaKhuVuc(Long maKhuVuc);
    
    // Truy vấn tất cả thiết bị trong dự án
    List<ThietBi> findByKhuVucDuAnMaDuAn(Long maDuAn);
}