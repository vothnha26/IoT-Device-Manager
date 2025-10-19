package com.iot.management.controller.ui;

import com.iot.management.model.entity.NguoiDung;
import com.iot.management.model.repository.NguoiDungRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final NguoiDungRepository nguoiDungRepository;

    public ProfileController(NguoiDungRepository nguoiDungRepository) {
        this.nguoiDungRepository = nguoiDungRepository;
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
        
        // Count user's resources
        long totalKhuVuc = user.getKhuVucs() != null ? user.getKhuVucs().size() : 0;
        model.addAttribute("totalKhuVuc", totalKhuVuc);

        return "profile";
    }
}
