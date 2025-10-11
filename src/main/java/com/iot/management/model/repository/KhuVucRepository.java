package com.iot.management.model.repository;

import com.iot.management.model.entity.KhuVuc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KhuVucRepository extends JpaRepository<KhuVuc, Long> {
    
    // Tìm tất cả khu vực của một người dùng
    List<KhuVuc> findByChuSoHuu_MaNguoiDung(Long maNguoiDung);
    
    // Tìm tất cả khu vực gốc (không có cha) của một người dùng
    List<KhuVuc> findByChuSoHuu_MaNguoiDungAndKhuVucChaIsNull(Long maNguoiDung);
    
    // Tìm tất cả các khu vực con của một khu vực cha
    List<KhuVuc> findByKhuVucCha_MaKhuVuc(Long maKhuVucCha);
}