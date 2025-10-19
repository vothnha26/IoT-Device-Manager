package com.iot.management.controller.ui;

import com.iot.management.model.entity.KhuVuc;
import com.iot.management.security.SecurityUser;
import com.iot.management.service.KhuVucService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller("khuVucUiController")
@RequestMapping("/khu-vuc")
public class KhuVucController {

    @Autowired
    private KhuVucService khuVucService;

    @GetMapping
    public String khuVucManagement(Model model, @AuthenticationPrincipal SecurityUser currentUser) {
        // Kiểm tra đăng nhập
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        // Set thông tin cơ bản
        model.addAttribute("title", "Quản lý Khu vực - IoT Manager");
        model.addAttribute("username", currentUser.getUsername());
        
        // Lấy danh sách khu vực gốc (không có cha) của user
        Long userId = currentUser.getMaNguoiDung();
        List<KhuVuc> rootKhuVucs = khuVucService.getRootKhuVucsByUser(userId);
        
        model.addAttribute("rootKhuVucs", rootKhuVucs);
        
        // Lấy tất cả khu vực để làm dropdown khi thêm/sửa
        List<KhuVuc> allKhuVucs = khuVucService.getAllKhuVucsByUser(userId);
        model.addAttribute("allKhuVucs", allKhuVucs);
        
        return "khu-vuc/index";
    }
}
