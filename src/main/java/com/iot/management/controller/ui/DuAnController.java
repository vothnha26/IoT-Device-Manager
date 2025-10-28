package com.iot.management.controller.ui;

import com.iot.management.model.dto.request.DuAnRequest;
import com.iot.management.model.entity.DuAn;
import com.iot.management.model.entity.NguoiDung;
import com.iot.management.service.DuAnService;
import com.iot.management.service.NguoiDungService;
import com.iot.management.service.DuAnAuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import java.util.List;

@Controller
@RequestMapping("/du-an")
public class DuAnController {

    private final DuAnService duAnService;
    private final NguoiDungService nguoiDungService;
    
    @Autowired
    private DuAnAuthorizationService duAnAuthorizationService;

    public DuAnController(DuAnService duAnService, NguoiDungService nguoiDungService) {
        this.duAnService = duAnService;
        this.nguoiDungService = nguoiDungService;
    }

    @GetMapping
    public String showDuAnList(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        try {
            if (userDetails == null) {
                System.out.println("⚠️ User not authenticated, redirecting to login");
                return "redirect:/auth/login";
            }
            
            System.out.println("✅ User authenticated: " + userDetails.getUsername());
            NguoiDung nguoiDung = nguoiDungService.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

            List<DuAn> duAns = duAnService.findAllByNguoiDung(nguoiDung);
            System.out.println("📊 Found " + duAns.size() + " projects for user: " + nguoiDung.getEmail());
            
            // Tạo Map để lưu các quyền cho từng dự án
            Map<Long, Boolean> deletePermissions = new HashMap<>();
            Map<Long, Boolean> managePermissions = new HashMap<>();
            Map<Long, Boolean> editPermissions = new HashMap<>();
            Map<Long, String> userRoles = new HashMap<>();
            
            for (DuAn duAn : duAns) {
                Long maDuAn = duAn.getMaDuAn();
                Long maNguoiDung = nguoiDung.getMaNguoiDung();
                
                // Quyền xóa (chỉ CHU_SO_HUU)
                boolean coQuyenXoa = duAnAuthorizationService.coQuyenXoaDuAn(maDuAn, maNguoiDung);
                deletePermissions.put(maDuAn, coQuyenXoa);
                
                // Quyền quản lý thành viên và mời thành viên (chỉ CHU_SO_HUU)
                boolean coQuyenQuanLy = duAnAuthorizationService.laChuSoHuu(maDuAn, maNguoiDung);
                managePermissions.put(maDuAn, coQuyenQuanLy);
                
                // Quyền chỉnh sửa (CHU_SO_HUU và QUAN_LY)
                boolean coQuyenChinhSua = duAnAuthorizationService.laQuanLyTroLen(maDuAn, maNguoiDung);
                editPermissions.put(maDuAn, coQuyenChinhSua);
                
                // Lấy vai trò của user trong dự án
                com.iot.management.model.enums.DuAnRole vaiTro = duAnAuthorizationService.layVaiTroTrongDuAn(maDuAn, maNguoiDung);
                userRoles.put(maDuAn, vaiTro != null ? vaiTro.name() : "NGUOI_DUNG");
            }
            
            model.addAttribute("duAns", duAns);
            model.addAttribute("deletePermissions", deletePermissions);
            model.addAttribute("managePermissions", managePermissions);
            model.addAttribute("editPermissions", editPermissions);
            model.addAttribute("userRoles", userRoles);
            return "du-an/index";
        } catch (Exception e) {
            System.err.println("❌ Error loading projects: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", e.getMessage());
            model.addAttribute("duAns", List.of());
            return "du-an/index";
        }
    }

    @PostMapping("/them-moi")
    public ResponseEntity<Map<String, Object>> themDuAn(
            @RequestBody DuAnRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            NguoiDung nguoiDung = nguoiDungService.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

            DuAn duAn = duAnService.create(request, nguoiDung);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Tạo dự án thành công");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/{maDuAn}/xoa")
    public ResponseEntity<Map<String, Object>> xoaDuAn(
            @PathVariable Long maDuAn,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            NguoiDung nguoiDung = nguoiDungService.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

            // Xóa dự án - service sẽ tự kiểm tra quyền CHU_SO_HUU
            duAnService.delete(maDuAn, nguoiDung);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Xóa dự án thành công");
            return ResponseEntity.ok(response);
        } catch (org.springframework.security.access.AccessDeniedException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Chỉ chủ sở hữu mới có quyền xóa dự án");
            return ResponseEntity.status(403).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/{maDuAn}/cap-nhat")
    public ResponseEntity<Map<String, Object>> capNhatDuAn(
            @PathVariable Long maDuAn,
            @RequestBody DuAnRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            NguoiDung nguoiDung = nguoiDungService.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

            DuAn duAn = duAnService.update(maDuAn, request, nguoiDung);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Cập nhật dự án thành công");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}