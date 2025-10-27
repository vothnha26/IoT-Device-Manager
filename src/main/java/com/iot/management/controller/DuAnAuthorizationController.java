package com.iot.management.controller;

import com.iot.management.service.DuAnAuthorizationService;
import com.iot.management.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/du-an-authorization")
public class DuAnAuthorizationController {

    @Autowired
    private DuAnAuthorizationService authorizationService;

    /**
     * Kiểm tra tất cả quyền của người dùng trong dự án
     */
    @GetMapping("/{maDuAn}/kiem-tra-tat-ca")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> kiemTraTatCaQuyen(@PathVariable Long maDuAn) {
        Long maNguoiDung = SecurityUtils.getCurrentUserId();
        
        Map<String, Object> quyens = new HashMap<>();
        
        // Thông tin vai trò
        quyens.put("vaiTro", authorizationService.layVaiTroTrongDuAn(maDuAn, maNguoiDung));
        quyens.put("laChuSoHuu", authorizationService.laChuSoHuu(maDuAn, maNguoiDung));
        quyens.put("laQuanLyTroLen", authorizationService.laQuanLyTroLen(maDuAn, maNguoiDung));
        
        // Quyền dự án
        Map<String, Boolean> quyenDuAn = new HashMap<>();
        quyenDuAn.put("xem", authorizationService.coQuyenXemDuAn(maDuAn, maNguoiDung));
        quyenDuAn.put("chinhSua", authorizationService.coQuyenChinhSuaDuAn(maDuAn, maNguoiDung));
        quyenDuAn.put("xoa", authorizationService.coQuyenXoaDuAn(maDuAn, maNguoiDung));
        quyens.put("duAn", quyenDuAn);
        
        // Quyền khu vực
        Map<String, Boolean> quyenKhuVuc = new HashMap<>();
        quyenKhuVuc.put("them", authorizationService.coQuyenThemKhuVuc(maDuAn, maNguoiDung));
        quyenKhuVuc.put("xem", authorizationService.coQuyenXemKhuVuc(maDuAn, maNguoiDung));
        quyenKhuVuc.put("chinhSua", authorizationService.coQuyenChinhSuaKhuVuc(maDuAn, maNguoiDung));
        quyenKhuVuc.put("xoa", authorizationService.coQuyenXoaKhuVuc(maDuAn, maNguoiDung));
        quyens.put("khuVuc", quyenKhuVuc);
        
        // Quyền thiết bị
        Map<String, Boolean> quyenThietBi = new HashMap<>();
        quyenThietBi.put("them", authorizationService.coQuyenThemThietBi(maDuAn, maNguoiDung));
        quyenThietBi.put("xem", authorizationService.coQuyenXemThietBi(maDuAn, maNguoiDung));
        quyenThietBi.put("chinhSua", authorizationService.coQuyenChinhSuaThietBi(maDuAn, maNguoiDung));
        quyenThietBi.put("xoa", authorizationService.coQuyenXoaThietBi(maDuAn, maNguoiDung));
        quyenThietBi.put("xemDuLieu", authorizationService.coQuyenXemDuLieuCamBien(maDuAn, maNguoiDung));
        quyens.put("thietBi", quyenThietBi);
        
        // Quyền luật và lịch trình
        Map<String, Boolean> quyenTuDongHoa = new HashMap<>();
        quyenTuDongHoa.put("quanLyLuat", authorizationService.coQuyenQuanLyLuat(maDuAn, maNguoiDung));
        quyenTuDongHoa.put("quanLyLichTrinh", authorizationService.coQuyenQuanLyLichTrinh(maDuAn, maNguoiDung));
        quyenTuDongHoa.put("quanLyCanhBao", authorizationService.coQuyenQuanLyCanhBao(maDuAn, maNguoiDung));
        quyens.put("tuDongHoa", quyenTuDongHoa);
        
        // Quyền phân quyền
        Map<String, Boolean> quyenPhanQuyen = new HashMap<>();
        quyenPhanQuyen.put("capQuyenDuAn", authorizationService.coQuyenCapQuyenDuAn(maDuAn, maNguoiDung));
        quyenPhanQuyen.put("capQuyenThietBi", authorizationService.coQuyenCapQuyenThietBi(maDuAn, maNguoiDung));
        quyenPhanQuyen.put("capQuyenKhuVuc", authorizationService.coQuyenCapQuyenKhuVuc(maDuAn, maNguoiDung));
        quyens.put("phanQuyen", quyenPhanQuyen);
        
        // Quyền báo cáo và log
        Map<String, Boolean> quyenBaoCao = new HashMap<>();
        quyenBaoCao.put("xemBaoCao", authorizationService.coQuyenXemBaoCao(maDuAn, maNguoiDung));
        quyenBaoCao.put("xemLog", authorizationService.coQuyenXemLog(maDuAn, maNguoiDung));
        quyenBaoCao.put("xoaLog", authorizationService.coQuyenXoaLog(maDuAn, maNguoiDung));
        quyens.put("baoCao", quyenBaoCao);
        
        return ResponseEntity.ok(quyens);
    }

    /**
     * Kiểm tra quyền xem dự án
     */
    @GetMapping("/{maDuAn}/quyen/xem-du-an")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> kiemTraQuyenXemDuAn(@PathVariable Long maDuAn) {
        Long maNguoiDung = SecurityUtils.getCurrentUserId();
        boolean coQuyen = authorizationService.coQuyenXemDuAn(maDuAn, maNguoiDung);
        return ResponseEntity.ok(Map.of("coQuyen", coQuyen));
    }

    /**
     * Kiểm tra quyền chỉnh sửa dự án
     */
    @GetMapping("/{maDuAn}/quyen/chinh-sua-du-an")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> kiemTraQuyenChinhSuaDuAn(@PathVariable Long maDuAn) {
        Long maNguoiDung = SecurityUtils.getCurrentUserId();
        boolean coQuyen = authorizationService.coQuyenChinhSuaDuAn(maDuAn, maNguoiDung);
        return ResponseEntity.ok(Map.of("coQuyen", coQuyen));
    }

    /**
     * Kiểm tra quyền xóa dự án
     */
    @GetMapping("/{maDuAn}/quyen/xoa-du-an")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> kiemTraQuyenXoaDuAn(@PathVariable Long maDuAn) {
        Long maNguoiDung = SecurityUtils.getCurrentUserId();
        boolean coQuyen = authorizationService.coQuyenXoaDuAn(maDuAn, maNguoiDung);
        return ResponseEntity.ok(Map.of("coQuyen", coQuyen));
    }

    /**
     * Kiểm tra quyền thêm khu vực
     */
    @GetMapping("/{maDuAn}/quyen/them-khu-vuc")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> kiemTraQuyenThemKhuVuc(@PathVariable Long maDuAn) {
        Long maNguoiDung = SecurityUtils.getCurrentUserId();
        boolean coQuyen = authorizationService.coQuyenThemKhuVuc(maDuAn, maNguoiDung);
        return ResponseEntity.ok(Map.of("coQuyen", coQuyen));
    }

    /**
     * Kiểm tra quyền chỉnh sửa khu vực
     */
    @GetMapping("/{maDuAn}/quyen/chinh-sua-khu-vuc")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> kiemTraQuyenChinhSuaKhuVuc(@PathVariable Long maDuAn) {
        Long maNguoiDung = SecurityUtils.getCurrentUserId();
        boolean coQuyen = authorizationService.coQuyenChinhSuaKhuVuc(maDuAn, maNguoiDung);
        return ResponseEntity.ok(Map.of("coQuyen", coQuyen));
    }

    /**
     * Kiểm tra quyền thêm thiết bị
     */
    @GetMapping("/{maDuAn}/quyen/them-thiet-bi")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> kiemTraQuyenThemThietBi(@PathVariable Long maDuAn) {
        Long maNguoiDung = SecurityUtils.getCurrentUserId();
        boolean coQuyen = authorizationService.coQuyenThemThietBi(maDuAn, maNguoiDung);
        return ResponseEntity.ok(Map.of("coQuyen", coQuyen));
    }

    /**
     * Kiểm tra quyền chỉnh sửa thiết bị
     */
    @GetMapping("/{maDuAn}/quyen/chinh-sua-thiet-bi")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> kiemTraQuyenChinhSuaThietBi(@PathVariable Long maDuAn) {
        Long maNguoiDung = SecurityUtils.getCurrentUserId();
        boolean coQuyen = authorizationService.coQuyenChinhSuaThietBi(maDuAn, maNguoiDung);
        return ResponseEntity.ok(Map.of("coQuyen", coQuyen));
    }

    /**
     * Kiểm tra quyền điều khiển thiết bị cụ thể
     */
    @GetMapping("/thiet-bi/{maThietBi}/quyen/dieu-khien")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> kiemTraQuyenDieuKhienThietBi(@PathVariable Long maThietBi) {
        Long maNguoiDung = SecurityUtils.getCurrentUserId();
        boolean coQuyen = authorizationService.coQuyenDieuKhienThietBi(maThietBi, maNguoiDung);
        return ResponseEntity.ok(Map.of("coQuyen", coQuyen));
    }

    /**
     * Kiểm tra quyền quản lý luật
     */
    @GetMapping("/{maDuAn}/quyen/quan-ly-luat")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> kiemTraQuyenQuanLyLuat(@PathVariable Long maDuAn) {
        Long maNguoiDung = SecurityUtils.getCurrentUserId();
        boolean coQuyen = authorizationService.coQuyenQuanLyLuat(maDuAn, maNguoiDung);
        return ResponseEntity.ok(Map.of("coQuyen", coQuyen));
    }

    /**
     * Kiểm tra quyền quản lý lịch trình
     */
    @GetMapping("/{maDuAn}/quyen/quan-ly-lich-trinh")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> kiemTraQuyenQuanLyLichTrinh(@PathVariable Long maDuAn) {
        Long maNguoiDung = SecurityUtils.getCurrentUserId();
        boolean coQuyen = authorizationService.coQuyenQuanLyLichTrinh(maDuAn, maNguoiDung);
        return ResponseEntity.ok(Map.of("coQuyen", coQuyen));
    }

    /**
     * Kiểm tra quyền cấp quyền dự án
     */
    @GetMapping("/{maDuAn}/quyen/cap-quyen-du-an")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> kiemTraQuyenCapQuyenDuAn(@PathVariable Long maDuAn) {
        Long maNguoiDung = SecurityUtils.getCurrentUserId();
        boolean coQuyen = authorizationService.coQuyenCapQuyenDuAn(maDuAn, maNguoiDung);
        return ResponseEntity.ok(Map.of("coQuyen", coQuyen));
    }

    /**
     * Kiểm tra quyền cấp quyền thiết bị
     */
    @GetMapping("/{maDuAn}/quyen/cap-quyen-thiet-bi")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> kiemTraQuyenCapQuyenThietBi(@PathVariable Long maDuAn) {
        Long maNguoiDung = SecurityUtils.getCurrentUserId();
        boolean coQuyen = authorizationService.coQuyenCapQuyenThietBi(maDuAn, maNguoiDung);
        return ResponseEntity.ok(Map.of("coQuyen", coQuyen));
    }

    /**
     * Kiểm tra quyền xem báo cáo
     */
    @GetMapping("/{maDuAn}/quyen/xem-bao-cao")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> kiemTraQuyenXemBaoCao(@PathVariable Long maDuAn) {
        Long maNguoiDung = SecurityUtils.getCurrentUserId();
        boolean coQuyen = authorizationService.coQuyenXemBaoCao(maDuAn, maNguoiDung);
        return ResponseEntity.ok(Map.of("coQuyen", coQuyen));
    }

    /**
     * Kiểm tra quyền xem log
     */
    @GetMapping("/{maDuAn}/quyen/xem-log")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> kiemTraQuyenXemLog(@PathVariable Long maDuAn) {
        Long maNguoiDung = SecurityUtils.getCurrentUserId();
        boolean coQuyen = authorizationService.coQuyenXemLog(maDuAn, maNguoiDung);
        return ResponseEntity.ok(Map.of("coQuyen", coQuyen));
    }

    /**
     * Kiểm tra quyền xóa log
     */
    @GetMapping("/{maDuAn}/quyen/xoa-log")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> kiemTraQuyenXoaLog(@PathVariable Long maDuAn) {
        Long maNguoiDung = SecurityUtils.getCurrentUserId();
        boolean coQuyen = authorizationService.coQuyenXoaLog(maDuAn, maNguoiDung);
        return ResponseEntity.ok(Map.of("coQuyen", coQuyen));
    }
}
