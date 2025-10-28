package com.iot.management.controller.ui;

import com.iot.management.model.entity.DuAn;
import com.iot.management.model.entity.KhuVuc;
import com.iot.management.model.entity.NguoiDung;
import com.iot.management.security.SecurityUser;
import com.iot.management.service.DuAnService;
import com.iot.management.service.KhuVucService;
import com.iot.management.service.PackageLimitService;
import com.iot.management.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/du-an/{maDuAn}/khu-vuc")
public class KhuVucController {

    @Autowired
    private KhuVucService khuVucService;

    @Autowired
    private DuAnService duAnService;

    @Autowired
    private PackageLimitService packageLimitService;
    
    @Autowired
    private com.iot.management.service.DuAnAuthorizationService duAnAuthorizationService; 

    @GetMapping("")
    public String danhSachKhuVuc(@PathVariable Long maDuAn, Model model, Authentication authentication) {
        try {
            // Kiểm tra authentication
            if (authentication == null || !(authentication.getPrincipal() instanceof SecurityUser)) {
                System.out.println("❌ Authentication failed - redirecting to login");
                return "redirect:/auth/login?redirect=/du-an/" + maDuAn + "/khu-vuc";
            }
            
            SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
            NguoiDung nguoiDung = securityUser.getNguoiDung();
            
            System.out.println("✅ User authenticated: " + nguoiDung.getTenDangNhap());
            System.out.println("📍 Accessing project: " + maDuAn);
            
            Optional<DuAn> duAnOpt = duAnService.findByIdAndNguoiDung(maDuAn, nguoiDung);
            
            if (duAnOpt.isEmpty()) {
                System.out.println("❌ Project not found or no access: " + maDuAn);
                return "redirect:/du-an?error=not_found";
            }
            
            DuAn duAn = duAnOpt.get();
            System.out.println("✅ Project found: " + duAn.getTenDuAn());
            
            // LẤY CHỈ NHỮNG KHU VỰC CÓ QUYỀN XEM
            List<KhuVuc> khuVucs = khuVucService.findKhuVucCoQuyenXem(maDuAn, nguoiDung.getMaNguoiDung());
            System.out.println("📊 Found " + khuVucs.size() + " zones with permission");
            
            // Kiểm tra quyền xóa dự án và khu vực
            boolean coQuyenXoaDuAn = duAnAuthorizationService.coQuyenXoaDuAn(maDuAn, nguoiDung.getMaNguoiDung());
            System.out.println("🔑 Can delete project: " + coQuyenXoaDuAn);
            
            boolean coQuyenXoaKhuVuc = duAnAuthorizationService.coQuyenXoaKhuVuc(maDuAn, nguoiDung.getMaNguoiDung());
            System.out.println("🔑 Can delete zone: " + coQuyenXoaKhuVuc);
            
            // Force load thietBis để tránh lazy loading exception
            khuVucs.forEach(kv -> {
                if (kv.getThietBis() != null) {
                    kv.getThietBis().size(); // Force initialize
                    // Ensure loaiThietBi is loaded
                    kv.getThietBis().forEach(tb -> {
                        if (tb.getLoaiThietBi() != null) {
                            tb.getLoaiThietBi().getNhomThietBi(); // Force load
                        }
                    });
                }
            });
            
            model.addAttribute("duAn", duAn);
            model.addAttribute("khuVucs", khuVucs);
            model.addAttribute("maDuAn", maDuAn);
            model.addAttribute("coQuyenXoaDuAn", coQuyenXoaDuAn);
            model.addAttribute("coQuyenXoaKhuVuc", coQuyenXoaKhuVuc);
            
            return "khu-vuc/index";
        } catch (Exception e) {
            // Log the error with more detail
            System.err.println("❌ Error in danhSachKhuVuc: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/du-an?error=unexpected";
        }
    }

    @PostMapping("/them-moi")
    @ResponseBody
    public ResponseEntity<?> themKhuVuc(
            @PathVariable Long maDuAn,
            @RequestBody Map<String, String> payload,
            Authentication authentication) {
        try {
            if (authentication == null || !(authentication.getPrincipal() instanceof SecurityUser)) {
                throw new RuntimeException("Không thể xác thực người dùng");
            }
            
            System.out.println(maDuAn);
            SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
            NguoiDung nguoiDung = securityUser.getNguoiDung();
            Optional<DuAn> duAnOpt = duAnService.findByIdAndNguoiDung(maDuAn, nguoiDung);
            
            if (duAnOpt.isEmpty()) {
                throw new RuntimeException("Dự án không tồn tại hoặc bạn không có quyền truy cập");
            }
            
            DuAn duAn = duAnOpt.get();
            KhuVuc khuVuc = new KhuVuc();

            packageLimitService.validateZoneLimit(duAn);

            khuVuc.setTenKhuVuc(payload.get("tenKhuVuc"));
            khuVuc.setLoaiKhuVuc(payload.get("loaiKhuVuc"));
            khuVuc.setDuAn(duAn);
            khuVuc.setChuSoHuu(nguoiDung);
            khuVuc.setMoTa(payload.get("moTa"));
            
            KhuVuc savedKhuVuc = khuVucService.createLocation(nguoiDung.getMaNguoiDung(), maDuAn, khuVuc, payload.get("moTa"));
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Tạo khu vực thành công");
            response.put("khuVuc", savedKhuVuc);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/{maKhuVuc}/cap-nhat")
    @ResponseBody
    public ResponseEntity<?> capNhatKhuVuc(
            @PathVariable Long maDuAn,
            @PathVariable Long maKhuVuc,
            @RequestBody KhuVuc khuVucCapNhat) {
        try {
            NguoiDung nguoiDung = SecurityUtils.getCurrentUser();

            Optional<DuAn> duAnOpt = duAnService.findByIdAndNguoiDung(maDuAn, nguoiDung);
            if (duAnOpt.isEmpty()) {
                throw new RuntimeException("Dự án không tồn tại hoặc bạn không có quyền truy cập");
            }

            khuVucCapNhat.setMaKhuVuc(maKhuVuc);
            khuVucCapNhat.setDuAn(duAnOpt.get());  // ✅ Gán lại dự án để tránh null

            KhuVuc updatedKhuVuc = khuVucService.updateLocation(nguoiDung.getMaNguoiDung(), khuVucCapNhat);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Cập nhật khu vực thành công");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }


    @DeleteMapping("/{maKhuVuc}/xoa")
    @ResponseBody
    public ResponseEntity<?> xoaKhuVuc(@PathVariable Long maDuAn, @PathVariable Long maKhuVuc) {
        try {
            NguoiDung nguoiDung = SecurityUtils.getCurrentUser();

            if (duAnService.findByIdAndNguoiDung(maDuAn, nguoiDung).isEmpty()) {
                throw new RuntimeException("Dự án không tồn tại hoặc bạn không có quyền truy cập");
            }
            
            // Kiểm tra quyền xóa khu vực (chỉ CHU_SO_HUU)
            if (!duAnAuthorizationService.coQuyenXoaKhuVuc(maDuAn, nguoiDung.getMaNguoiDung())) {
                return ResponseEntity.status(403).body(Map.of(
                    "success", false,
                    "message", "Chỉ chủ sở hữu dự án mới có quyền xóa khu vực"
                ));
            }

            KhuVuc khuVuc = khuVucService.getKhuVucById(maKhuVuc);
            if (!khuVuc.getDuAn().getMaDuAn().equals(maDuAn)) {
                throw new RuntimeException("Khu vực không thuộc dự án này");
            }

            khuVucService.deleteKhuVuc(maKhuVuc);
            return ResponseEntity.ok(Map.of("success", true, "message", "Đã xóa khu vực thành công"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}