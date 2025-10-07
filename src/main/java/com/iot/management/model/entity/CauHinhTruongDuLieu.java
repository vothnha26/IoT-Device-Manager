package com.iot.management.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "CauHinhTruongDuLieu", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"ma_loai_thiet_bi", "ten_truong"})
})
public class CauHinhTruongDuLieu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_cau_hinh_truong")
    private Long maCauHinhTruong;

    @Column(name = "ma_loai_thiet_bi")
    private Long maLoaiThietBi;

    @Column(name = "ten_truong", length = 150)
    private String tenTruong;

    @Column(name = "ten_hien_thi", length = 150)
    private String tenHienThi;

    @Column(name = "kieu_du_lieu", length = 50)
    private String kieuDuLieu;

    @Column(name = "don_vi", length = 50)
    private String donVi;

    @Column(name = "gia_tri_min")
    private Double giaTriMin;

    @Column(name = "gia_tri_max")
    private Double giaTriMax;

    @Column(name = "dinh_nghia_enum", length = 1000)
    private String dinhNghiaEnum;

    @Column(name = "ghi_chu", length = 500)
    private String ghiChu;

    // Getters/Setters
    public Long getMaCauHinhTruong() { return maCauHinhTruong; }
    public void setMaCauHinhTruong(Long maCauHinhTruong) { this.maCauHinhTruong = maCauHinhTruong; }

    public Long getMaLoaiThietBi() { return maLoaiThietBi; }
    public void setMaLoaiThietBi(Long maLoaiThietBi) { this.maLoaiThietBi = maLoaiThietBi; }

    public String getTenTruong() { return tenTruong; }
    public void setTenTruong(String tenTruong) { this.tenTruong = tenTruong; }

    public String getTenHienThi() { return tenHienThi; }
    public void setTenHienThi(String tenHienThi) { this.tenHienThi = tenHienThi; }

    public String getKieuDuLieu() { return kieuDuLieu; }
    public void setKieuDuLieu(String kieuDuLieu) { this.kieuDuLieu = kieuDuLieu; }

    public String getDonVi() { return donVi; }
    public void setDonVi(String donVi) { this.donVi = donVi; }

    public Double getGiaTriMin() { return giaTriMin; }
    public void setGiaTriMin(Double giaTriMin) { this.giaTriMin = giaTriMin; }

    public Double getGiaTriMax() { return giaTriMax; }
    public void setGiaTriMax(Double giaTriMax) { this.giaTriMax = giaTriMax; }

    public String getDinhNghiaEnum() { return dinhNghiaEnum; }
    public void setDinhNghiaEnum(String dinhNghiaEnum) { this.dinhNghiaEnum = dinhNghiaEnum; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
}
