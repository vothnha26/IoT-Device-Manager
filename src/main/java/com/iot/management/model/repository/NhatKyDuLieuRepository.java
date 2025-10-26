package com.iot.management.model.repository;

import com.iot.management.model.entity.NhatKyDuLieu;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NhatKyDuLieuRepository extends JpaRepository<NhatKyDuLieu, Long> {

    // Lấy nhật ký của một thiết bị trong một khoảng thời gian, có phân trang
    List<NhatKyDuLieu> findByThietBi_MaThietBiAndThoiGianBetween(Long maThietBi, LocalDateTime start, LocalDateTime end, Pageable pageable);

    // Lấy nhật ký mới nhất cho một thiết bị (10 records)
    List<NhatKyDuLieu> findTop10ByThietBi_MaThietBiOrderByThoiGianDesc(Long maThietBi);
    
    // Lấy 1 bản ghi mới nhất
    List<NhatKyDuLieu> findTop1ByThietBi_MaThietBiOrderByThoiGianDesc(Long maThietBi);

    // Lấy theo thiết bị + tên trường (không phân biệt hoa thường) trong khoảng thời gian, tăng dần theo thời gian
    List<NhatKyDuLieu> findByThietBi_MaThietBiAndTenTruongIgnoreCaseAndThoiGianBetweenOrderByThoiGianAsc(
            Long maThietBi,
            String tenTruong,
            LocalDateTime start,
            LocalDateTime end
    );
}