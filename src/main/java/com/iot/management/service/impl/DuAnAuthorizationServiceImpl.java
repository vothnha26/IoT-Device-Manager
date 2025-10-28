package com.iot.management.service.impl;

import com.iot.management.model.entity.DuAn;
import com.iot.management.model.entity.ThietBi;
import com.iot.management.model.enums.DuAnRole;
import com.iot.management.model.repository.DuAnRepository;
import com.iot.management.model.repository.ThietBiRepository;
import com.iot.management.service.DuAnAuthorizationService;
import com.iot.management.service.PhanQuyenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DuAnAuthorizationServiceImpl implements DuAnAuthorizationService {

    @Autowired
    private PhanQuyenService phanQuyenService;
    
    @Autowired
    private DuAnRepository duAnRepository;
    
    @Autowired
    private ThietBiRepository thietBiRepository;

    // ==================== Quyền quản lý dự án ====================

    @Override
    public boolean coQuyenXemDuAn(Long maDuAn, Long maNguoiDung) {
        // Tất cả các vai trò đều có quyền xem
        return layVaiTroTrongDuAn(maDuAn, maNguoiDung) != null;
    }

    @Override
    public boolean coQuyenChinhSuaDuAn(Long maDuAn, Long maNguoiDung) {
        // Chỉ CHU_SO_HUU mới có quyền chỉnh sửa dự án
        return laChuSoHuu(maDuAn, maNguoiDung);
    }

    @Override
    public boolean coQuyenXoaDuAn(Long maDuAn, Long maNguoiDung) {
        // Chỉ CHU_SO_HUU mới có quyền xóa dự án
        return laChuSoHuu(maDuAn, maNguoiDung);
    }

    // ==================== Quyền quản lý khu vực ====================

    @Override
    public boolean coQuyenThemKhuVuc(Long maDuAn, Long maNguoiDung) {
        // Chỉ CHU_SO_HUU mới có quyền thêm khu vực
        return laChuSoHuu(maDuAn, maNguoiDung);
    }

    @Override
    public boolean coQuyenXemKhuVuc(Long maDuAn, Long maNguoiDung) {
        // Tất cả các vai trò đều có quyền xem khu vực
        return layVaiTroTrongDuAn(maDuAn, maNguoiDung) != null;
    }

    @Override
    public boolean coQuyenChinhSuaKhuVuc(Long maDuAn, Long maNguoiDung) {
        // CHU_SO_HUU và QUAN_LY có quyền chỉnh sửa khu vực
        return laQuanLyTroLen(maDuAn, maNguoiDung);
    }

    @Override
    public boolean coQuyenXoaKhuVuc(Long maDuAn, Long maNguoiDung) {
        // Chỉ CHU_SO_HUU mới có quyền xóa khu vực
        return laChuSoHuu(maDuAn, maNguoiDung);
    }

    // ==================== Quyền quản lý thiết bị ====================

    @Override
    public boolean coQuyenThemThietBi(Long maDuAn, Long maNguoiDung) {
        // CHU_SO_HUU và QUAN_LY có quyền thêm thiết bị
        return laQuanLyTroLen(maDuAn, maNguoiDung);
    }

    @Override
    public boolean coQuyenXemThietBi(Long maDuAn, Long maNguoiDung) {
        // Tất cả các vai trò đều có quyền xem thiết bị
        return layVaiTroTrongDuAn(maDuAn, maNguoiDung) != null;
    }

    @Override
    public boolean coQuyenChinhSuaThietBi(Long maDuAn, Long maNguoiDung) {
        // CHU_SO_HUU và QUAN_LY có quyền chỉnh sửa thiết bị
        return laQuanLyTroLen(maDuAn, maNguoiDung);
    }

    @Override
    public boolean coQuyenXoaThietBi(Long maDuAn, Long maNguoiDung) {
        // CHU_SO_HUU và QUAN_LY có quyền xóa thiết bị
        return laQuanLyTroLen(maDuAn, maNguoiDung);
    }

    @Override
    public boolean coQuyenDieuKhienThietBi(Long maThietBi, Long maNguoiDung) {
        // Kiểm tra quyền điều khiển thiết bị cụ thể
        return phanQuyenService.kiemTraQuyenDieuKhienThietBi(maThietBi, maNguoiDung);
    }

    @Override
    public boolean coQuyenXemDuLieuCamBien(Long maDuAn, Long maNguoiDung) {
        // Tất cả các vai trò đều có quyền xem dữ liệu cảm biến
        return layVaiTroTrongDuAn(maDuAn, maNguoiDung) != null;
    }

    // ==================== Quyền quản lý luật và lịch trình ====================

    @Override
    public boolean coQuyenQuanLyLuat(Long maDuAn, Long maNguoiDung) {
        // CHU_SO_HUU và QUAN_LY có quyền quản lý luật
        return laQuanLyTroLen(maDuAn, maNguoiDung);
    }

    @Override
    public boolean coQuyenQuanLyLichTrinh(Long maDuAn, Long maNguoiDung) {
        // CHU_SO_HUU và QUAN_LY có quyền quản lý lịch trình
        return laQuanLyTroLen(maDuAn, maNguoiDung);
    }

    @Override
    public boolean coQuyenQuanLyCanhBao(Long maDuAn, Long maNguoiDung) {
        // CHU_SO_HUU và QUAN_LY có quyền quản lý cảnh báo
        return laQuanLyTroLen(maDuAn, maNguoiDung);
    }

    // ==================== Quyền phân quyền ====================

    @Override
    public boolean coQuyenCapQuyenDuAn(Long maDuAn, Long maNguoiDung) {
        // Chỉ CHU_SO_HUU mới có quyền cấp/thu hồi quyền dự án
        return laChuSoHuu(maDuAn, maNguoiDung);
    }

    @Override
    public boolean coQuyenCapQuyenThietBi(Long maDuAn, Long maNguoiDung) {
        // CHU_SO_HUU và QUAN_LY có quyền cấp quyền thiết bị
        return laQuanLyTroLen(maDuAn, maNguoiDung);
    }

    @Override
    public boolean coQuyenCapQuyenKhuVuc(Long maDuAn, Long maNguoiDung) {
        // Chỉ CHU_SO_HUU mới có quyền cấp quyền khu vực
        return laChuSoHuu(maDuAn, maNguoiDung);
    }

    // ==================== Quyền báo cáo và log ====================

    @Override
    public boolean coQuyenXemBaoCao(Long maDuAn, Long maNguoiDung) {
        // CHU_SO_HUU và QUAN_LY có quyền xem báo cáo
        return laQuanLyTroLen(maDuAn, maNguoiDung);
    }

    @Override
    public boolean coQuyenXemLog(Long maDuAn, Long maNguoiDung) {
        // CHU_SO_HUU và QUAN_LY có quyền xem log
        return laQuanLyTroLen(maDuAn, maNguoiDung);
    }

    @Override
    public boolean coQuyenXoaLog(Long maDuAn, Long maNguoiDung) {
        // Chỉ CHU_SO_HUU mới có quyền xóa log
        return laChuSoHuu(maDuAn, maNguoiDung);
    }

    // ==================== Utility methods ====================

    @Override
    public DuAnRole layVaiTroTrongDuAn(Long maDuAn, Long maNguoiDung) {
        return phanQuyenService.layVaiTroDuAn(maDuAn, maNguoiDung);
    }

    @Override
    public boolean laChuSoHuu(Long maDuAn, Long maNguoiDung) {
        DuAnRole vaiTro = layVaiTroTrongDuAn(maDuAn, maNguoiDung);
        return vaiTro == DuAnRole.CHU_SO_HUU;
    }

    @Override
    public boolean laQuanLyTroLen(Long maDuAn, Long maNguoiDung) {
        DuAnRole vaiTro = layVaiTroTrongDuAn(maDuAn, maNguoiDung);
        return vaiTro == DuAnRole.CHU_SO_HUU || vaiTro == DuAnRole.QUAN_LY;
    }
}
