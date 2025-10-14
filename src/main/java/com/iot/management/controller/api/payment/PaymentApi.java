package com.iot.management.controller.api.payment;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.iot.management.model.dto.payment.PaymentRequest;
import com.iot.management.model.dto.payment.PaymentResponse;
import com.iot.management.model.entity.NguoiDung;
import com.iot.management.model.repository.NguoiDungRepository;
import com.iot.management.service.impl.PaymentService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/auth/payment")
public class PaymentApi {
    private final PaymentService paymentService;
    private final NguoiDungRepository nguoiDungRepository;

    @Value("${app.frontend-url}")
    private String frontendUrl; 

    public PaymentApi(PaymentService paymentService, NguoiDungRepository nguoiDungRepository) {
        this.paymentService = paymentService;
        this.nguoiDungRepository = nguoiDungRepository;
    }

    @PostMapping("/create-link")
    @PreAuthorize("hasAnyRole('USER', 'MANAGER')")
    public ResponseEntity<PaymentResponse> createPaymentLink(
            @RequestBody PaymentRequest request,
            @AuthenticationPrincipal UserDetails userDetails, 
            HttpServletRequest httpRequest) { 
        try {
            if (userDetails == null) { return ResponseEntity.status(401).build(); }
            
            String username = userDetails.getUsername(); 
            
            NguoiDung nguoiDung = nguoiDungRepository.findByTenDangNhap(username)
                .orElseThrow(() -> new Exception("Không tìm thấy người dùng: " + username));
            
            Long maNguoiDung = nguoiDung.getMaNguoiDung();
            
            PaymentResponse response = paymentService.createVnPayLink(maNguoiDung, request, httpRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            PaymentResponse errorResponse = new PaymentResponse();
            errorResponse.setDescription("Lỗi tạo thanh toán VNPAY: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @GetMapping("/vnpay_return")
    public ResponseEntity<Void> handleVnPayReturn(HttpServletRequest request) {
        Map<String, String> result = paymentService.processVnPayTransaction(request);
        String orderCode = result.getOrDefault("TxnRef", "0");
        String rspCode = result.getOrDefault("RspCode", "99");
        
        StringBuilder redirectUrlBuilder = new StringBuilder(frontendUrl);
        
        if ("00".equals(rspCode)) {
            redirectUrlBuilder.append("/payment/success?orderCode=").append(orderCode);
        } else {
            redirectUrlBuilder.append("/payment/cancel?orderCode=").append(orderCode)
                              .append("&rspCode=").append(rspCode);
        }

        String redirectUrl = redirectUrlBuilder.toString();
        
        return ResponseEntity.status(302) 
               .header("Location", redirectUrl)
               .build();
    }
    
    @GetMapping("/vnpay_ipn")
    public ResponseEntity<Map<String, String>> handleVnPayIpn(HttpServletRequest request) {
        Map<String, String> result = paymentService.processVnPayTransaction(request);
        Map<String, String> response = Map.of(
            "RspCode", result.getOrDefault("RspCode", "99"), 
            "Message", result.getOrDefault("Message", "Unknown Error")
        );
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/status/{orderCode}")
    @PreAuthorize("hasAnyRole('USER', 'MANAGER')")
    public ResponseEntity<String> getPaymentStatus(@PathVariable String orderCode) {
        String status = paymentService.getOrderStatus(orderCode);
        return ResponseEntity.ok(status);
    }
}