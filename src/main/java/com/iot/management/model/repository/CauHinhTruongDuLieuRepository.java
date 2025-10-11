package com.iot.management.model.repository;

import com.iot.management.model.entity.CauHinhTruongDuLieu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CauHinhTruongDuLieuRepository extends JpaRepository<CauHinhTruongDuLieu, Long> {
    
    // Tìm tất cả các cấu hình trường dữ liệu (datastream) cho một loại thiết bị
    List<CauHinhTruongDuLieu> findByLoaiThietBi_MaLoaiThietBi(Integer maLoaiThietBi);
}