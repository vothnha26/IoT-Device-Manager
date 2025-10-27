package com.iot.management.controller.ui;

import com.iot.management.model.entity.DangKyGoi;
import com.iot.management.model.entity.NguoiDung;
import com.iot.management.model.repository.DuAnRepository;
import com.iot.management.model.repository.NguoiDungRepository;
import com.iot.management.model.repository.ThietBiRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final NguoiDungRepository nguoiDungRepository;
    private final DuAnRepository duAnRepository;
    private final ThietBiRepository thietBiRepository;

    public ProfileController(NguoiDungRepository nguoiDungRepository, 
                           DuAnRepository duAnRepository,
                           ThietBiRepository thietBiRepository) {
        this.nguoiDungRepository = nguoiDungRepository;
        this.duAnRepository = duAnRepository;
        this.thietBiRepository = thietBiRepository;
    }

    @GetMapping
    public String getProfile(Model model, Authentication authentication) {
        if (authentication == null) {
            return "redirect:/auth/login";
        }

        String email = authentication.getName();
        Optional<NguoiDung> userOpt = nguoiDungRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            return "redirect:/auth/login";
        }

        NguoiDung user = userOpt.get();
        model.addAttribute("user", user);
        model.addAttribute("username", user.getTenDangNhap());
        
        // Thống kê tổng quan
        long totalDuAn = duAnRepository.countByNguoiDung(user);
        long totalKhuVuc = user.getKhuVucs() != null ? user.getKhuVucs().size() : 0;
        long totalThietBi = thietBiRepository.countByChuSoHuu(user);
        
        model.addAttribute("totalDuAn", totalDuAn);
        model.addAttribute("totalKhuVuc", totalKhuVuc);
        model.addAttribute("totalThietBi", totalThietBi);
        
        // Thông tin gói cước hiện tại (ACTIVE và còn hạn)
        DangKyGoi activePackage = user.getDangKyGois().stream()
            .filter(dk -> DangKyGoi.TRANG_THAI_ACTIVE.equals(dk.getTrangThai()) && 
                         dk.getNgayKetThuc() != null &&
                         dk.getNgayKetThuc().isAfter(LocalDateTime.now()))
            .findFirst()
            .orElse(null);
            
        model.addAttribute("activePackage", activePackage);
        
        if (activePackage != null && activePackage.getGoiCuoc() != null) {
            model.addAttribute("goiCuoc", activePackage.getGoiCuoc());
        }

        return "profile";
    }
}
