package com.iot.management.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "DangKyGoi")
public class DangKyGoi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_dang_ky")
    private Long maDangKy;

    @Column(name = "ma_nguoi_dung")
    private Long maNguoiDung;

    @Column(name = "ma_goi_cuoc")
    private Integer maGoiCuoc;

    @Column(name = "ngay_bat_dau")
    private LocalDateTime ngayBatDau;

    @Column(name = "ngay_ket_thuc")
    private LocalDateTime ngayKetThuc;

    @Column(name = "trang_thai", length = 20)
    private String trangThai;

    // Getters/Setters
    public Long getMaDangKy() { return maDangKy; }
    public void setMaDangKy(Long maDangKy) { this.maDangKy = maDangKy; }

    public Long getMaNguoiDung() { return maNguoiDung; }
    public void setMaNguoiDung(Long maNguoiDung) { this.maNguoiDung = maNguoiDung; }

    public Integer getMaGoiCuoc() { return maGoiCuoc; }
    public void setMaGoiCuoc(Integer maGoiCuoc) { this.maGoiCuoc = maGoiCuoc; }

    public LocalDateTime getNgayBatDau() { return ngayBatDau; }
    public void setNgayBatDau(LocalDateTime ngayBatDau) { this.ngayBatDau = ngayBatDau; }

    public LocalDateTime getNgayKetThuc() { return ngayKetThuc; }
    public void setNgayKetThuc(LocalDateTime ngayKetThuc) { this.ngayKetThuc = ngayKetThuc; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
}
