package com.iot.management.service;

import com.iot.management.model.entity.PhanQuyenDuAn;
import com.iot.management.model.entity.PhanQuyenThietBi;
import com.iot.management.model.entity.PhanQuyenKhuVuc;
import com.iot.management.model.entity.QuyenHeThong;
import com.iot.management.model.entity.VaiTroQuyen;
import com.iot.management.model.dto.device.AreaPermissionDTO;
import com.iot.management.model.dto.device.DevicePermissionDTO;
import com.iot.management.model.entity.NguoiDung;
import com.iot.management.model.enums.DuAnRole;

import java.util.List;

public interface PhanQuyenService {
    
    // ==================== Phân quyền dự án ====================
    
    /**
     * Cấp quyền cho người dùng trong dự án
     */
    PhanQuyenDuAn capQuyenDuAn(Long maDuAn, Long maNguoiDung, DuAnRole vaiTro);
    
    /**
     * Thu hồi quyền người dùng trong dự án
     */
    void thuHoiQuyenDuAn(Long maDuAn, Long maNguoiDung);
    
    /**
     * Cập nhật vai trò người dùng trong dự án
     */
    PhanQuyenDuAn capNhatVaiTroDuAn(Long maDuAn, Long maNguoiDung, DuAnRole vaiTroMoi);
    
    /**
     * Lấy danh sách người dùng có quyền trong dự án
     */
    List<PhanQuyenDuAn> layDanhSachQuyenDuAn(Long maDuAn);
    
    /**
     * Kiểm tra quyền người dùng trong dự án
     */
    boolean kiemTraQuyenDuAn(Long maDuAn, Long maNguoiDung, DuAnRole vaiTro);
    
    /**
     * Lấy vai trò của người dùng trong dự án
     */
    DuAnRole layVaiTroDuAn(Long maDuAn, Long maNguoiDung);
    
    // ==================== Phân quyền thiết bị ====================
    
    /**
     * Cấp quyền cho người dùng với thiết bị
     */
    PhanQuyenThietBi capQuyenThietBi(Long maThietBi, Long maNguoiDung, DuAnRole vaiTro, 
                                      boolean coQuyenDieuKhien, boolean coQuyenXemDuLieu, 
                                      boolean coQuyenChinhSua);
    
    /**
     * Thu hồi quyền người dùng với thiết bị
     */
    void thuHoiQuyenThietBi(Long maThietBi, Long maNguoiDung);
    
    /**
     * Cập nhật quyền người dùng với thiết bị
     */
    PhanQuyenThietBi capNhatQuyenThietBi(Long maThietBi, Long maNguoiDung, 
                                          boolean coQuyenDieuKhien, boolean coQuyenXemDuLieu, 
                                          boolean coQuyenChinhSua);
    
    /**
     * Lấy danh sách người dùng có quyền với thiết bị
     */
    List<PhanQuyenThietBi> layDanhSachQuyenThietBi(Long maThietBi);
    
    /**
     * Kiểm tra quyền điều khiển thiết bị
     */
    boolean kiemTraQuyenDieuKhienThietBi(Long maThietBi, Long maNguoiDung);
    
    /**
     * Kiểm tra quyền xem dữ liệu thiết bị
     */
    boolean kiemTraQuyenXemDuLieuThietBi(Long maThietBi, Long maNguoiDung);
    
    /**
     * Kiểm tra quyền chỉnh sửa thiết bị
     */
    boolean kiemTraQuyenChinhSuaThietBi(Long maThietBi, Long maNguoiDung);
    
    // ==================== Quản lý quyền hệ thống ====================
    
    /**
     * Tạo quyền hệ thống mới
     */
    QuyenHeThong taoQuyenHeThong(String maNhom, String tenQuyen, String moTa);
    
    /**
     * Lấy danh sách tất cả quyền hệ thống
     */
    List<QuyenHeThong> layDanhSachQuyenHeThong();
    
    /**
     * Lấy danh sách quyền theo nhóm
     */
    List<QuyenHeThong> layQuyenTheoNhom(String maNhom);
    
    /**
     * Cấp quyền hệ thống cho vai trò
     */
    VaiTroQuyen capQuyenChoVaiTro(Long maVaiTro, Long maQuyen);
    
    /**
     * Thu hồi quyền hệ thống từ vai trò
     */
    void thuHoiQuyenTuVaiTro(Long maVaiTro, Long maQuyen);
    
    /**
     * Lấy danh sách quyền của vai trò
     */
    List<QuyenHeThong> layQuyenCuaVaiTro(Long maVaiTro);
    
    /**
     * Kiểm tra vai trò có quyền hệ thống
     */
    boolean kiemTraVaiTroCoQuyen(Long maVaiTro, String tenQuyen);
    
    // ==================== Phân quyền khu vực ====================
    
    /**
     * Cấp quyền cho người dùng với khu vực
     */
    PhanQuyenKhuVuc capQuyenKhuVuc(Long maKhuVuc, Long maNguoiDung, String vaiTro);
    
    /**
     * Thu hồi quyền người dùng với khu vực
     */
    void thuHoiQuyenKhuVuc(Long maKhuVuc, Long maNguoiDung);
    
    /**
     * Cập nhật vai trò người dùng với khu vực
     */
    PhanQuyenKhuVuc capNhatVaiTroKhuVuc(Long maKhuVuc, Long maNguoiDung, String vaiTroMoi);
    
    /**
     * Lấy danh sách người dùng có quyền với khu vực
     */
    List<PhanQuyenKhuVuc> layDanhSachQuyenKhuVuc(Long maKhuVuc);
    
    /**
     * Kiểm tra quyền quản lý khu vực
     */
    boolean kiemTraQuyenQuanLyKhuVuc(Long maKhuVuc, Long maNguoiDung);
    
    /**
     * Kiểm tra quyền xem khu vực
     */
    boolean kiemTraQuyenXemKhuVuc(Long maKhuVuc, Long maNguoiDung);
    
    /**
     * Lấy vai trò của người dùng với khu vực
     */
    String layVaiTroKhuVuc(Long maKhuVuc, Long maNguoiDung);
    
    /**
     * Cập nhật phân quyền chi tiết (khu vực và thiết bị) cho user trong dự án
     */
    void capNhatPhanQuyenChiTiet(Long userId, Long duAnId, 
                                   NguoiDung targetUser,
                                   List<AreaPermissionDTO> areaPermissions,
                                   List<DevicePermissionDTO> devicePermissions);
}
