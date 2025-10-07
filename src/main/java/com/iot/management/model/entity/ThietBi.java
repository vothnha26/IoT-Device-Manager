package com.iot.management.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ThietBi")
public class ThietBi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_thiet_bi")
    private Long maThietBi;

    @Column(name = "token_thiet_bi", length = 200)
    private String tokenThietBi;

    @Column(name = "ten_thiet_bi", length = 200)
    private String tenThietBi;

    @Column(name = "ma_loai_thiet_bi")
    private Long maLoaiThietBi;

    @Column(name = "trang_thai", length = 50)
    private String trangThai;

    @Column(name = "ma_chu_so_huu")
    private Long maChuSoHuu;

    @Column(name = "lan_hoat_dong_cuoi")
    private LocalDateTime lanHoatDongCuoi;

    @Column(name = "ngay_lap_dat")
    private LocalDateTime ngayLapDat;

    // Getters/Setters
    public Long getMaThietBi() { return maThietBi; }
    public void setMaThietBi(Long maThietBi) { this.maThietBi = maThietBi; }

    public String getTokenThietBi() { return tokenThietBi; }
    public void setTokenThietBi(String tokenThietBi) { this.tokenThietBi = tokenThietBi; }

    public String getTenThietBi() { return tenThietBi; }
    public void setTenThietBi(String tenThietBi) { this.tenThietBi = tenThietBi; }

    public Long getMaLoaiThietBi() { return maLoaiThietBi; }
    public void setMaLoaiThietBi(Long maLoaiThietBi) { this.maLoaiThietBi = maLoaiThietBi; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public Long getMaChuSoHuu() { return maChuSoHuu; }
    public void setMaChuSoHuu(Long maChuSoHuu) { this.maChuSoHuu = maChuSoHuu; }

    public LocalDateTime getLanHoatDongCuoi() { return lanHoatDongCuoi; }
    public void setLanHoatDongCuoi(LocalDateTime lanHoatDongCuoi) { this.lanHoatDongCuoi = lanHoatDongCuoi; }

    public LocalDateTime getNgayLapDat() { return ngayLapDat; }
    public void setNgayLapDat(LocalDateTime ngayLapDat) { this.ngayLapDat = ngayLapDat; }
}
