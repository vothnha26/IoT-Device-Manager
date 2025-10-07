package com.iot.management.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "LichSuCanhBao")
public class LichSuCanhBao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_canh_bao")
    private Long maCanhBao;

    @Column(name = "ma_luat")
    private Long maLuat;

    @Column(name = "ma_thiet_bi")
    private Long maThietBi;

    @Column(name = "noi_dung", length = 2000)
    private String noiDung;

    @Column(name = "thoi_gian")
    private LocalDateTime thoiGian;

    // Getters/Setters
    public Long getMaCanhBao() { return maCanhBao; }
    public void setMaCanhBao(Long maCanhBao) { this.maCanhBao = maCanhBao; }

    public Long getMaLuat() { return maLuat; }
    public void setMaLuat(Long maLuat) { this.maLuat = maLuat; }

    public Long getMaThietBi() { return maThietBi; }
    public void setMaThietBi(Long maThietBi) { this.maThietBi = maThietBi; }

    public String getNoiDung() { return noiDung; }
    public void setNoiDung(String noiDung) { this.noiDung = noiDung; }

    public LocalDateTime getThoiGian() { return thoiGian; }
    public void setThoiGian(LocalDateTime thoiGian) { this.thoiGian = thoiGian; }
}
