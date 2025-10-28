package com.iot.management.service;

import com.iot.management.model.dto.request.DuAnRequest;
import com.iot.management.model.entity.DuAn;
import com.iot.management.model.entity.NguoiDung;
import java.util.List;
import java.util.Optional;

public interface DuAnService {
    DuAn create(DuAnRequest request, NguoiDung nguoiDung);
    DuAn update(Long maDuAn, DuAnRequest request, NguoiDung nguoiDung);
    void delete(Long maDuAn, NguoiDung nguoiDung);
    List<DuAn> findAllByNguoiDung(NguoiDung nguoiDung);
    Optional<DuAn> findByIdAndNguoiDung(Long maDuAn, NguoiDung nguoiDung);
    boolean existsByTenDuAnAndNguoiDung(String tenDuAn, NguoiDung nguoiDung);
}