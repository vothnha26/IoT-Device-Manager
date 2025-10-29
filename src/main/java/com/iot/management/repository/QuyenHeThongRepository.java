package com.iot.management.repository;

import com.iot.management.model.entity.QuyenHeThong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuyenHeThongRepository extends JpaRepository<QuyenHeThong, Long> {
    
    Optional<QuyenHeThong> findByTenQuyen(String tenQuyen);
    
    List<QuyenHeThong> findByMaNhom(String maNhom);
    
    boolean existsByTenQuyen(String tenQuyen);
}
