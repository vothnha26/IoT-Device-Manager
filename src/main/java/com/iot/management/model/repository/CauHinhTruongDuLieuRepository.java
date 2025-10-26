package com.iot.management.model.repository;

import com.iot.management.model.entity.CauHinhTruongDuLieu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CauHinhTruongDuLieuRepository extends JpaRepository<CauHinhTruongDuLieu, Long> {
    
    // Tìm tất cả các trường dữ liệu của một loại thiết bị
    List<CauHinhTruongDuLieu> findByLoaiThietBi_MaLoaiThietBi(Long maLoaiThietBi);
    
    // Tìm trường dữ liệu theo tên và mã loại thiết bị (ĐÃ SỬA LỖI TYPO)
    Optional<CauHinhTruongDuLieu> findByTenTruongAndLoaiThietBi_MaLoaiThietBi(String tenTruong, Long maLoaiThietBi);
    
    // Kiểm tra sự tồn tại của tên trường trong một loại thiết bị
    boolean existsByTenTruongAndLoaiThietBi_MaLoaiThietBi(String tenTruong, Long maLoaiThietBi);
    
    // Lấy danh sách trường dữ liệu theo thứ tự tạo (dùng ID thay cho ngayTao)
    List<CauHinhTruongDuLieu> findByLoaiThietBi_MaLoaiThietBiOrderByMaCauHinhTruongAsc(Long maLoaiThietBi);
    
    // Đếm số trường dữ liệu của một loại thiết bị (ĐÃ SỬA)
    long countByLoaiThietBi_MaLoaiThietBi(Long maLoaiThietBi);
    
    // Tìm kiếm trường dữ liệu theo tên (không phân biệt hoa thường)
    @Query("SELECT c FROM CauHinhTruongDuLieu c WHERE LOWER(c.tenTruong) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(c.tenHienThi) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<CauHinhTruongDuLieu> searchByKeyword(@Param("keyword") String keyword);
    
    // Lấy danh sách các trường dữ liệu theo kiểu dữ liệu
    List<CauHinhTruongDuLieu> findByKieuDuLieu(String kieuDuLieu);
    
    // Lấy các trường dữ liệu có giới hạn min/max
    @Query("SELECT c FROM CauHinhTruongDuLieu c WHERE c.giaTriMin IS NOT NULL AND c.giaTriMax IS NOT NULL")
    List<CauHinhTruongDuLieu> findAllWithLimits();
    
    // Xóa tất cả trường dữ liệu của một loại thiết bị (ĐÃ SỬA)
    void deleteByLoaiThietBi_MaLoaiThietBi(Long maLoaiThietBi);
    
    // Kiểm tra xem một trường dữ liệu có đang được sử dụng không (ĐÃ SỬA "c.id" thành "c.maCauHinhTruong")
    // dmin 
    
    // Tìm các trường dữ liệu có đơn vị đo tương tự
    List<CauHinhTruongDuLieu> findByDonViContainingIgnoreCase(String donVi);
    
    // Kiểm tra xem một loại thiết bị có trường dữ liệu nào không (ĐÃ SỬA "c.maLoaiThietBi" thành "c.loaiThietBi.maLoaiThietBi")
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM CauHinhTruongDuLieu c WHERE c.loaiThietBi.maLoaiThietBi = :maLoaiThietBi")
    boolean hasConfiguredFields(@Param("maLoaiThietBi") Long maLoaiThietBi);
    

    // === ĐÃ XÓA CÁC HÀM LIÊN QUAN ĐẾN NGÀY TẠO / NGÀY CẬP NHẬT ===
    
    // @Query("SELECT c FROM CauHinhTruongDuLieu c WHERE c.ngayTao BETWEEN :startDate AND :endDate")
    // List<CauHinhTruongDuLieu> findByCreatedDateBetween(@Param("startDate") java.time.LocalDateTime startDate, 
    //                                                     @Param("endDate") java.time.LocalDateTime endDate);
    
    // @Query("SELECT c FROM CauHinhTruongDuLieu c WHERE c.ngayCapNhat >= :lastUpdateDate")
    // List<CauHinhTruongDuLieu> findRecentlyUpdated(@Param("lastUpdateDate") java.time.LocalDateTime lastUpdateDate);
}