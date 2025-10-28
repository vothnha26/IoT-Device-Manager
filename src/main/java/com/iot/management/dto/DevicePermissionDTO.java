package com.iot.management.dto;

public class DevicePermissionDTO {
    private Long maThietBi;
    private boolean coQuyenXemDuLieu;
    private boolean coQuyenChinhSua;
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
    
    public boolean isCoQuyenChinhSua() { 
        return coQuyenChinhSua; 
    }
    
    public void setCoQuyenChinhSua(boolean coQuyenChinhSua) { 
        this.coQuyenChinhSua = coQuyenChinhSua; 
    }
    
    public boolean isCoQuyenDieuKhien() { 
        return coQuyenDieuKhien; 
    }
    
    public void setCoQuyenDieuKhien(boolean coQuyenDieuKhien) { 
        this.coQuyenDieuKhien = coQuyenDieuKhien; 
    }
}
