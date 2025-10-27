package com.iot.management.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "ThietBi")
public class ThietBi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_thiet_bi")
    private Long maThietBi;

    @Column(name = "token_thiet_bi", unique = true, nullable = false, length = 255)
    private String tokenThietBi;

    @Column(name = "ten_thiet_bi", nullable = false, length = 100)
    private String tenThietBi;

    @Column(name = "trang_thai", nullable = false, length = 20)
    private String trangThai;

    @Column(name = "lan_hoat_dong_cuoi")
    private LocalDateTime lanHoatDongCuoi;

    @Column(name = "ngay_lap_dat")
    private LocalDate ngayLapDat;

    @Column(name = "mo_ta", length = 500)
    private String moTa;

    @JsonBackReference("owner-devices")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_chu_so_huu", nullable = false)
    private NguoiDung chuSoHuu;

    @ManyToOne(fetch = FetchType.EAGER)  // Chuyển sang EAGER loading cho loaiThietBi
    @JoinColumn(name = "ma_loai_thiet_bi")
    private LoaiThietBi loaiThietBi;

    @JsonBackReference("region-devices")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_khu_vuc")
    private KhuVuc khuVuc;

    @JsonIgnore
    @OneToMany(mappedBy = "thietBi")
    private Set<NhatKyDuLieu> nhatKyDuLieus;

    @JsonIgnore
    @OneToMany(mappedBy = "thietBi", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<LichTrinh> lichTrinhs;

    public ThietBi() {
    }

    public Long getMaThietBi() {
        return maThietBi;
    }

    public void setMaThietBi(Long maThietBi) {
        this.maThietBi = maThietBi;
    }

    public String getTokenThietBi() {
        return tokenThietBi;
    }

    public void setTokenThietBi(String tokenThietBi) {
        this.tokenThietBi = tokenThietBi;
    }

    public String getTenThietBi() {
        return tenThietBi;
    }

    public void setTenThietBi(String tenThietBi) {
        this.tenThietBi = tenThietBi;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public LocalDateTime getLanHoatDongCuoi() {
        return lanHoatDongCuoi;
    }

    public void setLanHoatDongCuoi(LocalDateTime lanHoatDongCuoi) {
        this.lanHoatDongCuoi = lanHoatDongCuoi;
    }

    public LocalDate getNgayLapDat() {
        return ngayLapDat;
    }

    public void setNgayLapDat(LocalDate ngayLapDat) {
        this.ngayLapDat = ngayLapDat;
    }

    public NguoiDung getChuSoHuu() {
        return chuSoHuu;
    }

    public void setChuSoHuu(NguoiDung chuSoHuu) {
        this.chuSoHuu = chuSoHuu;
    }

    public LoaiThietBi getLoaiThietBi() {
        return loaiThietBi;
    }

    public void setLoaiThietBi(LoaiThietBi loaiThietBi) {
        this.loaiThietBi = loaiThietBi;
    }

    public KhuVuc getKhuVuc() {
        return khuVuc;
    }

    public void setKhuVuc(KhuVuc khuVuc) {
        this.khuVuc = khuVuc;
    }

    public Set<NhatKyDuLieu> getNhatKyDuLieus() {
        return nhatKyDuLieus;
    }

    public void setNhatKyDuLieus(Set<NhatKyDuLieu> nhatKyDuLieus) {
        this.nhatKyDuLieus = nhatKyDuLieus;
    }

    public Set<LichTrinh> getLichTrinhs() {
        return lichTrinhs;
    }

    public void setLichTrinhs(Set<LichTrinh> lichTrinhs) {
        this.lichTrinhs = lichTrinhs;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }
}