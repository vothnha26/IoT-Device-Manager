package com.iot.management.controller.ui;

import com.iot.management.security.SecurityUser;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/packages")
@PreAuthorize("hasRole('ADMIN')")
public class AdminPackageManagementController {

    @GetMapping
    public String packageManagement(Model model, @AuthenticationPrincipal SecurityUser currentUser) {
        // Kiểm tra đăng nhập
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        // Set thông tin cơ bản
        model.addAttribute("title", "Quản lý Gói cước - IoT Manager");
        model.addAttribute("username", currentUser.getUsername());
        model.addAttribute("user", currentUser);
        
        return "admin/package-management";
    }
}
