package com.iot.management.model.repository;

import com.iot.management.model.entity.LuatNguong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LuatNguongRepository extends JpaRepository<LuatNguong, Long> {
    List<LuatNguong> findByThietBi_MaThietBiAndKichHoatIsTrue(Long maThietBi);
    List<LuatNguong> findByThietBi_MaThietBi(Long maThietBi);
    
    // Lấy TẤT CẢ luật đang kích hoạt của MỌI thiết bị
    // (để hỗ trợ luật cross-device: thiết bị A phụ thuộc vào dữ liệu thiết bị B)
    List<LuatNguong> findByKichHoatIsTrue();
}