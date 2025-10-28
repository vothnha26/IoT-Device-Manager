package com.iot.management.model.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iot.management.model.entity.NguoiDung;
import com.iot.management.model.entity.ThongBao;

@Repository
public interface ThongBaoRepository extends JpaRepository<ThongBao, Long> {

    // Lấy tất cả thông báo của user, sắp xếp mới nhất trước
    List<ThongBao> findByNguoiDungOrderByThoiGianTaoDesc(NguoiDung nguoiDung);

    // Lấy thông báo chưa đọc của user
    List<ThongBao> findByNguoiDungAndDaDocOrderByThoiGianTaoDesc(NguoiDung nguoiDung, Boolean daDoc);

    // Đếm số thông báo chưa đọc
    Long countByNguoiDungAndDaDoc(NguoiDung nguoiDung, Boolean daDoc);

    // Lấy thông báo theo loại
    List<ThongBao> findByNguoiDungAndLoaiThongBaoOrderByThoiGianTaoDesc(NguoiDung nguoiDung, String loaiThongBao);

    // Xóa thông báo cũ hơn X ngày
    @Modifying
    @Query("DELETE FROM ThongBao t WHERE t.thoiGianTao < :cutoffDate")
    void deleteOldNotifications(@Param("cutoffDate") LocalDateTime cutoffDate);

    // Lấy N thông báo mới nhất
    List<ThongBao> findTop10ByNguoiDungOrderByThoiGianTaoDesc(NguoiDung nguoiDung);
    
    // Đếm số thông báo trong khoảng thời gian
    long countByThoiGianTaoBetween(LocalDateTime start, LocalDateTime end);
}
