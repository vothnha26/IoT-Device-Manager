package com.iot.management.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "CauHinhHeThong")
public class CauHinhHeThong {

    @Id
    @Column(name = "ten_cau_hinh", columnDefinition = "NVARCHAR(200)")
    private String tenCauHinh;

    @Column(name = "gia_tri_cau_hinh", columnDefinition = "NVARCHAR(2000)")
    private String giaTriCauHinh;

    // Getters/Setters
    public String getTenCauHinh() { return tenCauHinh; }
    public void setTenCauHinh(String tenCauHinh) { this.tenCauHinh = tenCauHinh; }

    public String getGiaTriCauHinh() { return giaTriCauHinh; }
    public void setGiaTriCauHinh(String giaTriCauHinh) { this.giaTriCauHinh = giaTriCauHinh; }
}
