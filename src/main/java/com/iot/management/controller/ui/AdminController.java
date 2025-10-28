package com.iot.management.controller.ui;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.iot.management.model.repository.DuAnRepository;
import com.iot.management.model.repository.KhuVucRepository;
import com.iot.management.model.repository.LoaiThietBiRepository;
import com.iot.management.model.repository.NguoiDungRepository;
import com.iot.management.model.repository.ThietBiRepository;
import com.iot.management.model.repository.ThongBaoRepository;
import com.iot.management.security.SecurityUser;

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
    
    @Autowired
    private DuAnRepository duAnRepository;
    
    @Autowired
    private KhuVucRepository khuVucRepository;
    
    @Autowired
    private ThongBaoRepository thongBaoRepository;

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
        model.addAttribute("totalAreas", khuVucRepository.count());
        model.addAttribute("totalProjects", duAnRepository.count());
        
        // Get today's notifications count
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();
        long todayNotifications = thongBaoRepository.countByThoiGianTaoBetween(startOfDay, endOfDay);
        model.addAttribute("todayNotifications", todayNotifications);

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