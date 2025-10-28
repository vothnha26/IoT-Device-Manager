package com.iot.management.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "NhatKyHeThong")
public class NhatKyHeThong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_nhat_ky_ht")
    private Long maNhatKyHt;

    @Column(name = "ma_nguoi_dung")
    private Long maNguoiDung;

    @Column(name = "loai_hanh_dong", columnDefinition = "NVARCHAR(150)")
    private String loaiHanhDong;

    @Column(name = "chi_tiet", columnDefinition = "NVARCHAR(2000)")
    private String chiTiet;

    @Column(name = "thoi_gian")
    private LocalDateTime thoiGian;

    // Getters/Setters
    public Long getMaNhatKyHt() { return maNhatKyHt; }
    public void setMaNhatKyHt(Long maNhatKyHt) { this.maNhatKyHt = maNhatKyHt; }

    public Long getMaNguoiDung() { return maNguoiDung; }
    public void setMaNguoiDung(Long maNguoiDung) { this.maNguoiDung = maNguoiDung; }

    public String getLoaiHanhDong() { return loaiHanhDong; }
    public void setLoaiHanhDong(String loaiHanhDong) { this.loaiHanhDong = loaiHanhDong; }

    public String getChiTiet() { return chiTiet; }
    public void setChiTiet(String chiTiet) { this.chiTiet = chiTiet; }

    public LocalDateTime getThoiGian() { return thoiGian; }
    public void setThoiGian(LocalDateTime thoiGian) { this.thoiGian = thoiGian; }
}
