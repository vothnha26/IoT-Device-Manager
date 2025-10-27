package com.iot.management.model.repository;

import com.iot.management.model.entity.LichTrinh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LichTrinhRepository extends JpaRepository<LichTrinh, Long> {

    // Lấy tất cả các lịch trình đang được kích hoạt để bộ lập lịch xử lý
    List<LichTrinh> findByKichHoatIsTrue();

    // Find schedules for a specific device
    List<LichTrinh> findByThietBi_MaThietBi(Long maThietBi);
}