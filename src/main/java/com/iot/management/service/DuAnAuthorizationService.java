package com.iot.management.service;

import com.iot.management.model.enums.DuAnRole;

/**
 * Service kiểm tra quyền cụ thể theo vai trò trong dự án
 */
public interface DuAnAuthorizationService {
    
    // ==================== Quyền quản lý dự án ====================
    
    /**
     * Kiểm tra quyền xem dự án
     */
    boolean coQuyenXemDuAn(Long maDuAn, Long maNguoiDung);
    
    /**
     * Kiểm tra quyền chỉnh sửa dự án
     */
    boolean coQuyenChinhSuaDuAn(Long maDuAn, Long maNguoiDung);
    
    /**
     * Kiểm tra quyền xóa dự án (chỉ CHU_SO_HUU)
     */
    boolean coQuyenXoaDuAn(Long maDuAn, Long maNguoiDung);
    
    // ==================== Quyền quản lý khu vực ====================
    
    /**
     * Kiểm tra quyền thêm khu vực (CHU_SO_HUU)
     */
    boolean coQuyenThemKhuVuc(Long maDuAn, Long maNguoiDung);
    
    /**
     * Kiểm tra quyền xem khu vực
     */
    boolean coQuyenXemKhuVuc(Long maDuAn, Long maNguoiDung);
    
    /**
     * Kiểm tra quyền chỉnh sửa khu vực (CHU_SO_HUU, QUAN_LY)
     */
    boolean coQuyenChinhSuaKhuVuc(Long maDuAn, Long maNguoiDung);
    
    /**
     * Kiểm tra quyền xóa khu vực (CHU_SO_HUU)
     */
    boolean coQuyenXoaKhuVuc(Long maDuAn, Long maNguoiDung);
    
    // ==================== Quyền quản lý thiết bị ====================
    
    /**
     * Kiểm tra quyền thêm thiết bị (CHU_SO_HUU, QUAN_LY)
     */
    boolean coQuyenThemThietBi(Long maDuAn, Long maNguoiDung);
    
    /**
     * Kiểm tra quyền xem thiết bị
     */
    boolean coQuyenXemThietBi(Long maDuAn, Long maNguoiDung);
    
    /**
     * Kiểm tra quyền chỉnh sửa thiết bị (CHU_SO_HUU, QUAN_LY)
     */
    boolean coQuyenChinhSuaThietBi(Long maDuAn, Long maNguoiDung);
    
    /**
     * Kiểm tra quyền xóa thiết bị (CHU_SO_HUU, QUAN_LY)
     */
    boolean coQuyenXoaThietBi(Long maDuAn, Long maNguoiDung);
    
    /**
     * Kiểm tra quyền điều khiển thiết bị (tùy thuộc vào quyền thiết bị cụ thể)
     */
    boolean coQuyenDieuKhienThietBi(Long maThietBi, Long maNguoiDung);
    
    /**
     * Kiểm tra quyền xem dữ liệu cảm biến
     */
    boolean coQuyenXemDuLieuCamBien(Long maDuAn, Long maNguoiDung);
    
    // ==================== Quyền quản lý luật và lịch trình ====================
    
    /**
     * Kiểm tra quyền quản lý luật ngưỡng (CHU_SO_HUU, QUAN_LY)
     */
    boolean coQuyenQuanLyLuat(Long maDuAn, Long maNguoiDung);
    
    /**
     * Kiểm tra quyền quản lý lịch trình (CHU_SO_HUU, QUAN_LY)
     */
    boolean coQuyenQuanLyLichTrinh(Long maDuAn, Long maNguoiDung);
    
    /**
     * Kiểm tra quyền quản lý cảnh báo (CHU_SO_HUU, QUAN_LY)
     */
    boolean coQuyenQuanLyCanhBao(Long maDuAn, Long maNguoiDung);
    
    // ==================== Quyền phân quyền ====================
    
    /**
     * Kiểm tra quyền cấp/thu hồi quyền dự án (chỉ CHU_SO_HUU)
     */
    boolean coQuyenCapQuyenDuAn(Long maDuAn, Long maNguoiDung);
    
    /**
     * Kiểm tra quyền cấp quyền thiết bị (CHU_SO_HUU, QUAN_LY)
     */
    boolean coQuyenCapQuyenThietBi(Long maDuAn, Long maNguoiDung);
    
    /**
     * Kiểm tra quyền cấp quyền khu vực (chỉ CHU_SO_HUU)
     */
    boolean coQuyenCapQuyenKhuVuc(Long maDuAn, Long maNguoiDung);
    
    // ==================== Quyền báo cáo và log ====================
    
    /**
     * Kiểm tra quyền xem báo cáo thống kê
     */
    boolean coQuyenXemBaoCao(Long maDuAn, Long maNguoiDung);
    
    /**
     * Kiểm tra quyền xem log (CHU_SO_HUU, QUAN_LY)
     */
    boolean coQuyenXemLog(Long maDuAn, Long maNguoiDung);
    
    /**
     * Kiểm tra quyền xóa log (chỉ CHU_SO_HUU)
     */
    boolean coQuyenXoaLog(Long maDuAn, Long maNguoiDung);
    
    // ==================== Utility methods ====================
    
    /**
     * Lấy vai trò của người dùng trong dự án
     */
    DuAnRole layVaiTroTrongDuAn(Long maDuAn, Long maNguoiDung);
    
    /**
     * Kiểm tra người dùng có phải chủ sở hữu dự án
     */
    boolean laChuSoHuu(Long maDuAn, Long maNguoiDung);
    
    /**
     * Kiểm tra người dùng có vai trò quản lý trở lên
     */
    boolean laQuanLyTroLen(Long maDuAn, Long maNguoiDung);
}
