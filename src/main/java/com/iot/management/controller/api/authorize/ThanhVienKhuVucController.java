package com.iot.management.controller.api.authorize;

import com.iot.management.model.entity.KhuVuc;
import com.iot.management.model.entity.NguoiDung;
import com.iot.management.model.entity.PhanQuyenKhuVuc;
import com.iot.management.repository.KhuVucRepository;
import com.iot.management.repository.NguoiDungRepository;
import com.iot.management.repository.PhanQuyenKhuVucRepository;
import com.iot.management.service.KhuVucAuthorizationService;
import com.iot.management.service.PhanQuyenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/khu-vuc")
public class ThanhVienKhuVucController {

    @Autowired
    private KhuVucRepository khuVucRepository;

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @Autowired
    private PhanQuyenKhuVucRepository phanQuyenKhuVucRepository;

    @Autowired
    private PhanQuyenService phanQuyenService;

    @Autowired
    private KhuVucAuthorizationService khuVucAuthorizationService;

    /**
     * Trang quản lý thành viên khu vực
     */
    @GetMapping("/{id}/thanh-vien")
    public String danhSachThanhVien(@PathVariable Long id, Model model, Authentication authentication) {
        try {
            // Lấy thông tin người dùng hiện tại
            NguoiDung currentUser = nguoiDungRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

            // Lấy thông tin khu vực
            KhuVuc khuVuc = khuVucRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy khu vực"));

            // Kiểm tra quyền quản lý khu vực (chỉ chủ sở hữu hoặc quản lý dự án mới được quản lý thành viên khu vực)
            boolean coQuyenQuanLy = khuVucAuthorizationService.coQuyenCapQuyenKhuVuc(id, currentUser.getMaNguoiDung());
            
            if (!coQuyenQuanLy) {
                return "redirect:/du-an/" + khuVuc.getDuAn().getMaDuAn() + "/khu-vuc?error=no_permission";
            }

            // Lấy danh sách thành viên có quyền với khu vực
            List<PhanQuyenKhuVuc> danhSachQuyen = phanQuyenService.layDanhSachQuyenKhuVuc(id);

            model.addAttribute("khuVuc", khuVuc);
            model.addAttribute("danhSachQuyen", danhSachQuyen);
            model.addAttribute("currentUser", currentUser);

            return "khu-vuc/thanh-vien";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/du-an?error=unexpected";
        }
    }

    /**
     * API: Mời thành viên vào khu vực
     */
    @PostMapping("/{id}/thanh-vien/moi")
    @ResponseBody
    public ResponseEntity<?> moiThanhVien(
            @PathVariable Long id,
            @RequestBody MoiThanhVienRequest request,
            Authentication authentication) {
        try {
            // Lấy người dùng hiện tại
            NguoiDung currentUser = nguoiDungRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

            // Kiểm tra quyền
            boolean coQuyenQuanLy = khuVucAuthorizationService.coQuyenCapQuyenKhuVuc(id, currentUser.getMaNguoiDung());
            if (!coQuyenQuanLy) {
                return ResponseEntity.status(403).body(Map.of(
                        "success", false,
                        "message", "Bạn không có quyền cấp phân quyền cho khu vực này"
                ));
            }

            // Tìm người dùng được mời theo email hoặc username
            NguoiDung nguoiDuocMoi = nguoiDungRepository.findByEmail(request.getEmail())
                    .or(() -> nguoiDungRepository.findByTenDangNhap(request.getEmail()))
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với email/username: " + request.getEmail()));

            // Kiểm tra đã có quyền chưa
            boolean daTonTai = phanQuyenKhuVucRepository
                    .findByKhuVucAndNguoiDung(
                            khuVucRepository.findById(id).orElseThrow(),
                            nguoiDuocMoi
                    ).isPresent();

            if (daTonTai) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Người dùng đã có quyền với khu vực này"
                ));
            }

            // Cấp quyền
            PhanQuyenKhuVuc phanQuyen = phanQuyenService.capQuyenKhuVuc(
                    id,
                    nguoiDuocMoi.getMaNguoiDung(),
                    request.getVaiTro()
            );

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Đã thêm thành viên thành công");
            response.put("phanQuyen", Map.of(
                    "maPhanQuyen", phanQuyen.getMaPhanQuyen(),
                    "tenDangNhap", nguoiDuocMoi.getTenDangNhap(),
                    "email", nguoiDuocMoi.getEmail(),
                    "vaiTro", phanQuyen.getVaiTro()
            ));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Có lỗi xảy ra: " + e.getMessage()
            ));
        }
    }

    /**
     * API: Xóa thành viên khỏi khu vực
     */
    @DeleteMapping("/thanh-vien/xoa/{maPhanQuyen}")
    @ResponseBody
    public ResponseEntity<?> xoaThanhVien(
            @PathVariable Long maPhanQuyen,
            Authentication authentication) {
        try {
            // Lấy người dùng hiện tại
            NguoiDung currentUser = nguoiDungRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

            // Lấy thông tin phân quyền
            PhanQuyenKhuVuc phanQuyen = phanQuyenKhuVucRepository.findById(maPhanQuyen)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy phân quyền"));

            Long maKhuVuc = phanQuyen.getKhuVuc().getMaKhuVuc();

            // Kiểm tra quyền
            boolean coQuyenQuanLy = khuVucAuthorizationService.coQuyenCapQuyenKhuVuc(maKhuVuc, currentUser.getMaNguoiDung());
            if (!coQuyenQuanLy) {
                return ResponseEntity.status(403).body(Map.of(
                        "success", false,
                        "message", "Bạn không có quyền xóa thành viên khỏi khu vực này"
                ));
            }

            // Không cho xóa chủ sở hữu khu vực
            if (phanQuyen.getKhuVuc().getChuSoHuu().getMaNguoiDung().equals(phanQuyen.getNguoiDung().getMaNguoiDung())) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Không thể xóa chủ sở hữu khu vực"
                ));
            }

            // Xóa phân quyền
            phanQuyenKhuVucRepository.delete(phanQuyen);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Đã xóa thành viên thành công"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Có lỗi xảy ra: " + e.getMessage()
            ));
        }
    }

    /**
     * API: Cập nhật vai trò thành viên
     */
    @PutMapping("/thanh-vien/cap-nhat/{maPhanQuyen}")
    @ResponseBody
    public ResponseEntity<?> capNhatVaiTro(
            @PathVariable Long maPhanQuyen,
            @RequestBody CapNhatVaiTroRequest request,
            Authentication authentication) {
        try {
            // Lấy người dùng hiện tại
            NguoiDung currentUser = nguoiDungRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

            // Lấy thông tin phân quyền
            PhanQuyenKhuVuc phanQuyen = phanQuyenKhuVucRepository.findById(maPhanQuyen)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy phân quyền"));

            Long maKhuVuc = phanQuyen.getKhuVuc().getMaKhuVuc();

            // Kiểm tra quyền
            boolean coQuyenQuanLy = khuVucAuthorizationService.coQuyenCapQuyenKhuVuc(maKhuVuc, currentUser.getMaNguoiDung());
            if (!coQuyenQuanLy) {
                return ResponseEntity.status(403).body(Map.of(
                        "success", false,
                        "message", "Bạn không có quyền cập nhật vai trò thành viên"
                ));
            }

            // Cập nhật vai trò
            phanQuyen.setVaiTro(request.getVaiTro());
            phanQuyenKhuVucRepository.save(phanQuyen);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Đã cập nhật vai trò thành công"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Có lỗi xảy ra: " + e.getMessage()
            ));
        }
    }

    // DTO classes
    static class MoiThanhVienRequest {
        private String email;
        private String vaiTro;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getVaiTro() {
            return vaiTro;
        }

        public void setVaiTro(String vaiTro) {
            this.vaiTro = vaiTro;
        }
    }

    static class CapNhatVaiTroRequest {
        private String vaiTro;

        public String getVaiTro() {
            return vaiTro;
        }

        public void setVaiTro(String vaiTro) {
            this.vaiTro = vaiTro;
        }
    }
}
