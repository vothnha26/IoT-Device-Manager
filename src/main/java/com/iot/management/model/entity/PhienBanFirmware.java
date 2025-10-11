package com.iot.management.model.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "PhienBanFirmware", uniqueConstraints = {
    // Đảm bảo trong cùng một loại thiết bị, không có 2 phiên bản trùng tên
    @UniqueConstraint(columnNames = {"ma_loai_thiet_bi", "so_phien_ban"})
})
public class PhienBanFirmware {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_phien_ban")
    private Long maPhienBan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_loai_thiet_bi", nullable = false)
    private LoaiThietBi loaiThietBi;

    @Column(name = "so_phien_ban", nullable = false, length = 20)
    private String soPhienBan;

    @Column(name = "duong_dan_file", nullable = false, length = 512)
    private String duongDanFile;

    @Column(name = "hash_md5_file", length = 32)
    private String hashMd5File;
    
    @Column(name = "hash_sha256_file", length = 64)
    private String hashSha256File;

    @Column(name = "ghi_chu_phien_ban", length = 1000)
    private String ghiChuPhienBan;

    @CreationTimestamp
    @Column(name = "ngay_phat_hanh", nullable = false, updatable = false)
    private LocalDateTime ngayPhatHanh;

    // Constructors
    public PhienBanFirmware() {
    }

    // Getters and Setters
    public Long getMaPhienBan() {
        return maPhienBan;
    }

    public void setMaPhienBan(Long maPhienBan) {
        this.maPhienBan = maPhienBan;
    }

    public LoaiThietBi getLoaiThietBi() {
        return loaiThietBi;
    }

    public void setLoaiThietBi(LoaiThietBi loaiThietBi) {
        this.loaiThietBi = loaiThietBi;
    }

    public String getSoPhienBan() {
        return soPhienBan;
    }

    public void setSoPhienBan(String soPhienBan) {
        this.soPhienBan = soPhienBan;
    }

    public String getDuongDanFile() {
        return duongDanFile;
    }

    public void setDuongDanFile(String duongDanFile) {
        this.duongDanFile = duongDanFile;
    }

    public String getHashMd5File() {
        return hashMd5File;
    }

    public void setHashMd5File(String hashMd5File) {
        this.hashMd5File = hashMd5File;
    }

    public String getHashSha256File() {
        return hashSha256File;
    }

    public void setHashSha256File(String hashSha256File) {
        this.hashSha256File = hashSha256File;
    }
    
    public String getGhiChuPhienBan() {
        return ghiChuPhienBan;
    }

    public void setGhiChuPhienBan(String ghiChuPhienBan) {
        this.ghiChuPhienBan = ghiChuPhienBan;
    }

    public LocalDateTime getNgayPhatHanh() {
        return ngayPhatHanh;
    }

    public void setNgayPhatHanh(LocalDateTime ngayPhatHanh) {
        this.ngayPhatHanh = ngayPhatHanh;
    }
}