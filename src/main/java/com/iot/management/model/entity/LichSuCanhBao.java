package com.iot.management.model.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "LichSuCanhBao")
public class LichSuCanhBao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_canh_bao")
    private Long maCanhBao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_luat", nullable = false)
    private LuatNguong luat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_thiet_bi", nullable = false)
    private ThietBi thietBi;

    @Column(name = "noi_dung", columnDefinition = "NVARCHAR(255)")
    private String noiDung;

    @CreationTimestamp
    @Column(name = "thoi_gian", nullable = false, updatable = false)
    private LocalDateTime thoiGian;

    public LichSuCanhBao() {
    }

    public Long getMaCanhBao() {
        return maCanhBao;
    }

    public void setMaCanhBao(Long maCanhBao) {
        this.maCanhBao = maCanhBao;
    }

    public LuatNguong getLuat() {
        return luat;
    }

    public void setLuat(LuatNguong luat) {
        this.luat = luat;
    }

    public ThietBi getThietBi() {
        return thietBi;
    }

    public void setThietBi(ThietBi thietBi) {
        this.thietBi = thietBi;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }

    public LocalDateTime getThoiGian() {
        return thoiGian;
    }

    public void setThoiGian(LocalDateTime thoiGian) {
        this.thoiGian = thoiGian;
    }
}