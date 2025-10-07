package com.iot.management.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "LoaiThietBi")
public class LoaiThietBi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_loai_thiet_bi")
    private Long maLoaiThietBi;

    @Column(name = "ten_loai", length = 150)
    private String tenLoai;

    @Column(name = "mo_ta", length = 500)
    private String moTa;

    // Getters/Setters
    public Long getMaLoaiThietBi() { return maLoaiThietBi; }
    public void setMaLoaiThietBi(Long maLoaiThietBi) { this.maLoaiThietBi = maLoaiThietBi; }

    public String getTenLoai() { return tenLoai; }
    public void setTenLoai(String tenLoai) { this.tenLoai = tenLoai; }

    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
}
