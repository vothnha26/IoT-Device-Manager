package com.iot.management.model.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "CauHinhTruongDuLieu", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"ma_loai_thiet_bi", "ten_truong"})
})
public class CauHinhTruongDuLieu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_cau_hinh_truong")
    private Long maCauHinhTruong;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_loai_thiet_bi", nullable = false)
    private LoaiThietBi loaiThietBi;

    @Column(name = "ten_truong", nullable = false, length = 50)
    private String tenTruong;

    @Column(name = "ten_hien_thi", length = 100)
    private String tenHienThi;

    @Column(name = "kieu_du_lieu", nullable = false, length = 20)
    private String kieuDuLieu;

    @Column(name = "don_vi", length = 20)
    private String donVi;

    @Column(name = "gia_tri_min", precision = 18, scale = 4)
    private BigDecimal giaTriMin;

    @Column(name = "gia_tri_max", precision = 18, scale = 4)
    private BigDecimal giaTriMax;

    @Column(name = "dinh_nghia_enum", length = 1000)
    private String dinhNghiaEnum;

    @Column(name = "ghi_chu", length = 255)
    private String ghiChu;
    
    public CauHinhTruongDuLieu() {
    }

    public Long getMaCauHinhTruong() {
        return maCauHinhTruong;
    }

    public void setMaCauHinhTruong(Long maCauHinhTruong) {
        this.maCauHinhTruong = maCauHinhTruong;
    }

    public LoaiThietBi getLoaiThietBi() {
        return loaiThietBi;
    }

    public void setLoaiThietBi(LoaiThietBi loaiThietBi) {
        this.loaiThietBi = loaiThietBi;
    }

    public String getTenTruong() {
        return tenTruong;
    }

    public void setTenTruong(String tenTruong) {
        this.tenTruong = tenTruong;
    }

    public String getTenHienThi() {
        return tenHienThi;
    }

    public void setTenHienThi(String tenHienThi) {
        this.tenHienThi = tenHienThi;
    }

    public String getKieuDuLieu() {
        return kieuDuLieu;
    }

    public void setKieuDuLieu(String kieuDuLieu) {
        this.kieuDuLieu = kieuDuLieu;
    }

    public String getDonVi() {
        return donVi;
    }

    public void setDonVi(String donVi) {
        this.donVi = donVi;
    }

    public BigDecimal getGiaTriMin() {
        return giaTriMin;
    }

    public void setGiaTriMin(BigDecimal giaTriMin) {
        this.giaTriMin = giaTriMin;
    }

    public BigDecimal getGiaTriMax() {
        return giaTriMax;
    }

    public void setGiaTriMax(BigDecimal giaTriMax) {
        this.giaTriMax = giaTriMax;
    }

    public String getDinhNghiaEnum() {
        return dinhNghiaEnum;
    }

    public void setDinhNghiaEnum(String dinhNghiaEnum) {
        this.dinhNghiaEnum = dinhNghiaEnum;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }
}