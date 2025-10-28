package com.iot.management.model.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ThanhToan")
public class ThanhToan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_thanh_toan")
    private Long maThanhToan;

    // HOÀN TÁC: Liên kết với DangKyGoi thay vì NguoiDung/GoiCuoc
    // Nullable vì khi tạo đơn hàng chưa có DangKyGoi, chỉ tạo khi thanh toán thành công
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_dang_ky", nullable = true)
    private DangKyGoi dangKyGoi;
    
    // Thông tin tạm để tạo DangKyGoi khi thanh toán thành công
    @Column(name = "ma_nguoi_dung")
    private Long maNguoiDung;
    
    @Column(name = "ma_goi_cuoc")
    private Long maGoiCuoc;

    @Column(name = "so_tien", nullable = false)
    private BigDecimal soTien;

    @Column(name = "ngay_thanh_toan")
    private LocalDateTime ngayThanhToan;

    @Column(name = "phuong_thuc", columnDefinition = "NVARCHAR(50)")
    private String phuongThuc;

    @Column(name = "ma_giao_dich_cong_thanh_toan", columnDefinition = "NVARCHAR(100)")
    private String maGiaoDichCongThanhToan;

    @Column(name = "trang_thai", nullable = false, columnDefinition = "NVARCHAR(20)")
    private String trangThai;

    // Getters and Setters
    public Long getMaThanhToan() {
        return maThanhToan;
    }

    public void setMaThanhToan(Long maThanhToan) {
        this.maThanhToan = maThanhToan;
    }

    public DangKyGoi getDangKyGoi() {
        return dangKyGoi;
    }

    public void setDangKyGoi(DangKyGoi dangKyGoi) {
        this.dangKyGoi = dangKyGoi;
    }

    public BigDecimal getSoTien() {
        return soTien;
    }

    public void setSoTien(BigDecimal soTien) {
        this.soTien = soTien;
    }

    public LocalDateTime getNgayThanhToan() {
        return ngayThanhToan;
    }

    public void setNgayThanhToan(LocalDateTime ngayThanhToan) {
        this.ngayThanhToan = ngayThanhToan;
    }

    public String getPhuongThuc() {
        return phuongThuc;
    }

    public void setPhuongThuc(String phuongThuc) {
        this.phuongThuc = phuongThuc;
    }

    public String getMaGiaoDichCongThanhToan() {
        return maGiaoDichCongThanhToan;
    }

    public void setMaGiaoDichCongThanhToan(String maGiaoDichCongThanhToan) {
        this.maGiaoDichCongThanhToan = maGiaoDichCongThanhToan;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public Long getMaNguoiDung() {
        return maNguoiDung;
    }

    public void setMaNguoiDung(Long maNguoiDung) {
        this.maNguoiDung = maNguoiDung;
    }

    public Long getMaGoiCuoc() {
        return maGoiCuoc;
    }

    public void setMaGoiCuoc(Long maGoiCuoc) {
        this.maGoiCuoc = maGoiCuoc;
    }
}