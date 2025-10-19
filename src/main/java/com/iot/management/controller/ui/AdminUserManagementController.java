package com.iot.management.controller.ui;

import com.iot.management.security.SecurityUser;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('MANAGER')")
public class AdminUserManagementController {

    @GetMapping
    public String userManagement(Model model, @AuthenticationPrincipal SecurityUser currentUser) {
        // Kiểm tra đăng nhập
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        // Set thông tin cơ bản
        model.addAttribute("title", "Quản lý Người dùng - IoT Manager");
        model.addAttribute("username", currentUser.getUsername());
        model.addAttribute("user", currentUser);
        
        return "admin/user-management";
    }
}
