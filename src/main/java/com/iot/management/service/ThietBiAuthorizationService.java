package com.iot.management.service;

public interface ThietBiAuthorizationService {
    
    /**
     * Lấy quyền của người dùng đối với thiết bị
     * @return "MANAGE", "CONTROL", "VIEW", hoặc null nếu không có quyền
     */
    String layQuyenThietBi(Long maThietBi, Long maNguoiDung);
    
    /**
     * Kiểm tra người dùng có bất kỳ quyền nào đối với thiết bị không
     */
    boolean coQuyenTruyCapThietBi(Long maThietBi, Long maNguoiDung);
    
    // ==================== QUYỀN VIEW ====================
    
    /**
     * Xem thông tin thiết bị
     * VIEW: ✅ Có - Xem thông tin cơ bản
     * CONTROL: ✅ Có - Xem đầy đủ thông tin
     * MANAGE: ✅ Có - Xem đầy đủ thông tin
     */
    boolean coQuyenXemThongTin(Long maThietBi, Long maNguoiDung);
    
    /**
     * Xem dữ liệu cảm biến/trạng thái
     * VIEW: ✅ Có
     * CONTROL: ✅ Có
     * MANAGE: ✅ Có
     */
    boolean coQuyenXemDuLieu(Long maThietBi, Long maNguoiDung);
    
    /**
     * Xem lịch sử hoạt động
     * VIEW: ✅ Có
     * CONTROL: ✅ Có
     * MANAGE: ✅ Có
     */
    boolean coQuyenXemLichSu(Long maThietBi, Long maNguoiDung);
    
    // ==================== QUYỀN CONTROL ====================
    
    /**
     * Điều khiển thiết bị (bật/tắt, thay đổi trạng thái)
     * VIEW: ❌ Không
     * CONTROL: ✅ Có - Điều khiển cơ bản
     * MANAGE: ✅ Có - Điều khiển đầy đủ
     */
    boolean coQuyenDieuKhien(Long maThietBi, Long maNguoiDung);
    
    /**
     * Gửi lệnh điều khiển
     * VIEW: ❌ Không
     * CONTROL: ✅ Có
     * MANAGE: ✅ Có
     */
    boolean coQuyenGuiLenh(Long maThietBi, Long maNguoiDung);
    
    // ==================== QUYỀN MANAGE ====================
    
    /**
     * Chỉnh sửa thông tin thiết bị (tên, mô tả, vị trí)
     * VIEW: ❌ Không
     * CONTROL: ❌ Không
     * MANAGE: ✅ Có
     */
    boolean coQuyenChinhSuaThongTin(Long maThietBi, Long maNguoiDung);
    
    /**
     * Cấu hình thiết bị (thay đổi cài đặt, tham số)
     * VIEW: ❌ Không
     * CONTROL: ❌ Không
     * MANAGE: ✅ Có
     */
    boolean coQuyenCauHinh(Long maThietBi, Long maNguoiDung);
    
    /**
     * Xóa thiết bị
     * VIEW: ❌ Không
     * CONTROL: ❌ Không
     * MANAGE: ✅ Có
     */
    boolean coQuyenXoa(Long maThietBi, Long maNguoiDung);
    
    /**
     * Chia sẻ thiết bị (cấp quyền cho người khác)
     * VIEW: ❌ Không
     * CONTROL: ❌ Không
     * MANAGE: ✅ Có
     */
    boolean coQuyenChiaSe(Long maThietBi, Long maNguoiDung);
    
    /**
     * Xem danh sách người dùng có quyền truy cập
     * VIEW: ❌ Không
     * CONTROL: ❌ Không
     * MANAGE: ✅ Có
     */
    boolean coQuyenXemDanhSachNguoiDung(Long maThietBi, Long maNguoiDung);
    
    /**
     * Quản lý luật tự động hóa cho thiết bị
     * VIEW: ❌ Không
     * CONTROL: ❌ Không
     * MANAGE: ✅ Có
     */
    boolean coQuyenQuanLyLuat(Long maThietBi, Long maNguoiDung);
    
    /**
     * Quản lý lịch trình cho thiết bị
     * VIEW: ❌ Không
     * CONTROL: ❌ Không
     * MANAGE: ✅ Có
     */
    boolean coQuyenQuanLyLichTrinh(Long maThietBi, Long maNguoiDung);
    
    // ==================== QUYỀN ĐẶC BIỆT ====================
    
    /**
     * Kiểm tra người dùng có quyền quản lý thông qua khu vực
     * (Nếu có quyền QUAN_LY_KHU_VUC thì tự động có quyền MANAGE)
     */
    boolean coQuyenQuanLyThongQuaKhuVuc(Long maThietBi, Long maNguoiDung);
    
    /**
     * Kiểm tra người dùng có quyền quản lý thông qua dự án
     * (CHU_SO_HUU hoặc QUAN_LY của dự án tự động có quyền MANAGE)
     */
    boolean coQuyenQuanLyThongQuaDuAn(Long maThietBi, Long maNguoiDung);
}
