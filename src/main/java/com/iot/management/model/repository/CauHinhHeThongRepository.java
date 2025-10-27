package com.iot.management.model.repository;

import com.iot.management.model.entity.CauHinhHeThong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CauHinhHeThongRepository extends JpaRepository<CauHinhHeThong, String> {
    // Vì khóa chính là String, nên kiểu ID là String
}