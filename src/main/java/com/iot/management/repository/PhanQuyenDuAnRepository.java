package com.iot.management.repository;

import com.iot.management.model.entity.DuAn;
import com.iot.management.model.entity.NguoiDung;
import com.iot.management.model.entity.PhanQuyenDuAn;
import com.iot.management.model.enums.DuAnRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PhanQuyenDuAnRepository extends JpaRepository<PhanQuyenDuAn, Long> {
    List<PhanQuyenDuAn> findByNguoiDungAndVaiTro(NguoiDung nguoiDung, DuAnRole vaiTro);
    List<PhanQuyenDuAn> findByNguoiDung(NguoiDung nguoiDung);
    List<PhanQuyenDuAn> findByDuAn(DuAn duAn);
    Optional<PhanQuyenDuAn> findByDuAnAndNguoiDung(DuAn duAn, NguoiDung nguoiDung);
}