package com.iot.management.model.repository;

import com.iot.management.model.entity.NhatKyHeThong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NhatKyHeThongRepository extends JpaRepository<NhatKyHeThong, Long> {
    // Thường chỉ cần các hàm CRUD cơ bản
}