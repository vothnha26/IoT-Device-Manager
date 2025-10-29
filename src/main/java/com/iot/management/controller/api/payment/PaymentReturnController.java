package com.iot.management.controller.api.payment;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.iot.management.model.entity.DangKyGoi;
import com.iot.management.repository.DangKyGoiRepository;

@Controller
public class PaymentReturnController {

    private final DangKyGoiRepository dangKyGoiRepository;

    public PaymentReturnController(DangKyGoiRepository dangKyGoiRepository) {
        this.dangKyGoiRepository = dangKyGoiRepository;
    }

    /**
     * Simple return endpoint for SePay redirects.
     * SePay may redirect users back to this URL after payment.
     * Example: /payment/return?order_id=5&status=success
     */
    @GetMapping("/payment/return")
    public String paymentReturn(@RequestParam(name = "order_id", required = false) Integer orderId,
                                @RequestParam(name = "status", required = false) String status,
                                Model model) {
        if (orderId != null) {
            Optional<DangKyGoi> dk = dangKyGoiRepository.findById(orderId.longValue());
            dk.ifPresent(d -> model.addAttribute("order", d));
        }
        model.addAttribute("status", status != null ? status : "unknown");
        return "payment/return";
    }
}
