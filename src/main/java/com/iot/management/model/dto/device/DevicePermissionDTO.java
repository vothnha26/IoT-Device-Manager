package com.iot.management.model.dto.device;

public class DevicePermissionDTO {
    private Long maThietBi;
    private boolean coQuyenXemDuLieu;
    private boolean coQuyenDieuKhien;
    
    public Long getMaThietBi() { 
        return maThietBi; 
    }
    
    public void setMaThietBi(Long maThietBi) { 
        this.maThietBi = maThietBi; 
    }
    
    public boolean isCoQuyenXemDuLieu() { 
        return coQuyenXemDuLieu; 
    }
    
    public void setCoQuyenXemDuLieu(boolean coQuyenXemDuLieu) { 
        this.coQuyenXemDuLieu = coQuyenXemDuLieu; 
    }
    
    public boolean isCoQuyenDieuKhien() { 
        return coQuyenDieuKhien; 
    }
    
    public void setCoQuyenDieuKhien(boolean coQuyenDieuKhien) { 
        this.coQuyenDieuKhien = coQuyenDieuKhien; 
    }
}
