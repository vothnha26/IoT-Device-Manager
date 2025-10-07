package com.iot.management.model.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ThanhToan")
public class ThanhToan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_thanh_toan")
    private Long maThanhToan;

    @Column(name = "ma_dang_ky")
    private Long maDangKy;

    @Column(name = "so_tien")
    private BigDecimal soTien;

    @Column(name = "ngay_thanh_toan")
    private LocalDateTime ngayThanhToan;

    @Column(name = "phuong_thuc", length = 50)
    private String phuongThuc;

    @Column(name = "ma_giao_dich_cong_thanh_toan", length = 100)
    private String maGiaoDichCongThanhToan;

    @Column(name = "trang_thai", length = 20)
    private String trangThai;

    // Getters/Setters
    public Long getMaThanhToan() { return maThanhToan; }
    public void setMaThanhToan(Long maThanhToan) { this.maThanhToan = maThanhToan; }

    public Long getMaDangKy() { return maDangKy; }
    public void setMaDangKy(Long maDangKy) { this.maDangKy = maDangKy; }

    public BigDecimal getSoTien() { return soTien; }
    public void setSoTien(BigDecimal soTien) { this.soTien = soTien; }

    public LocalDateTime getNgayThanhToan() { return ngayThanhToan; }
    public void setNgayThanhToan(LocalDateTime ngayThanhToan) { this.ngayThanhToan = ngayThanhToan; }

    public String getPhuongThuc() { return phuongThuc; }
    public void setPhuongThuc(String phuongThuc) { this.phuongThuc = phuongThuc; }

    public String getMaGiaoDichCongThanhToan() { return maGiaoDichCongThanhToan; }
    public void setMaGiaoDichCongThanhToan(String maGiaoDichCongThanhToan) { this.maGiaoDichCongThanhToan = maGiaoDichCongThanhToan; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
}
