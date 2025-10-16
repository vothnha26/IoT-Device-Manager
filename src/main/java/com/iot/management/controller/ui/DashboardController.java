package com.iot.management.controller.ui;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping({"/dashboard", "/"})
    public String dashboard(Model model) {
        // For demo we can add some defaults if needed
        model.addAttribute("title", "IoT Realtime Dashboard");
        return "dashboard";
    }
}
