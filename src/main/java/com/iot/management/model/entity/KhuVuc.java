package com.iot.management.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "KhuVuc")
public class KhuVuc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_khu_vuc")
    private Long maKhuVuc;

    @JsonBackReference("owner-regions")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_chu_so_huu", nullable = false)
    private NguoiDung chuSoHuu;

    @Column(name = "ten_khu_vuc", nullable = false, length = 100)
    private String tenKhuVuc;

    @Column(name = "loai_khu_vuc", length = 20)
    private String loaiKhuVuc;

    @JsonBackReference("parent-child")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_khu_vuc_cha")
    private KhuVuc khuVucCha;

    @JsonManagedReference("parent-child")
    @OneToMany(mappedBy = "khuVucCha")
    private Set<KhuVuc> khuVucCons;

    @JsonManagedReference("region-devices")
    @OneToMany(mappedBy = "khuVuc")
    private Set<ThietBi> thietBis;
    
    
    // Constructors
    public KhuVuc() {
    }

    // Getters and Setters
    public Long getMaKhuVuc() {
        return maKhuVuc;
    }

    public void setMaKhuVuc(Long maKhuVuc) {
        this.maKhuVuc = maKhuVuc;
    }

    public NguoiDung getChuSoHuu() {
        return chuSoHuu;
    }

    public void setChuSoHuu(NguoiDung chuSoHuu) {
        this.chuSoHuu = chuSoHuu;
    }

    public String getTenKhuVuc() {
        return tenKhuVuc;
    }

    public void setTenKhuVuc(String tenKhuVuc) {
        this.tenKhuVuc = tenKhuVuc;
    }

    public String getLoaiKhuVuc() {
        return loaiKhuVuc;
    }

    public void setLoaiKhuVuc(String loaiKhuVuc) {
        this.loaiKhuVuc = loaiKhuVuc;
    }

    public KhuVuc getKhuVucCha() {
        return khuVucCha;
    }

    public void setKhuVucCha(KhuVuc khuVucCha) {
        this.khuVucCha = khuVucCha;
    }

    public Set<KhuVuc> getKhuVucCons() {
        return khuVucCons;
    }

    public void setKhuVucCons(Set<KhuVuc> khuVucCons) {
        this.khuVucCons = khuVucCons;
    }

    public Set<ThietBi> getThietBis() {
        return thietBis;
    }

    public void setThietBis(Set<ThietBi> thietBis) {
        this.thietBis = thietBis;
    }
}