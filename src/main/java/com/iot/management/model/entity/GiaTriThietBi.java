package com.iot.management.model.entity;
import jakarta.persistence.*;




@Entity
@Table(name = "GiaTriThietBi", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"thiet_bi_id", "cau_hinh_truong_id"})
})
public class GiaTriThietBi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "thiet_bi_id")
    private ThietBi thietBi;

    @ManyToOne
    @JoinColumn(name = "cau_hinh_truong_id")
    private CauHinhTruongDuLieu cauHinhTruongDuLieu;

    @Column(name = "gia_tri")
    private Double giaTri;

    @Column(name = "gia_tri_str")
    private String giaTriStr;

    public GiaTriThietBi() {}
    public GiaTriThietBi(ThietBi thietBi, CauHinhTruongDuLieu cauHinhTruongDuLieu) {
        this.thietBi = thietBi;
        this.cauHinhTruongDuLieu = cauHinhTruongDuLieu;
    }

    // getter / setter
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public ThietBi getThietBi() {
        return thietBi;
    }
    public void setThietBi(ThietBi thietBi) {
        this.thietBi = thietBi;
    }
    public CauHinhTruongDuLieu getCauHinhTruongDuLieu() {
        return cauHinhTruongDuLieu;
    }
    public void setCauHinhTruongDuLieu(CauHinhTruongDuLieu cauHinhTruongDuLieu) {
        this.cauHinhTruongDuLieu = cauHinhTruongDuLieu;
    }
    public Double getGiaTri() {
        return giaTri;
    }
    public void setGiaTri(Double giaTri) {
        this.giaTri = giaTri;
    }
    public String getGiaTriStr() {
        return giaTriStr;
    }
    public void setGiaTriStr(String giaTriStr) {
        this.giaTriStr = giaTriStr;
    }

}

