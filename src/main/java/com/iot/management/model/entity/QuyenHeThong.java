package com.iot.management.model.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "quyen_he_thong")
public class QuyenHeThong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_quyen")
    private Long maQuyen;

    @Column(name = "ma_nhom", length = 50)
    private String maNhom;

    @Column(name = "ten_quyen", nullable = false, length = 100)
    private String tenQuyen;

    @Column(name = "mo_ta", length = 255)
    private String moTa;

    @OneToMany(mappedBy = "quyenHeThong", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<VaiTroQuyen> vaiTroQuyens = new HashSet<>();

    // Constructors
    public QuyenHeThong() {
    }

    public QuyenHeThong(String maNhom, String tenQuyen, String moTa) {
        this.maNhom = maNhom;
        this.tenQuyen = tenQuyen;
        this.moTa = moTa;
    }

    // Getters and Setters
    public Long getMaQuyen() {
        return maQuyen;
    }

    public void setMaQuyen(Long maQuyen) {
        this.maQuyen = maQuyen;
    }

    public String getMaNhom() {
        return maNhom;
    }

    public void setMaNhom(String maNhom) {
        this.maNhom = maNhom;
    }

    public String getTenQuyen() {
        return tenQuyen;
    }

    public void setTenQuyen(String tenQuyen) {
        this.tenQuyen = tenQuyen;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public Set<VaiTroQuyen> getVaiTroQuyens() {
        return vaiTroQuyens;
    }

    public void setVaiTroQuyens(Set<VaiTroQuyen> vaiTroQuyens) {
        this.vaiTroQuyens = vaiTroQuyens;
    }
}
