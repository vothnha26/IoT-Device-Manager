package com.iot.management.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iot.management.model.dto.payment.PaymentRequest;
import com.iot.management.model.dto.payment.PaymentResponse;
import com.iot.management.model.entity.DangKyGoi;
import com.iot.management.model.entity.GoiCuoc;
import com.iot.management.model.entity.NguoiDung;
import com.iot.management.model.entity.ThanhToan;
import com.iot.management.repository.DangKyGoiRepository;
import com.iot.management.repository.GoiCuocRepository;
import com.iot.management.repository.NguoiDungRepository;
import com.iot.management.repository.ThanhToanRepository;
import com.iot.management.service.VietQRService;

@Service
public class PaymentService {

    private final VietQRService vietQRService;
    private final NguoiDungRepository nguoiDungRepository;
    private final GoiCuocRepository goiCuocRepository;
    private final DangKyGoiRepository dangKyGoiRepository;
    private final ThanhToanRepository thanhToanRepository;

    public PaymentService(VietQRService vietQRService, NguoiDungRepository nguoiDungRepository,
            GoiCuocRepository goiCuocRepository,
            DangKyGoiRepository dangKyGoiRepository,
            ThanhToanRepository thanhToanRepository) {
        this.vietQRService = vietQRService;
        this.nguoiDungRepository = nguoiDungRepository;
        this.goiCuocRepository = goiCuocRepository;
        this.dangKyGoiRepository = dangKyGoiRepository;
        this.thanhToanRepository = thanhToanRepository;
    }

    @Transactional
    public PaymentResponse createVnPayLink(Long maNguoiDung, PaymentRequest request) throws Exception {
        // Legacy method name retained for compatibility. Now implemented with VietQR
        // flow.
        GoiCuoc goiCuoc = goiCuocRepository.findById(request.getMaGoiCuoc())
                .orElseThrow(() -> new Exception("Không tìm thấy gói cước."));

        NguoiDung nguoiDung = nguoiDungRepository.findById(maNguoiDung)
                .orElseThrow(() -> new Exception("Không tìm thấy người dùng."));

        BigDecimal amount = goiCuoc.getGiaTien();

        // Tạo DangKyGoi tạm với trạng thái PENDING (để thỏa constraint NOT NULL)
        DangKyGoi dangKyGoi = new DangKyGoi();
        dangKyGoi.setNguoiDung(nguoiDung);
        dangKyGoi.setGoiCuoc(goiCuoc);
        dangKyGoi.setNgayBatDau(LocalDateTime.now());
        dangKyGoi.setTrangThai("PENDING"); // Sẽ chuyển thành ACTIVE khi thanh toán thành công
        dangKyGoi = dangKyGoiRepository.save(dangKyGoi);

        // Tạo ThanhToan liên kết với DangKyGoi
        ThanhToan thanhToan = new ThanhToan();
        thanhToan.setDangKyGoi(dangKyGoi);
        thanhToan.setMaNguoiDung(nguoiDung.getMaNguoiDung());
        thanhToan.setMaGoiCuoc(goiCuoc.getMaGoiCuoc().longValue());
        thanhToan.setSoTien(amount);
        thanhToan.setPhuongThuc("QR");
        thanhToan.setTrangThai("PENDING");
        thanhToanRepository.save(thanhToan);

        // Use VietQR service to generate QR image URL
        String description = "Thanh toan goi " + goiCuoc.getTenGoi();
        String qrImageUrl = vietQRService.generateQRCode(
                thanhToan.getMaThanhToan() != null ? thanhToan.getMaThanhToan().intValue() : 0,
                amount, description);

        PaymentResponse response = new PaymentResponse();
        response.setOrderCode(String.valueOf(thanhToan.getMaThanhToan()));
        response.setPaymentUrl(qrImageUrl);
        response.setDescription(description);
        response.setAmount(amount.longValue());

        return response;
    }

    // VNPAY IPN/return processing removed. SePay/VietQR webhooks should be
    // implemented separately.

    public String getOrderStatus(String orderCode) {
        return thanhToanRepository.findByMaGiaoDichCongThanhToan(orderCode)
                .map(ThanhToan::getTrangThai)
                .orElse("NOT_FOUND");
    }
}