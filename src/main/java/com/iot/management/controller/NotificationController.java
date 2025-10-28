package com.iot.management.controller;

import com.iot.management.model.entity.NguoiDung;
import com.iot.management.model.entity.ThongBao;
import com.iot.management.model.repository.NguoiDungRepository;
import com.iot.management.service.ThongBaoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/notifications")
public class NotificationController {

    private final ThongBaoService thongBaoService;
    private final NguoiDungRepository nguoiDungRepository;

    public NotificationController(ThongBaoService thongBaoService,
                                 NguoiDungRepository nguoiDungRepository) {
        this.thongBaoService = thongBaoService;
        this.nguoiDungRepository = nguoiDungRepository;
    }

    @GetMapping
    public String viewNotifications(
            @RequestParam(required = false, defaultValue = "all") String filter,
            Principal principal,
            Model model) {
        
        if (principal == null) {
            return "redirect:/auth/login";
        }

        NguoiDung user = nguoiDungRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<ThongBao> notifications;
        if ("unread".equals(filter)) {
            notifications = thongBaoService.getUnreadByUser(user);
            model.addAttribute("filter", "unread");
        } else {
            notifications = thongBaoService.getAllByUser(user);
            model.addAttribute("filter", "all");
        }

        Long unreadCount = thongBaoService.countUnread(user);

        model.addAttribute("notifications", notifications);
        model.addAttribute("unreadCount", unreadCount);
        model.addAttribute("currentUri", "/notifications");

        return "notifications";
    }
    
    @GetMapping("/test")
    public String testNotifications() {
        return "test-notifications";
    }
}
