package com.iot.management.model.repository;

import com.iot.management.model.entity.PhanQuyenThietBi;
import com.iot.management.model.enums.DuAnRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhanQuyenThietBiRepository extends JpaRepository<PhanQuyenThietBi, Long> {
    
    @Query("SELECT pq FROM PhanQuyenThietBi pq WHERE pq.thietBi.maThietBi = :maThietBi")
    List<PhanQuyenThietBi> findByMaThietBi(@Param("maThietBi") Long maThietBi);
    
    @Query("SELECT pq FROM PhanQuyenThietBi pq WHERE pq.nguoiDung.maNguoiDung = :maNguoiDung")
    List<PhanQuyenThietBi> findByMaNguoiDung(@Param("maNguoiDung") Long maNguoiDung);
    
    @Query("SELECT pq FROM PhanQuyenThietBi pq WHERE pq.thietBi.maThietBi = :maThietBi " +
           "AND pq.nguoiDung.maNguoiDung = :maNguoiDung")
    Optional<PhanQuyenThietBi> findByMaThietBiAndMaNguoiDung(
            @Param("maThietBi") Long maThietBi, 
            @Param("maNguoiDung") Long maNguoiDung);
    
    @Query("SELECT pq FROM PhanQuyenThietBi pq WHERE pq.thietBi.maThietBi = :maThietBi " +
           "AND pq.nguoiDung.maNguoiDung = :maNguoiDung AND pq.vaiTro = :vaiTro")
    Optional<PhanQuyenThietBi> findByMaThietBiAndMaNguoiDungAndVaiTro(
            @Param("maThietBi") Long maThietBi, 
            @Param("maNguoiDung") Long maNguoiDung,
            @Param("vaiTro") DuAnRole vaiTro);
    
    @Query("DELETE FROM PhanQuyenThietBi pq WHERE pq.thietBi.maThietBi = :maThietBi " +
           "AND pq.nguoiDung.maNguoiDung = :maNguoiDung")
    void deleteByMaThietBiAndMaNguoiDung(
            @Param("maThietBi") Long maThietBi, 
            @Param("maNguoiDung") Long maNguoiDung);
    
    @Query("SELECT pq FROM PhanQuyenThietBi pq " +
           "WHERE pq.nguoiDung.maNguoiDung = :maNguoiDung " +
           "AND pq.thietBi.khuVuc.duAn.maDuAn = :maDuAn")
    List<PhanQuyenThietBi> findByMaNguoiDungAndMaDuAn(
            @Param("maNguoiDung") Long maNguoiDung,
            @Param("maDuAn") Long maDuAn);
}
