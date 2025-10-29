package com.iot.management.controller.ui;


import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.iot.management.model.entity.GoiCuoc;
import com.iot.management.repository.DangKyGoiRepository;
import com.iot.management.model.dto.payment.PaymentRequest;
import com.iot.management.model.dto.payment.PaymentResponse;
import com.iot.management.security.SecurityUser;
import com.iot.management.service.GoiCuocService;
import com.iot.management.service.impl.PaymentService;
import com.iot.management.model.entity.DangKyGoi;
import java.time.LocalDateTime;
import com.iot.management.service.VietQRService;
import java.util.List;

@Controller
@RequestMapping("/payment")
public class PaymentController {

    private final GoiCuocService goiCuocService;
    private final PaymentService paymentService;
    private final DangKyGoiRepository dangKyGoiRepository;
    
    public PaymentController(GoiCuocService goiCuocService,
                             PaymentService paymentService,
                             VietQRService vietQRService,
                             DangKyGoiRepository dangKyGoiRepository) {
        this.goiCuocService = goiCuocService;
        this.paymentService = paymentService;
        this.dangKyGoiRepository = dangKyGoiRepository;
    }

    // ================= DANH SÁCH GÓI CƯỚC =================
    @GetMapping
    public String showPackages(Model model, Authentication authentication) {
        List<GoiCuoc> packages = goiCuocService.findAll();
        model.addAttribute("packages", packages);
        
        // Lấy thông tin gói hiện tại nếu có
        if (authentication != null && authentication.isAuthenticated()) {
            SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
            Long maNguoiDung = securityUser.getMaNguoiDung();
            var activePackageOpt = dangKyGoiRepository.findByNguoiDung_MaNguoiDungAndTrangThai(
                maNguoiDung, DangKyGoi.TRANG_THAI_ACTIVE);
            
            if (activePackageOpt.isPresent()) {
                DangKyGoi activePackage = activePackageOpt.get();
                if (activePackage.getNgayKetThuc() != null && 
                    activePackage.getNgayKetThuc().isAfter(LocalDateTime.now())) {
                    model.addAttribute("currentPackage", activePackage.getGoiCuoc());
                }
            }
        }
        
        return "payment/packages";
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

        // Nếu đã có gói ACTIVE và còn hạn thì chuyển về profile luôn
        var activeOpt = dangKyGoiRepository.findByNguoiDung_MaNguoiDungAndTrangThai(maNguoiDung, DangKyGoi.TRANG_THAI_ACTIVE);
        if (activeOpt.isPresent()) {
            DangKyGoi dk = activeOpt.get();
            if (dk.getNgayKetThuc() != null && dk.getNgayKetThuc().isAfter(LocalDateTime.now())) {
                return "redirect:/profile";
            }
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

        // Kiểm tra xem user đã có gói ACTIVE chưa
        var activeOpt = dangKyGoiRepository.findByNguoiDung_MaNguoiDungAndTrangThai(maNguoiDung, DangKyGoi.TRANG_THAI_ACTIVE);
        if (activeOpt.isPresent()) {
            DangKyGoi currentPackage = activeOpt.get();
            // Nếu đang chọn gói giống với gói hiện tại -> về profile
            if (currentPackage.getGoiCuoc().getMaGoiCuoc().equals(maGoiCuoc)) {
                return "redirect:/profile?message=already_subscribed";
            }
            // Nếu chọn gói khác -> cho phép upgrade/downgrade (gói cũ sẽ bị expire khi thanh toán thành công)
            model.addAttribute("isUpgrade", true);
            model.addAttribute("currentPackageName", currentPackage.getGoiCuoc().getTenGoi());
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