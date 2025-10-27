package com.iot.management.controller.ui;

import com.iot.management.model.repository.NguoiDungRepository;
import com.iot.management.model.repository.ThietBiRepository;
import com.iot.management.model.repository.LoaiThietBiRepository;
import com.iot.management.security.SecurityUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @Autowired
    private ThietBiRepository thietBiRepository;

    @Autowired
    private LoaiThietBiRepository loaiThietBiRepository;

    @GetMapping({"", "/", "/dashboard"})
    public String dashboard(Model model, @AuthenticationPrincipal SecurityUser currentUser) {
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        // Set basic info
        model.addAttribute("title", "Admin Dashboard - IoT Manager");
        model.addAttribute("username", currentUser.getUsername());
        model.addAttribute("user", currentUser);

        // Get statistics
        model.addAttribute("totalUsers", nguoiDungRepository.count());
        model.addAttribute("totalDevices", thietBiRepository.count());
        model.addAttribute("totalDeviceTypes", loaiThietBiRepository.count());

        return "admin/dashboard";
    }

    @GetMapping("/profile")
    public String profile(Model model, @AuthenticationPrincipal SecurityUser currentUser) {
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        model.addAttribute("title", "Admin Profile - IoT Manager");
        model.addAttribute("username", currentUser.getUsername());
        model.addAttribute("user", currentUser);

        return "admin/profile";
    }
}