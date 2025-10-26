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

    @GetMapping("")
    public String danhSachKhuVuc(@PathVariable Long maDuAn, Model model, Authentication authentication) {
        try {
            if (authentication == null || !(authentication.getPrincipal() instanceof SecurityUser)) {
                return "redirect:/auth/login";
            }
            
            SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
            NguoiDung nguoiDung = securityUser.getNguoiDung();
            Optional<DuAn> duAnOpt = duAnService.findByIdAndNguoiDung(maDuAn, nguoiDung);
            
            if (duAnOpt.isEmpty()) {
                return "redirect:/du-an?error=not_found";
            }
            
            DuAn duAn = duAnOpt.get();
            List<KhuVuc> khuVucs = khuVucService.findByDuAn(maDuAn);
            
            model.addAttribute("duAn", duAn);
            model.addAttribute("khuVucs", khuVucs);
            model.addAttribute("maDuAn", maDuAn);
            
            return "khu-vuc/index";
        } catch (Exception e) {
            // Log the error
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