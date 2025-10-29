package com.iot.management.repository;

import com.iot.management.model.entity.LoaiThietBi;
import com.iot.management.model.entity.PhienBanFirmware;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhienBanFirmwareRepository extends JpaRepository<PhienBanFirmware, Long> {
    Optional<PhienBanFirmware> findTopByLoaiThietBi_MaLoaiThietBiOrderByNgayPhatHanhDesc(Long maLoaiThietBi);
    List<PhienBanFirmware> findByLoaiThietBi_MaLoaiThietBiOrderByNgayPhatHanhDesc(Long maLoaiThietBi);
    boolean existsByLoaiThietBiAndSoPhienBan(LoaiThietBi loaiThietBi, String soPhienBan);
}