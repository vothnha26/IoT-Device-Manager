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

    @JsonBackReference("project-regions")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_du_an", nullable = false)
    private DuAn duAn;

    @JsonBackReference("owner-regions")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_chu_so_huu", nullable = false)
    private NguoiDung chuSoHuu;

    @Column(name = "ten_khu_vuc", nullable = false, columnDefinition = "NVARCHAR(100)")
    private String tenKhuVuc;

    @Column(name = "loai_khu_vuc", columnDefinition = "NVARCHAR(20)")
    private String loaiKhuVuc;

    @JsonManagedReference("region-devices")
    @OneToMany(mappedBy = "khuVuc")
    private Set<ThietBi> thietBis;

    @Column(name = "mo_ta", columnDefinition = "NVARCHAR(500)")
    private String moTa;
    
    
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

    public Set<ThietBi> getThietBis() {
        return thietBis;
    }

    public void setThietBis(Set<ThietBi> thietBis) {
        this.thietBis = thietBis;
    }

    public DuAn getDuAn() {
        return duAn;
    }

    public void setDuAn(DuAn duAn) {
        this.duAn = duAn;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }
}