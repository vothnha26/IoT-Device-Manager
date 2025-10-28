package com.iot.management.model.repository;

import com.iot.management.model.entity.PhanQuyenKhuVuc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhanQuyenKhuVucRepository extends JpaRepository<PhanQuyenKhuVuc, Long> {
    
    @Query("SELECT pq FROM PhanQuyenKhuVuc pq WHERE pq.khuVuc.maKhuVuc = :maKhuVuc")
    List<PhanQuyenKhuVuc> findByMaKhuVuc(@Param("maKhuVuc") Long maKhuVuc);
    
    @Query("SELECT pq FROM PhanQuyenKhuVuc pq WHERE pq.nguoiDung.maNguoiDung = :maNguoiDung")
    List<PhanQuyenKhuVuc> findByMaNguoiDung(@Param("maNguoiDung") Long maNguoiDung);
    
    @Query("SELECT pq FROM PhanQuyenKhuVuc pq WHERE pq.khuVuc.maKhuVuc = :maKhuVuc " +
           "AND pq.nguoiDung.maNguoiDung = :maNguoiDung")
    Optional<PhanQuyenKhuVuc> findByMaKhuVucAndMaNguoiDung(
            @Param("maKhuVuc") Long maKhuVuc, 
            @Param("maNguoiDung") Long maNguoiDung);
    
    @Query("SELECT pq FROM PhanQuyenKhuVuc pq WHERE pq.khuVuc.maKhuVuc = :maKhuVuc " +
           "AND pq.nguoiDung.maNguoiDung = :maNguoiDung AND pq.vaiTro = :vaiTro")
    Optional<PhanQuyenKhuVuc> findByMaKhuVucAndMaNguoiDungAndVaiTro(
            @Param("maKhuVuc") Long maKhuVuc, 
            @Param("maNguoiDung") Long maNguoiDung,
            @Param("vaiTro") String vaiTro);
    
    @Query("DELETE FROM PhanQuyenKhuVuc pq WHERE pq.khuVuc.maKhuVuc = :maKhuVuc " +
           "AND pq.nguoiDung.maNguoiDung = :maNguoiDung")
    void deleteByMaKhuVucAndMaNguoiDung(
            @Param("maKhuVuc") Long maKhuVuc, 
            @Param("maNguoiDung") Long maNguoiDung);
    
    /**
     * Tìm phân quyền theo entity KhuVuc và NguoiDung
     */
    Optional<PhanQuyenKhuVuc> findByKhuVucAndNguoiDung(
            com.iot.management.model.entity.KhuVuc khuVuc,
            com.iot.management.model.entity.NguoiDung nguoiDung);
    
    @Query("SELECT pq FROM PhanQuyenKhuVuc pq " +
           "WHERE pq.nguoiDung.maNguoiDung = :maNguoiDung " +
           "AND pq.khuVuc.duAn.maDuAn = :maDuAn")
    List<PhanQuyenKhuVuc> findByMaNguoiDungAndMaDuAn(
            @Param("maNguoiDung") Long maNguoiDung,
            @Param("maDuAn") Long maDuAn);
}
