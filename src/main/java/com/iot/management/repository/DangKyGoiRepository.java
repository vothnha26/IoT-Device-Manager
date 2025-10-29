package com.iot.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iot.management.model.entity.DangKyGoi;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DangKyGoiRepository extends JpaRepository<DangKyGoi, Long> {
    Optional<DangKyGoi> findByNguoiDung_MaNguoiDungAndTrangThai(Long maNguoiDung, String trangThai);
    List<DangKyGoi> findByTrangThai(String trangThai);
    
    // Tìm các gói đã hết hạn nhưng vẫn còn status ACTIVE
    List<DangKyGoi> findByTrangThaiAndNgayKetThucBefore(String trangThai, LocalDateTime now);
    
    // Tìm các gói sắp hết hạn (trong khoảng thời gian)
    List<DangKyGoi> findByTrangThaiAndNgayKetThucBetween(
        String trangThai,
        LocalDateTime start,
        LocalDateTime end
    );
    
    // Đếm số gói đã hết hạn
    @Query("SELECT COUNT(d) FROM DangKyGoi d WHERE d.trangThai = :trangThai AND d.ngayKetThuc < :now")
    long countByTrangThaiAndExpired(@Param("trangThai") String trangThai, @Param("now") LocalDateTime now);
}