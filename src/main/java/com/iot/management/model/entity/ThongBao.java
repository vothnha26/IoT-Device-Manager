package com.iot.management.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ThongBao")
public class ThongBao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_thong_bao")
    private Long maThongBao;

    @JsonBackReference("user-notifications")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_nguoi_dung", nullable = false)
    private NguoiDung nguoiDung;

    @Column(name = "tieu_de", nullable = false, length = 255)
    private String tieuDe;

    @Column(name = "noi_dung", columnDefinition = "TEXT")
    private String noiDung;

    @Column(name = "loai_thong_bao", length = 50)
    private String loaiThongBao; // INFO, WARNING, ERROR, SUCCESS

    @Column(name = "da_doc", nullable = false)
    private Boolean daDoc = false;

    @Column(name = "thoi_gian_tao", nullable = false)
    private LocalDateTime thoiGianTao;

    @Column(name = "thoi_gian_doc")
    private LocalDateTime thoiGianDoc;

    // Liên kết đến thiết bị (nếu thông báo liên quan đến thiết bị)
    @JsonBackReference("device-notifications")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_thiet_bi")
    private ThietBi thietBi;

    // Liên kết đến khu vực (nếu thông báo liên quan đến khu vực)
    @JsonBackReference("area-notifications")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_khu_vuc")
    private KhuVuc khuVuc;

    @Column(name = "url_lien_ket", length = 500)
    private String urlLienKet; // Link đến trang chi tiết nếu có

    @PrePersist
    protected void onCreate() {
        if (thoiGianTao == null) {
            thoiGianTao = LocalDateTime.now();
        }
        if (daDoc == null) {
            daDoc = false;
        }
    }

    // Constructors
    public ThongBao() {
    }

    public ThongBao(NguoiDung nguoiDung, String tieuDe, String noiDung, String loaiThongBao) {
        this.nguoiDung = nguoiDung;
        this.tieuDe = tieuDe;
        this.noiDung = noiDung;
        this.loaiThongBao = loaiThongBao;
        this.daDoc = false;
        this.thoiGianTao = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getMaThongBao() {
        return maThongBao;
    }

    public void setMaThongBao(Long maThongBao) {
        this.maThongBao = maThongBao;
    }

    public NguoiDung getNguoiDung() {
        return nguoiDung;
    }

    public void setNguoiDung(NguoiDung nguoiDung) {
        this.nguoiDung = nguoiDung;
    }

    public String getTieuDe() {
        return tieuDe;
    }

    public void setTieuDe(String tieuDe) {
        this.tieuDe = tieuDe;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }

    public String getLoaiThongBao() {
        return loaiThongBao;
    }

    public void setLoaiThongBao(String loaiThongBao) {
        this.loaiThongBao = loaiThongBao;
    }

    public Boolean getDaDoc() {
        return daDoc;
    }

    public void setDaDoc(Boolean daDoc) {
        this.daDoc = daDoc;
        if (daDoc && thoiGianDoc == null) {
            this.thoiGianDoc = LocalDateTime.now();
        }
    }

    public LocalDateTime getThoiGianTao() {
        return thoiGianTao;
    }

    public void setThoiGianTao(LocalDateTime thoiGianTao) {
        this.thoiGianTao = thoiGianTao;
    }

    public LocalDateTime getThoiGianDoc() {
        return thoiGianDoc;
    }

    public void setThoiGianDoc(LocalDateTime thoiGianDoc) {
        this.thoiGianDoc = thoiGianDoc;
    }

    public ThietBi getThietBi() {
        return thietBi;
    }

    public void setThietBi(ThietBi thietBi) {
        this.thietBi = thietBi;
    }

    public KhuVuc getKhuVuc() {
        return khuVuc;
    }

    public void setKhuVuc(KhuVuc khuVuc) {
        this.khuVuc = khuVuc;
    }

    public String getUrlLienKet() {
        return urlLienKet;
    }

    public void setUrlLienKet(String urlLienKet) {
        this.urlLienKet = urlLienKet;
    }
}
