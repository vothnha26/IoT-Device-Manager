package com.iot.management.controller.api.authorize;

import com.iot.management.model.dto.request.CapQuyenDuAnRequest;
import com.iot.management.model.dto.request.CapQuyenThietBiRequest;
import com.iot.management.model.dto.request.CapQuyenKhuVucRequest;
import com.iot.management.model.dto.request.TaoQuyenHeThongRequest;
import com.iot.management.model.entity.PhanQuyenDuAn;
import com.iot.management.model.entity.PhanQuyenThietBi;
import com.iot.management.model.entity.PhanQuyenKhuVuc;
import com.iot.management.model.entity.QuyenHeThong;
import com.iot.management.model.enums.DuAnRole;
import com.iot.management.service.PhanQuyenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/phan-quyen")
public class PhanQuyenController {

    @Autowired
    private PhanQuyenService phanQuyenService;

    // ==================== Phân quyền dự án ====================

    /**
     * Cấp quyền cho người dùng trong dự án
     */
    @PostMapping("/du-an/cap-quyen")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> capQuyenDuAn(@RequestBody CapQuyenDuAnRequest request) {
        try {
            PhanQuyenDuAn phanQuyen = phanQuyenService.capQuyenDuAn(
                    request.getMaDuAn(),
                    request.getMaNguoiDung(),
                    request.getVaiTro()
            );
            return ResponseEntity.ok(phanQuyen);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Thu hồi quyền người dùng trong dự án
     */
    @DeleteMapping("/du-an/thu-hoi")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> thuHoiQuyenDuAn(
            @RequestParam Long maDuAn,
            @RequestParam Long maNguoiDung) {
        try {
            phanQuyenService.thuHoiQuyenDuAn(maDuAn, maNguoiDung);
            return ResponseEntity.ok(Map.of("message", "Thu hồi quyền thành công"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Cập nhật vai trò người dùng trong dự án
     */
    @PutMapping("/du-an/cap-nhat-vai-tro")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> capNhatVaiTroDuAn(@RequestBody CapQuyenDuAnRequest request) {
        try {
            PhanQuyenDuAn phanQuyen = phanQuyenService.capNhatVaiTroDuAn(
                    request.getMaDuAn(),
                    request.getMaNguoiDung(),
                    request.getVaiTro()
            );
            return ResponseEntity.ok(phanQuyen);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Lấy danh sách người dùng có quyền trong dự án
     */
    @GetMapping("/du-an/{maDuAn}/danh-sach")
    public ResponseEntity<?> layDanhSachQuyenDuAn(@PathVariable Long maDuAn) {
        try {
            List<PhanQuyenDuAn> danhSach = phanQuyenService.layDanhSachQuyenDuAn(maDuAn);
            return ResponseEntity.ok(danhSach);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Kiểm tra quyền người dùng trong dự án
     */
    @GetMapping("/du-an/kiem-tra")
    public ResponseEntity<?> kiemTraQuyenDuAn(
            @RequestParam Long maDuAn,
            @RequestParam Long maNguoiDung,
            @RequestParam DuAnRole vaiTro) {
        boolean coQuyen = phanQuyenService.kiemTraQuyenDuAn(maDuAn, maNguoiDung, vaiTro);
        return ResponseEntity.ok(Map.of("coQuyen", coQuyen));
    }

    /**
     * Lấy vai trò của người dùng trong dự án
     */
    @GetMapping("/du-an/vai-tro")
    public ResponseEntity<?> layVaiTroDuAn(
            @RequestParam Long maDuAn,
            @RequestParam Long maNguoiDung) {
        DuAnRole vaiTro = phanQuyenService.layVaiTroDuAn(maDuAn, maNguoiDung);
        if (vaiTro != null) {
            return ResponseEntity.ok(Map.of("vaiTro", vaiTro));
        } else {
            return ResponseEntity.ok(Map.of("vaiTro", "KHONG_CO_QUYEN"));
        }
    }

    // ==================== Phân quyền thiết bị ====================

    /**
     * Cấp quyền cho người dùng với thiết bị
     */
    @PostMapping("/thiet-bi/cap-quyen")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<?> capQuyenThietBi(@RequestBody CapQuyenThietBiRequest request) {
        try {
            PhanQuyenThietBi phanQuyen = phanQuyenService.capQuyenThietBi(
                    request.getMaThietBi(),
                    request.getMaNguoiDung(),
                    request.getVaiTro(),
                    request.getCoQuyenDieuKhien(),
                    request.getCoQuyenXemDuLieu(),
                    request.getCoQuyenChinhSua()
            );
            return ResponseEntity.ok(phanQuyen);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Thu hồi quyền người dùng với thiết bị
     */
    @DeleteMapping("/thiet-bi/thu-hoi")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<?> thuHoiQuyenThietBi(
            @RequestParam Long maThietBi,
            @RequestParam Long maNguoiDung) {
        try {
            phanQuyenService.thuHoiQuyenThietBi(maThietBi, maNguoiDung);
            return ResponseEntity.ok(Map.of("message", "Thu hồi quyền thiết bị thành công"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Cập nhật quyền người dùng với thiết bị
     */
    @PutMapping("/thiet-bi/cap-nhat")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<?> capNhatQuyenThietBi(@RequestBody CapQuyenThietBiRequest request) {
        try {
            PhanQuyenThietBi phanQuyen = phanQuyenService.capNhatQuyenThietBi(
                    request.getMaThietBi(),
                    request.getMaNguoiDung(),
                    request.getCoQuyenDieuKhien(),
                    request.getCoQuyenXemDuLieu(),
                    request.getCoQuyenChinhSua()
            );
            return ResponseEntity.ok(phanQuyen);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Lấy danh sách người dùng có quyền với thiết bị
     */
    @GetMapping("/thiet-bi/{maThietBi}/danh-sach")
    public ResponseEntity<?> layDanhSachQuyenThietBi(@PathVariable Long maThietBi) {
        try {
            List<PhanQuyenThietBi> danhSach = phanQuyenService.layDanhSachQuyenThietBi(maThietBi);
            return ResponseEntity.ok(danhSach);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Kiểm tra các quyền của người dùng với thiết bị
     */
    @GetMapping("/thiet-bi/kiem-tra")
    public ResponseEntity<?> kiemTraQuyenThietBi(
            @RequestParam Long maThietBi,
            @RequestParam Long maNguoiDung) {
        Map<String, Boolean> quyens = new HashMap<>();
        quyens.put("coQuyenDieuKhien", 
                phanQuyenService.kiemTraQuyenDieuKhienThietBi(maThietBi, maNguoiDung));
        quyens.put("coQuyenXemDuLieu", 
                phanQuyenService.kiemTraQuyenXemDuLieuThietBi(maThietBi, maNguoiDung));
        quyens.put("coQuyenChinhSua", 
                phanQuyenService.kiemTraQuyenChinhSuaThietBi(maThietBi, maNguoiDung));
        return ResponseEntity.ok(quyens);
    }

    // ==================== Quản lý quyền hệ thống ====================

    /**
     * Tạo quyền hệ thống mới
     */
    @PostMapping("/he-thong/tao-quyen")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> taoQuyenHeThong(@RequestBody TaoQuyenHeThongRequest request) {
        try {
            QuyenHeThong quyen = phanQuyenService.taoQuyenHeThong(
                    request.getMaNhom(),
                    request.getTenQuyen(),
                    request.getMoTa()
            );
            return ResponseEntity.ok(quyen);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Lấy danh sách tất cả quyền hệ thống
     */
    @GetMapping("/he-thong/danh-sach")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> layDanhSachQuyenHeThong() {
        List<QuyenHeThong> danhSach = phanQuyenService.layDanhSachQuyenHeThong();
        return ResponseEntity.ok(danhSach);
    }

    /**
     * Lấy danh sách quyền theo nhóm
     */
    @GetMapping("/he-thong/nhom/{maNhom}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> layQuyenTheoNhom(@PathVariable String maNhom) {
        List<QuyenHeThong> danhSach = phanQuyenService.layQuyenTheoNhom(maNhom);
        return ResponseEntity.ok(danhSach);
    }

    /**
     * Cấp quyền hệ thống cho vai trò
     */
    @PostMapping("/he-thong/cap-quyen-vai-tro")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> capQuyenChoVaiTro(
            @RequestParam Long maVaiTro,
            @RequestParam Long maQuyen) {
        try {
            phanQuyenService.capQuyenChoVaiTro(maVaiTro, maQuyen);
            return ResponseEntity.ok(Map.of("message", "Cấp quyền cho vai trò thành công"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Thu hồi quyền hệ thống từ vai trò
     */
    @DeleteMapping("/he-thong/thu-hoi-quyen-vai-tro")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> thuHoiQuyenTuVaiTro(
            @RequestParam Long maVaiTro,
            @RequestParam Long maQuyen) {
        try {
            phanQuyenService.thuHoiQuyenTuVaiTro(maVaiTro, maQuyen);
            return ResponseEntity.ok(Map.of("message", "Thu hồi quyền từ vai trò thành công"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Lấy danh sách quyền của vai trò
     */
    @GetMapping("/he-thong/quyen-vai-tro/{maVaiTro}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> layQuyenCuaVaiTro(@PathVariable Long maVaiTro) {
        List<QuyenHeThong> danhSach = phanQuyenService.layQuyenCuaVaiTro(maVaiTro);
        return ResponseEntity.ok(danhSach);
    }

    /**
     * Kiểm tra vai trò có quyền hệ thống
     */
    @GetMapping("/he-thong/kiem-tra-vai-tro")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> kiemTraVaiTroCoQuyen(
            @RequestParam Long maVaiTro,
            @RequestParam String tenQuyen) {
        boolean coQuyen = phanQuyenService.kiemTraVaiTroCoQuyen(maVaiTro, tenQuyen);
        return ResponseEntity.ok(Map.of("coQuyen", coQuyen));
    }
    
    // ==================== Phân quyền khu vực ====================

    /**
     * Cấp quyền cho người dùng với khu vực
     */
    @PostMapping("/khu-vuc/cap-quyen")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<?> capQuyenKhuVuc(@RequestBody CapQuyenKhuVucRequest request) {
        try {
            PhanQuyenKhuVuc phanQuyen = phanQuyenService.capQuyenKhuVuc(
                    request.getMaKhuVuc(),
                    request.getMaNguoiDung(),
                    request.getVaiTro()
            );
            return ResponseEntity.ok(phanQuyen);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Thu hồi quyền người dùng với khu vực
     */
    @DeleteMapping("/khu-vuc/thu-hoi")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<?> thuHoiQuyenKhuVuc(
            @RequestParam Long maKhuVuc,
            @RequestParam Long maNguoiDung) {
        try {
            phanQuyenService.thuHoiQuyenKhuVuc(maKhuVuc, maNguoiDung);
            return ResponseEntity.ok(Map.of("message", "Thu hồi quyền khu vực thành công"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Cập nhật vai trò người dùng với khu vực
     */
    @PutMapping("/khu-vuc/cap-nhat-vai-tro")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<?> capNhatVaiTroKhuVuc(@RequestBody CapQuyenKhuVucRequest request) {
        try {
            PhanQuyenKhuVuc phanQuyen = phanQuyenService.capNhatVaiTroKhuVuc(
                    request.getMaKhuVuc(),
                    request.getMaNguoiDung(),
                    request.getVaiTro()
            );
            return ResponseEntity.ok(phanQuyen);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Lấy danh sách người dùng có quyền với khu vực
     */
    @GetMapping("/khu-vuc/{maKhuVuc}/danh-sach")
    public ResponseEntity<?> layDanhSachQuyenKhuVuc(@PathVariable Long maKhuVuc) {
        try {
            List<PhanQuyenKhuVuc> danhSach = phanQuyenService.layDanhSachQuyenKhuVuc(maKhuVuc);
            return ResponseEntity.ok(danhSach);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Kiểm tra các quyền của người dùng với khu vực
     */
    @GetMapping("/khu-vuc/kiem-tra")
    public ResponseEntity<?> kiemTraQuyenKhuVuc(
            @RequestParam Long maKhuVuc,
            @RequestParam Long maNguoiDung) {
        Map<String, Object> quyens = new HashMap<>();
        quyens.put("coQuyenQuanLy", 
                phanQuyenService.kiemTraQuyenQuanLyKhuVuc(maKhuVuc, maNguoiDung));
        quyens.put("coQuyenXem", 
                phanQuyenService.kiemTraQuyenXemKhuVuc(maKhuVuc, maNguoiDung));
        quyens.put("vaiTro", 
                phanQuyenService.layVaiTroKhuVuc(maKhuVuc, maNguoiDung));
        return ResponseEntity.ok(quyens);
    }
}
