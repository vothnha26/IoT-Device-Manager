package com.iot.management.controller.ui;

import com.iot.management.model.dto.request.DuAnRequest;
import com.iot.management.model.entity.DuAn;
import com.iot.management.model.entity.NguoiDung;
import com.iot.management.service.DuAnService;
import com.iot.management.service.NguoiDungService;
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

    public DuAnController(DuAnService duAnService, NguoiDungService nguoiDungService) {
        this.duAnService = duAnService;
        this.nguoiDungService = nguoiDungService;
    }

    @GetMapping
    public String showDuAnList(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        NguoiDung nguoiDung = nguoiDungService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        List<DuAn> duAns = duAnService.findAllByNguoiDung(nguoiDung);
        
        model.addAttribute("duAns", duAns);
        return "du-an/index";
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

            duAnService.delete(maDuAn, nguoiDung);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Xóa dự án thành công");
            return ResponseEntity.ok(response);
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