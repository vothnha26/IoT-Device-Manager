package com.iot.management.model.dto.backup;

import java.time.LocalDateTime;

public class KhuVucBackupDTO {
    private String tenKhuVuc;
    private String moTa;
    private String viTri;
    private LocalDateTime ngayTao;

    // Constructors
    public KhuVucBackupDTO() {}

    // Getters and Setters
    public String getTenKhuVuc() { return tenKhuVuc; }
    public void setTenKhuVuc(String tenKhuVuc) { this.tenKhuVuc = tenKhuVuc; }

    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }

    public String getViTri() { return viTri; }
    public void setViTri(String viTri) { this.viTri = viTri; }

    public LocalDateTime getNgayTao() { return ngayTao; }
    public void setNgayTao(LocalDateTime ngayTao) { this.ngayTao = ngayTao; }
}
