package com.iot.management.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iot.management.config.VnpayConfig;
import com.iot.management.model.dto.payment.PaymentRequest;
import com.iot.management.model.dto.payment.PaymentResponse;
import com.iot.management.model.entity.DangKyGoi;
import com.iot.management.model.entity.GoiCuoc;
import com.iot.management.model.entity.NguoiDung;
import com.iot.management.model.entity.ThanhToan;
import com.iot.management.model.repository.DangKyGoiRepository;
import com.iot.management.model.repository.GoiCuocRepository;
import com.iot.management.model.repository.NguoiDungRepository;
import com.iot.management.model.repository.ThanhToanRepository;
import com.iot.management.util.VnPayUtil;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class PaymentService {
    
    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);
    
    private final VnpayConfig vnpayConfig;
    private final NguoiDungRepository nguoiDungRepository;
    private final GoiCuocRepository goiCuocRepository;
    private final DangKyGoiRepository dangKyGoiRepository;
    private final ThanhToanRepository thanhToanRepository;

    public PaymentService(VnpayConfig vnpayConfig, NguoiDungRepository nguoiDungRepository, 
                          GoiCuocRepository goiCuocRepository, DangKyGoiRepository dangKyGoiRepository,
                          ThanhToanRepository thanhToanRepository) {
        this.vnpayConfig = vnpayConfig;
        this.nguoiDungRepository = nguoiDungRepository;
        this.goiCuocRepository = goiCuocRepository;
        this.dangKyGoiRepository = dangKyGoiRepository;
        this.thanhToanRepository = thanhToanRepository;
    }

    @Transactional
    public PaymentResponse createVnPayLink(Long maNguoiDung, PaymentRequest request, HttpServletRequest httpRequest) throws Exception {
        
        GoiCuoc goiCuoc = goiCuocRepository.findById(request.getMaGoiCuoc())
            .orElseThrow(() -> new Exception("Không tìm thấy gói cước."));
        
        NguoiDung nguoiDung = nguoiDungRepository.findById(maNguoiDung)
            .orElseThrow(() -> new Exception("Không tìm thấy người dùng."));
        
        String vnp_TxnRef = VnPayUtil.getRandomNumber(10);
        BigDecimal amount = goiCuoc.getGiaTien();
        
        DangKyGoi dangKyGoi = new DangKyGoi();
        dangKyGoi.setNguoiDung(nguoiDung);
        dangKyGoi.setGoiCuoc(goiCuoc);
        dangKyGoi.setNgayBatDau(LocalDateTime.now());
        dangKyGoi.setTrangThai("PENDING");
        dangKyGoi = dangKyGoiRepository.save(dangKyGoi);
        
        ThanhToan thanhToan = new ThanhToan();
        thanhToan.setDangKyGoi(dangKyGoi);
        thanhToan.setSoTien(amount);
        thanhToan.setPhuongThuc("VNPAY");
        thanhToan.setMaGiaoDichCongThanhToan(vnp_TxnRef);
        thanhToan.setTrangThai("PENDING");
        thanhToanRepository.save(thanhToan);
        
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", VnpayConfig.VNP_VERSION);
        vnp_Params.put("vnp_Command", VnpayConfig.VNP_COMMAND_PAY);
        vnp_Params.put("vnp_TmnCode", vnpayConfig.getTmnCode());
        vnp_Params.put("vnp_Amount", String.valueOf(amount.longValue() * 100));
        vnp_Params.put("vnp_CurrCode", VnpayConfig.VNP_CURR_CODE);
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan goi " + goiCuoc.getTenGoi());
        vnp_Params.put("vnp_OrderType", VnpayConfig.VNP_ORDER_TYPE);
        vnp_Params.put("vnp_Locale", VnpayConfig.VNP_LOCALE);
        vnp_Params.put("vnp_ReturnUrl", vnpayConfig.getReturnUrl());
        vnp_Params.put("vnp_IpAddr", VnPayUtil.getIpAddress(httpRequest));

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        vnp_Params.put("vnp_CreateDate", formatter.format(cld.getTime()));
        
        String paymentUrl = VnPayUtil.createPaymentUrl(vnpayConfig.getUrl(), vnp_Params, vnpayConfig.getHashSecret());
        
        PaymentResponse response = new PaymentResponse();
        response.setOrderCode(vnp_TxnRef);
        response.setPaymentUrl(paymentUrl);
        response.setDescription("Thanh toan goi " + goiCuoc.getTenGoi());
        response.setAmount(amount.longValue());
        
        return response;
    }

    @Transactional
    public Map<String, String> processVnPayTransaction(HttpServletRequest request) {
        Map<String, String> response = new HashMap<>();
        try {
            Map<String, String> fields = new HashMap<>();
            for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements();) {
                String fieldName = params.nextElement();
                String fieldValue = request.getParameter(fieldName);
                if (fieldValue != null && !fieldValue.isEmpty()) {
                    fields.put(fieldName, fieldValue);
                }
            }

            String vnp_SecureHash = fields.remove("vnp_SecureHash");
            if (vnp_SecureHash == null) { throw new RuntimeException("Checksum is missing!"); }

            String signValue = VnPayUtil.hmacSHA512(vnpayConfig.getHashSecret(), VnPayUtil.createQueryStringForHash(fields));

            String txnRef = fields.getOrDefault("vnp_TxnRef", "0");
            response.put("TxnRef", txnRef);

            if (vnp_SecureHash.equals(signValue)) {
                String rspCode = fields.getOrDefault("vnp_ResponseCode", "99");
                response.put("RspCode", rspCode);

                Optional<ThanhToan> thanhToanOpt = thanhToanRepository.findByMaGiaoDichCongThanhToan(txnRef);
                if (thanhToanOpt.isEmpty()) {
                    response.put("RspCode", "94");
                    response.put("Message", "Transaction Not Found");
                    return response;
                }
                
                ThanhToan thanhToan = thanhToanOpt.get();
                if (!"PENDING".equals(thanhToan.getTrangThai())) {
                    response.put("RspCode", "02"); // Lỗi: Giao dịch đã được xử lý
                    response.put("Message", "Order already confirmed");
                    return response;
                }

                if ("00".equals(rspCode)) {
                    thanhToan.setTrangThai("SUCCESS");
                    response.put("Message", "Success");
                } else {
                    thanhToan.setTrangThai("FAILED");
                    response.put("Message", "Failed");
                }
                thanhToan.setNgayThanhToan(LocalDateTime.now());
                thanhToanRepository.save(thanhToan);
            } else {
                response.put("RspCode", "97");
                response.put("Message", "Invalid Checksum");
            }
        } catch (Exception e) {
            logger.error("Lỗi xử lý VNPAY return", e);
            response.put("RspCode", "99");
            response.put("Message", "Unknown error");
        }
        return response;
    }
    
    public String getOrderStatus(String orderCode) {
        return thanhToanRepository.findByMaGiaoDichCongThanhToan(orderCode)
            .map(ThanhToan::getTrangThai)
            .orElse("NOT_FOUND");
    }
}