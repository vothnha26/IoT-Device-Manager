package com.iot.management.controller.api.authorize;

import com.iot.management.service.KhuVucAuthorizationService;
import com.iot.management.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/khu-vuc-authorization")
public class KhuVucAuthorizationController {

    @Autowired
    private KhuVucAuthorizationService authorizationService;

    /**
     * Kiểm tra tất cả quyền của người dùng trong khu vực
     */
    @GetMapping("/{maKhuVuc}/kiem-tra-tat-ca")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> kiemTraTatCaQuyen(@PathVariable Long maKhuVuc) {
        Long maNguoiDung = SecurityUtils.getCurrentUserId();
        
        Map<String, Object> quyens = new HashMap<>();
        
        // Thông tin vai trò
        String vaiTro = authorizationService.layVaiTroTrongKhuVuc(maKhuVuc, maNguoiDung);
        quyens.put("vaiTro", vaiTro);
        quyens.put("laQuanLyKhuVuc", authorizationService.laQuanLyKhuVuc(maKhuVuc, maNguoiDung));
        quyens.put("coQuyenQuanLyThongQuaDuAn", authorizationService.coQuyenQuanLyThongQuaDuAn(maKhuVuc, maNguoiDung));
        
        // Quyền xem thông tin
        Map<String, Boolean> quyenXem = new HashMap<>();
        quyenXem.put("thongTinKhuVuc", authorizationService.coQuyenXemThongTinKhuVuc(maKhuVuc, maNguoiDung));
        quyenXem.put("danhSachThietBi", authorizationService.coQuyenXemDanhSachThietBi(maKhuVuc, maNguoiDung));
        quyenXem.put("duLieuCamBien", authorizationService.coQuyenXemDuLieuCamBien(maKhuVuc, maNguoiDung));
        quyenXem.put("canhBao", authorizationService.coQuyenXemCanhBao(maKhuVuc, maNguoiDung));
        quyenXem.put("nhatKy", authorizationService.coQuyenXemNhatKy(maKhuVuc, maNguoiDung));
        quyens.put("xem", quyenXem);
        
        // Quyền chỉnh sửa
        Map<String, Boolean> quyenChinhSua = new HashMap<>();
        quyenChinhSua.put("thongTinKhuVuc", authorizationService.coQuyenChinhSuaThongTinKhuVuc(maKhuVuc, maNguoiDung));
        quyens.put("chinhSua", quyenChinhSua);
        
        // Quyền quản lý thiết bị
        Map<String, Boolean> quyenThietBi = new HashMap<>();
        quyenThietBi.put("them", authorizationService.coQuyenThemThietBi(maKhuVuc, maNguoiDung));
        quyenThietBi.put("xoa", authorizationService.coQuyenXoaThietBi(maKhuVuc, maNguoiDung));
        quyenThietBi.put("dieuKhien", authorizationService.coQuyenDieuKhienThietBi(maKhuVuc, maNguoiDung));
        quyens.put("thietBi", quyenThietBi);
        
        // Quyền phân quyền
        Map<String, Boolean> quyenPhanQuyen = new HashMap<>();
        quyenPhanQuyen.put("capQuyenThietBi", authorizationService.coQuyenCapQuyenThietBi(maKhuVuc, maNguoiDung));
        quyenPhanQuyen.put("capQuyenKhuVuc", authorizationService.coQuyenCapQuyenKhuVuc(maKhuVuc, maNguoiDung));
        quyens.put("phanQuyen", quyenPhanQuyen);
        
        return ResponseEntity.ok(quyens);
    }

    /**
     * Lấy vai trò trong khu vực
     */
    @GetMapping("/{maKhuVuc}/vai-tro")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> layVaiTro(@PathVariable Long maKhuVuc) {
        Long maNguoiDung = SecurityUtils.getCurrentUserId();
        String vaiTro = authorizationService.layVaiTroTrongKhuVuc(maKhuVuc, maNguoiDung);
        
        Map<String, Object> result = new HashMap<>();
        result.put("vaiTro", vaiTro);
        result.put("moTa", getVaiTroDescription(vaiTro));
        
        return ResponseEntity.ok(result);
    }

    /**
     * Kiểm tra quyền xem thông tin khu vực
     */
    @GetMapping("/{maKhuVuc}/quyen/xem-thong-tin")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> kiemTraQuyenXemThongTin(@PathVariable Long maKhuVuc) {
        Long maNguoiDung = SecurityUtils.getCurrentUserId();
        boolean coQuyen = authorizationService.coQuyenXemThongTinKhuVuc(maKhuVuc, maNguoiDung);
        return ResponseEntity.ok(Map.of("coQuyen", coQuyen));
    }

    /**
     * Kiểm tra quyền xem danh sách thiết bị
     */
    @GetMapping("/{maKhuVuc}/quyen/xem-danh-sach-thiet-bi")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> kiemTraQuyenXemDanhSachThietBi(@PathVariable Long maKhuVuc) {
        Long maNguoiDung = SecurityUtils.getCurrentUserId();
        boolean coQuyen = authorizationService.coQuyenXemDanhSachThietBi(maKhuVuc, maNguoiDung);
        return ResponseEntity.ok(Map.of("coQuyen", coQuyen));
    }

    /**
     * Kiểm tra quyền xem dữ liệu cảm biến
     */
    @GetMapping("/{maKhuVuc}/quyen/xem-du-lieu-cam-bien")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> kiemTraQuyenXemDuLieuCamBien(@PathVariable Long maKhuVuc) {
        Long maNguoiDung = SecurityUtils.getCurrentUserId();
        boolean coQuyen = authorizationService.coQuyenXemDuLieuCamBien(maKhuVuc, maNguoiDung);
        return ResponseEntity.ok(Map.of("coQuyen", coQuyen));
    }

    /**
     * Kiểm tra quyền xem cảnh báo
     */
    @GetMapping("/{maKhuVuc}/quyen/xem-canh-bao")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> kiemTraQuyenXemCanhBao(@PathVariable Long maKhuVuc) {
        Long maNguoiDung = SecurityUtils.getCurrentUserId();
        boolean coQuyen = authorizationService.coQuyenXemCanhBao(maKhuVuc, maNguoiDung);
        return ResponseEntity.ok(Map.of("coQuyen", coQuyen));
    }

    /**
     * Kiểm tra quyền xem nhật ký
     */
    @GetMapping("/{maKhuVuc}/quyen/xem-nhat-ky")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> kiemTraQuyenXemNhatKy(@PathVariable Long maKhuVuc) {
        Long maNguoiDung = SecurityUtils.getCurrentUserId();
        boolean coQuyen = authorizationService.coQuyenXemNhatKy(maKhuVuc, maNguoiDung);
        return ResponseEntity.ok(Map.of("coQuyen", coQuyen));
    }

    /**
     * Kiểm tra quyền chỉnh sửa thông tin khu vực
     */
    @GetMapping("/{maKhuVuc}/quyen/chinh-sua-thong-tin")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> kiemTraQuyenChinhSuaThongTin(@PathVariable Long maKhuVuc) {
        Long maNguoiDung = SecurityUtils.getCurrentUserId();
        boolean coQuyen = authorizationService.coQuyenChinhSuaThongTinKhuVuc(maKhuVuc, maNguoiDung);
        return ResponseEntity.ok(Map.of("coQuyen", coQuyen));
    }

    /**
     * Kiểm tra quyền thêm thiết bị
     */
    @GetMapping("/{maKhuVuc}/quyen/them-thiet-bi")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> kiemTraQuyenThemThietBi(@PathVariable Long maKhuVuc) {
        Long maNguoiDung = SecurityUtils.getCurrentUserId();
        boolean coQuyen = authorizationService.coQuyenThemThietBi(maKhuVuc, maNguoiDung);
        return ResponseEntity.ok(Map.of("coQuyen", coQuyen));
    }

    /**
     * Kiểm tra quyền xóa thiết bị
     */
    @GetMapping("/{maKhuVuc}/quyen/xoa-thiet-bi")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> kiemTraQuyenXoaThietBi(@PathVariable Long maKhuVuc) {
        Long maNguoiDung = SecurityUtils.getCurrentUserId();
        boolean coQuyen = authorizationService.coQuyenXoaThietBi(maKhuVuc, maNguoiDung);
        return ResponseEntity.ok(Map.of("coQuyen", coQuyen));
    }

    /**
     * Kiểm tra quyền điều khiển thiết bị
     */
    @GetMapping("/{maKhuVuc}/quyen/dieu-khien-thiet-bi")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> kiemTraQuyenDieuKhienThietBi(@PathVariable Long maKhuVuc) {
        Long maNguoiDung = SecurityUtils.getCurrentUserId();
        boolean coQuyen = authorizationService.coQuyenDieuKhienThietBi(maKhuVuc, maNguoiDung);
        return ResponseEntity.ok(Map.of("coQuyen", coQuyen));
    }

    /**
     * Kiểm tra quyền cấp quyền thiết bị
     */
    @GetMapping("/{maKhuVuc}/quyen/cap-quyen-thiet-bi")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> kiemTraQuyenCapQuyenThietBi(@PathVariable Long maKhuVuc) {
        Long maNguoiDung = SecurityUtils.getCurrentUserId();
        boolean coQuyen = authorizationService.coQuyenCapQuyenThietBi(maKhuVuc, maNguoiDung);
        return ResponseEntity.ok(Map.of("coQuyen", coQuyen));
    }

    /**
     * Kiểm tra quyền cấp quyền khu vực
     */
    @GetMapping("/{maKhuVuc}/quyen/cap-quyen-khu-vuc")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> kiemTraQuyenCapQuyenKhuVuc(@PathVariable Long maKhuVuc) {
        Long maNguoiDung = SecurityUtils.getCurrentUserId();
        boolean coQuyen = authorizationService.coQuyenCapQuyenKhuVuc(maKhuVuc, maNguoiDung);
        return ResponseEntity.ok(Map.of("coQuyen", coQuyen));
    }

    /**
     * Lấy mô tả vai trò
     */
    private String getVaiTroDescription(String vaiTro) {
        if (vaiTro == null) {
            return "Không có quyền trong khu vực này";
        }
        
        switch (vaiTro) {
            case "QUAN_LY_KHU_VUC":
                return "Toàn quyền trong khu vực: Xem, sửa thông tin, quản lý thiết bị, cấp quyền, xem cảnh báo và nhật ký";
            case "XEM_KHU_VUC":
                return "Chỉ xem dữ liệu: Xem danh sách thiết bị, dữ liệu cảm biến, trạng thái (không được điều khiển hoặc thêm thiết bị)";
            default:
                return "Vai trò không xác định";
        }
    }
}
