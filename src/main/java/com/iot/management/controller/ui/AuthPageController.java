package com.iot.management.controller.ui;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller xử lý các trang authentication UI
 */
@Controller
@RequestMapping("/auth")
public class AuthPageController {

    /**
     * Trang đăng nhập
     */
    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                           @RequestParam(value = "logout", required = false) String logout,
                           Model model) {
        
        if (error != null) {
            model.addAttribute("errorMessage", "Email hoặc mật khẩu không đúng!");
        }
        
        if (logout != null) {
            model.addAttribute("successMessage", "Đăng xuất thành công!");
        }
        
        model.addAttribute("title", "Đăng nhập");
        return "auth/login";
    }

    /**
     * Trang đăng ký
     */
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("title", "Đăng ký tài khoản");
        return "auth/register";
    }

    /**
     * Trang xác thực tài khoản
     */
    @GetMapping("/verify-account")
    public String verifyAccountPage(@RequestParam(value = "email", required = false) String email,
                                   Model model) {
        model.addAttribute("title", "Xác thực tài khoản");
        if (email != null) {
            model.addAttribute("email", email);
        }
        return "auth/verify-account";
    }

    /**
     * Trang quên mật khẩu
     */
    @GetMapping("/forgot-password")
    public String forgotPasswordPage(Model model) {
        model.addAttribute("title", "Quên mật khẩu");
        return "auth/forgot-password";
    }

    /**
     * Trang đặt lại mật khẩu
     */
    @GetMapping("/reset-password")
    public String resetPasswordPage(@RequestParam(value = "email", required = false) String email,
                                   Model model) {
        model.addAttribute("title", "Đặt lại mật khẩu");
        if (email != null) {
            model.addAttribute("email", email);
        }
        return "auth/reset-password";
    }
}
