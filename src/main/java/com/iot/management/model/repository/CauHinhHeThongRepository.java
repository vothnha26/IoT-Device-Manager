package com.iot.management.model.repository;

import com.iot.management.model.entity.CauHinhHeThong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CauHinhHeThongRepository extends JpaRepository<CauHinhHeThong, String> {
    // Vì khóa chính là String (ten_cau_hinh), nên kiểu ID là String
    
    Optional<CauHinhHeThong> findByTenCauHinh(String tenCauHinh);
    
    boolean existsByTenCauHinh(String tenCauHinh);
}