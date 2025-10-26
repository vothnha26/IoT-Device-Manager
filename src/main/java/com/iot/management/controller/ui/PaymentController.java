package com.iot.management.controller.ui;

import java.time.LocalDateTime;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.iot.management.model.entity.DangKyGoi;
import com.iot.management.model.entity.GoiCuoc;
import com.iot.management.model.entity.NguoiDung;
import com.iot.management.model.entity.ThanhToan;
import com.iot.management.service.DangKyGoiService;
import com.iot.management.service.GoiCuocService;
import com.iot.management.service.NguoiDungService;
import com.iot.management.service.ThanhToanService;
import com.iot.management.service.VietQRService;

@Controller
@RequestMapping("/payment")
public class PaymentController {

    private final VietQRService vietQRService;
    private final ThanhToanService thanhToanService;
    private final GoiCuocService goiCuocService;
    private final NguoiDungService nguoiDungService;
    private final DangKyGoiService dangKyGoiService;
    
    public PaymentController(VietQRService vietQRService,
                             ThanhToanService thanhToanService,
                             GoiCuocService goiCuocService,
                             NguoiDungService nguoiDungService,
                             DangKyGoiService dangKyGoiService) {
        this.vietQRService = vietQRService;
        this.thanhToanService = thanhToanService;
        this.goiCuocService = goiCuocService;
        this.nguoiDungService = nguoiDungService;
        this.dangKyGoiService = dangKyGoiService;
    }

    // ================= TẠO THANH TOÁN =================
    @PostMapping("/create-payment/{maGoiCuoc}")
    public String createPayment(@PathVariable Integer maGoiCuoc,
                                @AuthenticationPrincipal UserDetails userDetails) throws Exception {

    if (userDetails == null) {
        // Redirect to login if user not authenticated
        return "redirect:/auth/login";
    }

    GoiCuoc goiCuoc = goiCuocService.findById(maGoiCuoc)
        .orElseThrow(() -> new RuntimeException("Không tìm thấy gói cước"));
    NguoiDung nguoiDung = nguoiDungService.findByEmail(userDetails.getUsername())
        .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        DangKyGoi dangKyGoi = new DangKyGoi();
        dangKyGoi.setNguoiDung(nguoiDung);
        dangKyGoi.setGoiCuoc(goiCuoc);
        dangKyGoi.setNgayBatDau(LocalDateTime.now());
        dangKyGoi.setTrangThai("CHO_THANH_TOAN");
        dangKyGoi = dangKyGoiService.save(dangKyGoi);

        ThanhToan thanhToan = new ThanhToan();
        thanhToan.setDangKyGoi(dangKyGoi);
        thanhToan.setSoTien(goiCuoc.getGiaTien()); // Lấy giá gốc từ gói cước
        thanhToan.setNgayThanhToan(LocalDateTime.now());
    thanhToan.setPhuongThuc("QR");
        thanhToan.setTrangThai("CHO_THANH_TOAN");
        thanhToan = thanhToanService.save(thanhToan);

    // Generate VietQR image URL and redirect the browser to the image (will show the QR)
    String description = "Thanh toan goi " + dangKyGoi.getGoiCuoc().getTenGoi();
    String qrImageUrl = vietQRService.generateQRCode(thanhToan.getMaThanhToan() != null ? thanhToan.getMaThanhToan().intValue() : 0,
        thanhToan.getSoTien(), description);
    return "redirect:" + qrImageUrl;
    }

    @GetMapping("/create-payment/{maGoiCuoc}")
    public String showCheckout(@PathVariable Integer maGoiCuoc,
                               @AuthenticationPrincipal UserDetails userDetails,
                               Model model) throws Exception {
        GoiCuoc goiCuoc = goiCuocService.findById(maGoiCuoc)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy gói cước"));

        model.addAttribute("goiCuoc", goiCuoc);
        if (userDetails != null) {
            model.addAttribute("username", userDetails.getUsername());
        }
        return "payment/checkout";
    }

    // VNPAY return/ipn removed. Use SePay webhook endpoint or admin reconciliation instead.

    // VNPAY helpers removed
}