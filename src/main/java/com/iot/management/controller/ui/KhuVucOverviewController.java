package com.iot.management.controller.ui;

import com.iot.management.model.entity.KhuVuc;
import com.iot.management.model.entity.NguoiDung;
import com.iot.management.service.KhuVucService;
import com.iot.management.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/khu-vuc")
public class KhuVucOverviewController {

    @Autowired
    private KhuVucService khuVucService;

    @GetMapping("")
    public String danhSachTatCaKhuVuc(Model model) {
        NguoiDung nguoiDung = SecurityUtils.getCurrentUser();
        List<KhuVuc> khuVucs = khuVucService.getAllKhuVucsByUser(nguoiDung.getMaNguoiDung());
        
        model.addAttribute("khuVucs", khuVucs);
        return "khu-vuc/overview";
    }
}