package com.iot.management.model.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ThanhToan")
public class ThanhToan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_thanh_toan")
    private Long maThanhToan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_dang_ky", nullable = false)
    private DangKyGoi dangKyGoi;

    @Column(name = "so_tien", nullable = false)
    private BigDecimal soTien;

    @CreationTimestamp
    @Column(name = "ngay_thanh_toan", nullable = false, updatable = false)
    private LocalDateTime ngayThanhToan;

    @Column(name = "phuong_thuc", length = 50)
    private String phuongThuc;

    @Column(name = "ma_giao_dich_cong_thanh_toan", length = 100)
    private String maGiaoDichCongThanhToan;

    @Column(name = "trang_thai", nullable = false, length = 20)
    private String trangThai;

    public ThanhToan() {
    }

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
}