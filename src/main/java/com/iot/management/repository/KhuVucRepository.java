package com.iot.management.repository;

import com.iot.management.model.entity.KhuVuc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KhuVucRepository extends JpaRepository<KhuVuc, Long> {
    
    // Tìm tất cả khu vực của một người dùng
    List<KhuVuc> findByChuSoHuu_MaNguoiDung(Long maNguoiDung);
    
    // Đếm số khu vực của một người dùng
    Long countByChuSoHuu_MaNguoiDung(Long maNguoiDung);
    
    // Tìm tất cả khu vực của một dự án
    @Query("SELECT k FROM KhuVuc k WHERE k.duAn.maDuAn = :maDuAn")
    List<KhuVuc> findByDuAn_MaDuAn(Long maDuAn);
    
    // Tìm theo mã dự án (phương thức rút gọn)
    List<KhuVuc> findByDuAnMaDuAn(Long maDuAn);
    
    // Tìm tất cả khu vực mà người dùng có quyền truy cập (owned + shared)
    @Query("SELECT DISTINCT k FROM KhuVuc k " +
           "LEFT JOIN k.phanQuyenKhuVucs pq " +
           "WHERE k.chuSoHuu.maNguoiDung = :userId OR pq.nguoiDung.maNguoiDung = :userId")
    List<KhuVuc> findAllAccessibleByUser(Long userId);
}