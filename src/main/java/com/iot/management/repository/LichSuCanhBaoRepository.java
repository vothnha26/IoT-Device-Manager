package com.iot.management.repository;

import com.iot.management.model.entity.LichSuCanhBao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LichSuCanhBaoRepository extends JpaRepository<LichSuCanhBao, Long> {
    
    // Lấy log theo luật (có phân trang)
    Page<LichSuCanhBao> findByLuat_MaLuatOrderByThoiGianDesc(Long maLuat, Pageable pageable);
    
    // Xóa tất cả log của một luật (dùng khi xóa luật)
    void deleteByLuat_MaLuat(Long maLuat);
    
    // Lấy log theo thiết bị
    List<LichSuCanhBao> findByThietBi_MaThietBiOrderByThoiGianDesc(Long maThietBi);
    
    // Đếm số lượng log của luật
    Long countByLuat_MaLuat(Long maLuat);
    
    // Đếm cảnh báo theo danh sách thiết bị và thời gian
    long countByThietBiMaThietBiInAndThoiGianAfter(List<Long> maThietBiList, LocalDateTime thoiGian);
    
    // Lấy danh sách cảnh báo theo danh sách thiết bị
    List<LichSuCanhBao> findTop100ByThietBiMaThietBiInOrderByThoiGianDesc(List<Long> maThietBiList);
}