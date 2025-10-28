package com.iot.management.model.dto.backup;

import java.time.LocalDateTime;
import java.util.List;

public class DuAnBackupDTO {
    private String tenDuAn;
    private String moTa;
    private LocalDateTime ngayTao;
    private List<KhuVucBackupDTO> khuVucs;
    private List<ThietBiBackupDTO> thietBis;
    private List<LuatBackupDTO> luats;

    // Constructors
    public DuAnBackupDTO() {}

    // Getters and Setters
    public String getTenDuAn() { return tenDuAn; }
    public void setTenDuAn(String tenDuAn) { this.tenDuAn = tenDuAn; }

    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }

    public LocalDateTime getNgayTao() { return ngayTao; }
    public void setNgayTao(LocalDateTime ngayTao) { this.ngayTao = ngayTao; }

    public List<KhuVucBackupDTO> getKhuVucs() { return khuVucs; }
    public void setKhuVucs(List<KhuVucBackupDTO> khuVucs) { this.khuVucs = khuVucs; }

    public List<ThietBiBackupDTO> getThietBis() { return thietBis; }
    public void setThietBis(List<ThietBiBackupDTO> thietBis) { this.thietBis = thietBis; }

    public List<LuatBackupDTO> getLuats() { return luats; }
    public void setLuats(List<LuatBackupDTO> luats) { this.luats = luats; }
}
