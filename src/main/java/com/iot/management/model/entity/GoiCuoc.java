package com.iot.management.model.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "GoiCuoc")
public class GoiCuoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_goi_cuoc")
    private Integer maGoiCuoc;

    @Column(name = "ten_goi", nullable = false, length = 50)
    private String tenGoi;

    @Column(name = "gia_tien", precision = 10, scale = 2)
    private BigDecimal giaTien;

    @Column(name = "sl_thiet_bi_toi_da")
    private Integer slThietBiToiDa;

    @Column(name = "sl_luat_toi_da")
    private Integer slLuatToiDa;

    @Column(name = "so_ngay_luu_du_lieu")
    private Integer soNgayLuuDuLieu;

    // Getters and setters
    public Integer getMaGoiCuoc() { return maGoiCuoc; }
    public void setMaGoiCuoc(Integer maGoiCuoc) { this.maGoiCuoc = maGoiCuoc; }

    public String getTenGoi() { return tenGoi; }
    public void setTenGoi(String tenGoi) { this.tenGoi = tenGoi; }

    public BigDecimal getGiaTien() { return giaTien; }
    public void setGiaTien(BigDecimal giaTien) { this.giaTien = giaTien; }

    public Integer getSlThietBiToiDa() { return slThietBiToiDa; }
    public void setSlThietBiToiDa(Integer slThietBiToiDa) { this.slThietBiToiDa = slThietBiToiDa; }

    public Integer getSlLuatToiDa() { return slLuatToiDa; }
    public void setSlLuatToiDa(Integer slLuatToiDa) { this.slLuatToiDa = slLuatToiDa; }

    public Integer getSoNgayLuuDuLieu() { return soNgayLuuDuLieu; }
    public void setSoNgayLuuDuLieu(Integer soNgayLuuDuLieu) { this.soNgayLuuDuLieu = soNgayLuuDuLieu; }
}
