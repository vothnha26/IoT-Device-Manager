package com.iot.management.service;

public interface KhuVucAuthorizationService {
    
    /**
     * Lấy vai trò của người dùng trong khu vực
     * @return "QUAN_LY_KHU_VUC", "XEM_KHU_VUC", hoặc null nếu không có quyền
     */
    String layVaiTroTrongKhuVuc(Long maKhuVuc, Long maNguoiDung);
    
    /**
     * Kiểm tra người dùng có phải quản lý khu vực không
     */
    boolean laQuanLyKhuVuc(Long maKhuVuc, Long maNguoiDung);
    
    /**
     * Kiểm tra người dùng có quyền xem khu vực không (bao gồm cả QUAN_LY và XEM)
     */
    boolean coQuyenXemKhuVuc(Long maKhuVuc, Long maNguoiDung);
    
    // ==================== QUYỀN XEM THÔNG TIN ====================
    
    /**
     * Xem thông tin khu vực
     * QUAN_LY_KHU_VUC: Có
     * XEM_KHU_VUC: Có
     */
    boolean coQuyenXemThongTinKhuVuc(Long maKhuVuc, Long maNguoiDung);
    
    /**
     * Xem danh sách thiết bị trong khu vực
     * QUAN_LY_KHU_VUC: Có
     * XEM_KHU_VUC: Có
     */
    boolean coQuyenXemDanhSachThietBi(Long maKhuVuc, Long maNguoiDung);
    
    /**
     * Xem dữ liệu cảm biến và trạng thái
     * QUAN_LY_KHU_VUC: Có
     * XEM_KHU_VUC: Có
     */
    boolean coQuyenXemDuLieuCamBien(Long maKhuVuc, Long maNguoiDung);
    
    /**
     * Xem cảnh báo của khu vực
     * QUAN_LY_KHU_VUC: Có
     * XEM_KHU_VUC: Có
     */
    boolean coQuyenXemCanhBao(Long maKhuVuc, Long maNguoiDung);
    
    /**
     * Xem nhật ký khu vực
     * QUAN_LY_KHU_VUC: Có
     * XEM_KHU_VUC: Có
     */
    boolean coQuyenXemNhatKy(Long maKhuVuc, Long maNguoiDung);
    
    // ==================== QUYỀN CHỈNH SỬA ====================
    
    /**
     * Chỉnh sửa thông tin khu vực
     * QUAN_LY_KHU_VUC: Có
     * XEM_KHU_VUC: Không
     */
    boolean coQuyenChinhSuaThongTinKhuVuc(Long maKhuVuc, Long maNguoiDung);
    
    // ==================== QUYỀN QUẢN LÝ THIẾT BỊ ====================
    
    /**
     * Thêm thiết bị vào khu vực
     * QUAN_LY_KHU_VUC: Có
     * XEM_KHU_VUC: Không
     */
    boolean coQuyenThemThietBi(Long maKhuVuc, Long maNguoiDung);
    
    /**
     * Xóa thiết bị khỏi khu vực
     * QUAN_LY_KHU_VUC: Có
     * XEM_KHU_VUC: Không
     */
    boolean coQuyenXoaThietBi(Long maKhuVuc, Long maNguoiDung);
    
    /**
     * Điều khiển thiết bị
     * QUAN_LY_KHU_VUC: Có (toàn bộ thiết bị)
     * XEM_KHU_VUC: Không
     */
    boolean coQuyenDieuKhienThietBi(Long maKhuVuc, Long maNguoiDung);
    
    // ==================== QUYỀN PHÂN QUYỀN ====================
    
    /**
     * Cấp quyền thiết bị cho người khác
     * QUAN_LY_KHU_VUC: Có
     * XEM_KHU_VUC: Không
     */
    boolean coQuyenCapQuyenThietBi(Long maKhuVuc, Long maNguoiDung);
    
    /**
     * Cấp quyền khu vực cho người khác
     * QUAN_LY_KHU_VUC: Có
     * XEM_KHU_VUC: Không
     */
    boolean coQuyenCapQuyenKhuVuc(Long maKhuVuc, Long maNguoiDung);
    
    // ==================== KIỂM TRA QUYỀN ĐẶC BIỆT ====================
    
    /**
     * Kiểm tra người dùng có quyền quản lý khu vực thông qua vai trò dự án
     * (CHU_SO_HUU hoặc QUAN_LY của dự án tự động có quyền quản lý khu vực)
     */
    boolean coQuyenQuanLyThongQuaDuAn(Long maKhuVuc, Long maNguoiDung);
}
