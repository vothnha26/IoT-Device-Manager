package com.iot.management.model.repository;

import com.iot.management.model.entity.LichSuCanhBao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LichSuCanhBaoRepository extends JpaRepository<LichSuCanhBao, Long> {
    // Thường chỉ cần các hàm CRUD cơ bản
}