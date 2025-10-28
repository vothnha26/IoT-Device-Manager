package com.iot.management.controller.ui;

import com.iot.management.service.GoiCuocService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final GoiCuocService goiCuocService;
    public HomeController(GoiCuocService goiCuocService) {
        this.goiCuocService = goiCuocService;
    }

    @GetMapping("/")
    public String showHomePage(Model model) {
        System.out.println("=== HomeController / accessed ===");
        try {
            model.addAttribute("title", "Trang chủ - IoT Management");
            model.addAttribute("goiCuocs", goiCuocService.findAll());
            return "homepage";
        } catch (Exception e) {
            System.err.println("Error in showHomePage: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}