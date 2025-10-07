package com.iot.management.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "LuatNguong")
public class LuatNguong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_luat")
    private Long maLuat;

    @Column(name = "ma_thiet_bi")
    private Long maThietBi;

    @Column(name = "ten_truong", length = 150)
    private String tenTruong;

    @Column(name = "phep_toan", length = 10)
    private String phepToan;

    @Column(name = "gia_tri_nguong")
    private Double giaTriNguong;

    @Column(name = "lenh_hanh_dong", length = 1000)
    private String lenhHanhDong;

    @Column(name = "kich_hoat")
    private Boolean kichHoat;

    // Getters/Setters
    public Long getMaLuat() { return maLuat; }
    public void setMaLuat(Long maLuat) { this.maLuat = maLuat; }

    public Long getMaThietBi() { return maThietBi; }
    public void setMaThietBi(Long maThietBi) { this.maThietBi = maThietBi; }

    public String getTenTruong() { return tenTruong; }
    public void setTenTruong(String tenTruong) { this.tenTruong = tenTruong; }

    public String getPhepToan() { return phepToan; }
    public void setPhepToan(String phepToan) { this.phepToan = phepToan; }

    public Double getGiaTriNguong() { return giaTriNguong; }
    public void setGiaTriNguong(Double giaTriNguong) { this.giaTriNguong = giaTriNguong; }

    public String getLenhHanhDong() { return lenhHanhDong; }
    public void setLenhHanhDong(String lenhHanhDong) { this.lenhHanhDong = lenhHanhDong; }

    public Boolean getKichHoat() { return kichHoat; }
    public void setKichHoat(Boolean kichHoat) { this.kichHoat = kichHoat; }
}
