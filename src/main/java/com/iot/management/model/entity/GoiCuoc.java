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

    @Column(name = "ten_goi", nullable = false, columnDefinition = "NVARCHAR(50)")
    private String tenGoi;

    @Column(name = "gia_tien", precision = 10, scale = 2)
    private BigDecimal giaTien;
    

    @Column(name = "sl_thiet_bi_toi_da")
    private Integer slThietBiToiDa;

    @Column(name = "sl_luat_toi_da")
    private Integer slLuatToiDa;

    @Column(name = "so_ngay_luu_du_lieu")
    private Integer soNgayLuuDuLieu;

    @Column(name = "sl_khu_vuc_toi_da", nullable = false)
    private int slKhuVucToiDa;

    @Column(name = "sl_token_toi_da", nullable = false)
    private int slTokenToiDa;

    @Column(name = "sl_nguoi_dung_toi_da")
    private Integer slNguoiDungToiDa;

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

    public int getSlKhuVucToiDa() {
        return slKhuVucToiDa;
    }

    public void setSlKhuVucToiDa(int slKhuVucToiDa) {
        this.slKhuVucToiDa = slKhuVucToiDa;
    }

    public int getSlTokenToiDa() {
        return slTokenToiDa;
    }

    public void setSlTokenToiDa(int slTokenToiDa) {
        this.slTokenToiDa = slTokenToiDa;
    }

    public Integer getSlNguoiDungToiDa() {
        return slNguoiDungToiDa;
    }

    public void setSlNguoiDungToiDa(Integer slNguoiDungToiDa) {
        this.slNguoiDungToiDa = slNguoiDungToiDa;
    }
}
