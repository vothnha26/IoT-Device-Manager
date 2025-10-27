package com.iot.management.model.repository;

import com.iot.management.model.entity.GoiCuoc;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface GoiCuocRepository extends JpaRepository<GoiCuoc, Integer> {
    // Phương thức này giúp tìm gói cước theo tên để tránh tạo trùng lặp
    Optional<GoiCuoc> findByTenGoi(String tenGoi);    
}