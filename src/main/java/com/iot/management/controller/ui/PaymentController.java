package com.iot.management.controller.ui;


import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.iot.management.model.entity.GoiCuoc;
import com.iot.management.model.dto.payment.PaymentRequest;
import com.iot.management.model.dto.payment.PaymentResponse;
import com.iot.management.security.SecurityUser;
import com.iot.management.service.GoiCuocService;
import com.iot.management.service.impl.PaymentService;
import com.iot.management.service.VietQRService;

@Controller
@RequestMapping("/payment")
public class PaymentController {

    private final GoiCuocService goiCuocService;
    private final PaymentService paymentService;
    private final VietQRService vietQRService;
    
    public PaymentController(GoiCuocService goiCuocService,
                             PaymentService paymentService,
                             VietQRService vietQRService) {
        this.goiCuocService = goiCuocService;
        this.paymentService = paymentService;
        this.vietQRService = vietQRService;
    }

    // ================= TẠO THANH TOÁN =================
    @PostMapping("/create-payment/{maGoiCuoc}")
    public String createPayment(@PathVariable Integer maGoiCuoc,
                                @AuthenticationPrincipal SecurityUser securityUser) throws Exception {
        if (securityUser == null) {
            return "redirect:/auth/login";
        }

        Long maNguoiDung = securityUser.getMaNguoiDung();
        if (maNguoiDung == null) {
            throw new RuntimeException("Không lấy được ID người dùng");
        }

        PaymentRequest req = new PaymentRequest();
        req.setMaGoiCuoc(maGoiCuoc);
        PaymentResponse resp = paymentService.createVnPayLink(maNguoiDung, req);
        return "redirect:" + resp.getPaymentUrl();
    }

    @GetMapping("/create-payment/{maGoiCuoc}")
    public String showQrImmediately(@PathVariable Integer maGoiCuoc,
                                    @AuthenticationPrincipal SecurityUser securityUser,
                                    Model model) throws Exception {
        
        // BẮT BUỘC ĐĂNG NHẬP - không cho phép guest checkout
        if (securityUser == null) {
            return "redirect:/auth/login?redirect=/payment/create-payment/" + maGoiCuoc;
        }
        
        Long maNguoiDung = securityUser.getMaNguoiDung();
        if (maNguoiDung == null) {
            throw new RuntimeException("Không lấy được ID người dùng từ session");
        }
        
        GoiCuoc goiCuoc = goiCuocService.findById(maGoiCuoc)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy gói cước"));

        // Tạo payment với user ID bắt buộc
        PaymentRequest req = new PaymentRequest();
        req.setMaGoiCuoc(maGoiCuoc);
        
        PaymentResponse resp = paymentService.createVnPayLink(maNguoiDung, req);

        model.addAttribute("qrImageUrl", resp.getPaymentUrl());
        model.addAttribute("goiCuoc", goiCuoc);
        model.addAttribute("amount", goiCuoc.getGiaTien());
        model.addAttribute("orderId", resp.getOrderCode());
        model.addAttribute("username", securityUser.getUsername());
        
        return "payment/qr";
    }

    // VNPAY return/ipn removed. Use SePay webhook endpoint or admin reconciliation instead.

    // VNPAY helpers removed
}