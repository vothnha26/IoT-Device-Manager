package com.iot.management.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.HashSet;

@Entity
@Table(name = "DuAn")
public class DuAn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_du_an")
    private Long maDuAn;

    @Column(name = "ten_du_an", nullable = false, length = 100)
    private String tenDuAn;

    @Column(name = "mo_ta", length = 500)
    private String moTa;

    @Column(name = "dia_chi", length = 200)
    private String diaChi;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @Column(name = "trang_thai", length = 20)
    private String trangThai;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_nguoi_dung", nullable = false)
    private NguoiDung nguoiDung;

    @OneToMany(mappedBy = "duAn", cascade = CascadeType.ALL)
    private Set<KhuVuc> khuVucs;

    @OneToMany(mappedBy = "duAn", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PhanQuyenDuAn> phanQuyens = new HashSet<>();

    // Constructors
    public DuAn() {
        this.ngayTao = LocalDateTime.now();
        this.trangThai = "HOAT_DONG";
    }

    // Getters and Setters
    public Long getMaDuAn() {
        return maDuAn;
    }

    public void setMaDuAn(Long maDuAn) {
        this.maDuAn = maDuAn;
    }

    public String getTenDuAn() {
        return tenDuAn;
    }

    public void setTenDuAn(String tenDuAn) {
        this.tenDuAn = tenDuAn;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public LocalDateTime getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(LocalDateTime ngayTao) {
        this.ngayTao = ngayTao;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public NguoiDung getNguoiDung() {
        return nguoiDung;
    }

    public void setNguoiDung(NguoiDung nguoiDung) {
        this.nguoiDung = nguoiDung;
    }

    public Set<KhuVuc> getKhuVucs() {
        return khuVucs;
    }

    public void setKhuVucs(Set<KhuVuc> khuVucs) {
        this.khuVucs = khuVucs;
    }

    public Set<PhanQuyenDuAn> getPhanQuyens() {
        return phanQuyens;
    }

    public void setPhanQuyens(Set<PhanQuyenDuAn> phanQuyens) {
        this.phanQuyens = phanQuyens;
    }
}