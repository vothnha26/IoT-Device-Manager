package com.iot.management.model.repository;

import com.iot.management.model.entity.LoaiThietBi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoaiThietBiRepository extends JpaRepository<LoaiThietBi, Long> {
    // Tìm loại thiết bị theo tên
    Optional<LoaiThietBi> findByTenLoai(String tenLoai);
}