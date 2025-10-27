package com.iot.management.controller.ui;

import com.iot.management.config.VnpayConfig;
import com.iot.management.model.entity.DangKyGoi;
import com.iot.management.model.entity.GoiCuoc;
import com.iot.management.model.entity.NguoiDung;
import com.iot.management.model.entity.ThanhToan;
import com.iot.management.service.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequestMapping("/payment")
public class PaymentController {

    private final VnpayConfig vnpayConfig;
    private final ThanhToanService thanhToanService;
    private final GoiCuocService goiCuocService;
    private final NguoiDungService nguoiDungService;
    private final DangKyGoiService dangKyGoiService;
    private final HttpServletRequest request;

    public PaymentController(VnpayConfig vnpayConfig,
                             ThanhToanService thanhToanService,
                             GoiCuocService goiCuocService,
                             NguoiDungService nguoiDungService,
                             DangKyGoiService dangKyGoiService,
                             HttpServletRequest request) {
        this.vnpayConfig = vnpayConfig;
        this.thanhToanService = thanhToanService;
        this.goiCuocService = goiCuocService;
        this.nguoiDungService = nguoiDungService;
        this.dangKyGoiService = dangKyGoiService;
        this.request = request;
    }

    // ================= TẠO THANH TOÁN =================
    @PostMapping("/create-payment/{maGoiCuoc}")
    public String createPayment(@PathVariable Integer maGoiCuoc,
                                @AuthenticationPrincipal UserDetails userDetails) throws Exception {

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
        thanhToan.setPhuongThuc("VNPAY");
        thanhToan.setTrangThai("CHO_THANH_TOAN");
        thanhToan = thanhToanService.save(thanhToan);

        String vnpayUrl = createVnpayUrl(thanhToan);
        return "redirect:" + vnpayUrl;
    }

    // ================= VNPAY RETURN =================
    @GetMapping("/vnpay-return")
    public String vnpayReturn(@RequestParam Map<String, String> queryParams,
                            RedirectAttributes redirectAttributes) {

        String vnpResponseCode = queryParams.get("vnp_ResponseCode");
        String transactionId = queryParams.get("vnp_TxnRef");

        // --- Kiểm tra chữ ký ---
        String vnpSecureHash = queryParams.get("vnp_SecureHash");
        // Tạo một bản sao của map để không làm thay đổi map gốc khi remove
        Map<String, String> paramsToVerify = new HashMap<>(queryParams);
        if (!verifyReturnSignature(paramsToVerify, vnpSecureHash)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cảnh báo: Chữ ký VNPay không hợp lệ!");
            // TODO: Ghi log về việc chữ ký không hợp lệ để điều tra
            System.err.println("WARNING: Invalid VNPAY Return Signature for TxnRef: " + transactionId);
            // Vẫn nên tìm giao dịch để cập nhật trạng thái thất bại nếu có thể
             ThanhToan thanhToan = thanhToanService.findByMaGiaoDichCongThanhToan(transactionId).orElse(null);
             if (thanhToan != null && "CHO_THANH_TOAN".equals(thanhToan.getTrangThai())) {
                 thanhToan.setTrangThai("CHUKY_KHONGHOP lệ"); // Hoặc trạng thái lỗi khác
                 thanhToanService.save(thanhToan);
             }
            return "redirect:/user/subscriptions"; // Chuyển hướng về trang an toàn
        }

        // --- Xử lý kết quả thanh toán ---
        if ("00".equals(vnpResponseCode)) {
            ThanhToan thanhToan = thanhToanService.findByMaGiaoDichCongThanhToan(transactionId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy giao dịch với mã: " + transactionId));

            if ("CHO_THANH_TOAN".equals(thanhToan.getTrangThai())) {
                thanhToan.setTrangThai("DA_THANH_TOAN");
                // Lưu thêm thông tin nếu cần
                // thanhToan.setVnpBankCode(queryParams.get("vnp_BankCode"));
                // thanhToan.setVnpTransactionNo(queryParams.get("vnp_TransactionNo")); // Mã GD VNPAY
                // thanhToan.setVnpPayDate(queryParams.get("vnp_PayDate")); // Thời gian thanh toán
                thanhToanService.save(thanhToan);

                DangKyGoi dangKyGoi = thanhToan.getDangKyGoi();
                dangKyGoi.setTrangThai("HOAT_DONG");
                // Cập nhật ngày kết thúc dựa vào ngày thanh toán thành công
                dangKyGoi.setNgayKetThuc(LocalDateTime.now()
                        .plusDays(dangKyGoi.getGoiCuoc().getSoNgayLuuDuLieu())); // Sửa lại tên trường nếu cần
                dangKyGoiService.save(dangKyGoi);

                redirectAttributes.addFlashAttribute("successMessage",
                        "Thanh toán thành công! Gói '" + dangKyGoi.getGoiCuoc().getTenGoi() + "' đã được kích hoạt.");
            } else {
                redirectAttributes.addFlashAttribute("warningMessage",
                        "Giao dịch '" + transactionId + "' đã được xử lý trước đó (" + thanhToan.getTrangThai() + ").");
            }
        } else {
            // Thanh toán thất bại hoặc bị hủy
             ThanhToan thanhToan = thanhToanService.findByMaGiaoDichCongThanhToan(transactionId).orElse(null);
             if (thanhToan != null && "CHO_THANH_TOAN".equals(thanhToan.getTrangThai())) {
                 thanhToan.setTrangThai("THAT_BAI"); // Hoặc "HUY" tùy theo mã lỗi
                 thanhToanService.save(thanhToan);
             }
            String errorMessage = VnpayConfig.getResponseDescription(vnpResponseCode); // Lấy mô tả lỗi từ config (nếu có)
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Thanh toán thất bại! " + (errorMessage != null ? errorMessage : "Mã lỗi: " + vnpResponseCode));
        }

        return "redirect:/user/subscriptions"; // Chuyển hướng về trang quản lý gói cước
    }

    // ================= TẠO URL THANH TOÁN (ĐÃ SỬA LỖI HASH) =================
    private String createVnpayUrl(ThanhToan thanhToan) throws Exception {
        String randomStr = UUID.randomUUID().toString().substring(0, 6);
        String transactionId = thanhToan.getMaThanhToan() + "-" + System.currentTimeMillis() + "-" + randomStr;
        thanhToan.setMaGiaoDichCongThanhToan(transactionId);
        thanhToanService.save(thanhToan);
        System.out.println("DEBUG: NEW vnp_TxnRef = " + transactionId); // Log TxnRef mới

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", "2.1.0");
        vnp_Params.put("vnp_Command", "pay");
        vnp_Params.put("vnp_TmnCode", vnpayConfig.getTmnCode());
        // Lấy lại giá trị số tiền từ ThanhToan đã lưu để đảm bảo tính nhất quán
        vnp_Params.put("vnp_Amount", String.valueOf(thanhToan.getSoTien().multiply(BigDecimal.valueOf(100)).longValue()));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", transactionId);
        String orderInfo = "Thanh toan goi cuoc IoT: " + thanhToan.getDangKyGoi().getGoiCuoc().getTenGoi();
        orderInfo = orderInfo.length() > 255 ? orderInfo.substring(0, 250) + "..." : orderInfo;
        orderInfo = orderInfo.replaceAll("[^a-zA-Z0-9\\s\\-_]", ""); // Chỉ giữ lại ký tự an toàn
        vnp_Params.put("vnp_OrderInfo", orderInfo);
        vnp_Params.put("vnp_OrderType", "other"); // Hoặc loại phù hợp
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", vnpayConfig.getReturnUrl()); // Đảm bảo URL này public

        String vnp_IpAddr = Optional.ofNullable(request.getHeader("X-FORWARDED-FOR"))
                .filter(ip -> ip != null && !ip.isBlank() && !"unknown".equalsIgnoreCase(ip))
                .orElse(request.getRemoteAddr());
        System.out.println("DEBUG: Original IP = " + request.getRemoteAddr() + ", X-FORWARDED-FOR = " + request.getHeader("X-FORWARDED-FOR"));
        if (vnp_IpAddr == null || vnp_IpAddr.isBlank() || vnp_IpAddr.contains("127.0.0.1") || vnp_IpAddr.contains("0:0:0:0")) {
            vnp_IpAddr = "103.199.71.121 "; // IP public tạm thời khi test local
            System.out.println("DEBUG: IP address is localhost, using fallback IP: " + vnp_IpAddr);
        }
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
        System.out.println("DEBUG: Sending vnp_IpAddr = " + vnp_IpAddr); // Log IP gửi đi

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String createDate = formatter.format(cal.getTime());
        vnp_Params.put("vnp_CreateDate", createDate);

        cal.add(Calendar.MINUTE, 15); // Thêm thời gian hết hạn 15 phút
        vnp_Params.put("vnp_ExpireDate", formatter.format(cal.getTime()));

        // ======= Build Hash Data (RAW VALUES) & Query (ENCODED VALUES) =======
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder(); // Dùng giá trị GỐC
        StringBuilder query = new StringBuilder();    // Dùng giá trị ĐÃ ENCODE UTF-8

        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                // Build hashData (RAW value)
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(fieldValue); // <-- KHÔNG ENCODE

                // Build query (ENCODE value using UTF-8)
                query.append(URLEncoder.encode(fieldName, StandardCharsets.UTF_8.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString())); // <-- Dùng UTF-8

                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }

        String hash = hmacSHA512(vnpayConfig.getHashSecret(), hashData.toString());
        System.out.println("DEBUG: Raw hashData String = " + hashData.toString()); // Log hashData gốc
        System.out.println("DEBUG: Calculated secureHash = " + hash); // Log hash đã tính
        query.append("&vnp_SecureHash=").append(hash);
        System.out.println("DEBUG: Final Query URL Part = " + query.toString()); // Log query cuối

        return vnpayConfig.getUrl() + "?" + query.toString(); // Trả về URL hoàn chỉnh
    }

    // ================= HÀM HASH CHUNG =================
    private String hmacSHA512(String key, String data) throws Exception {
        Mac hmac = Mac.getInstance("HmacSHA512");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
        hmac.init(secretKeySpec);
        byte[] bytes = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    // ================= VERIFY SIGNATURE (ĐÃ SỬA LỖI HASH) =================
    private boolean verifyReturnSignature(Map<String, String> params, String inputHash) {
        try {
            String secretKey = vnpayConfig.getHashSecret();
            // KHÔNG remove khỏi map gốc, tạo map mới nếu cần
            // params.remove("vnp_SecureHashType"); // Không cần remove cái này khi hash
            // params.remove("vnp_SecureHash"); // Không cần remove cái này khi hash

            List<String> fieldNames = new ArrayList<>(params.keySet());
            // Loại bỏ 2 trường hash trước khi sắp xếp và tạo hashData
            fieldNames.remove("vnp_SecureHashType");
             fieldNames.remove("vnp_SecureHash");
            Collections.sort(fieldNames);

            StringBuilder hashData = new StringBuilder();
            Iterator<String> itr = fieldNames.iterator();
            while (itr.hasNext()) {
                String fieldName = itr.next();
                // Giá trị VNPAY trả về đã được URL Decode bởi Spring, nên đây là giá trị gốc
                String fieldValue = params.get(fieldName);
                if (fieldValue != null && !fieldValue.isEmpty()) {
                    // === SỬA Ở ĐÂY: Nối giá trị GỐC vào hashData ===
                    hashData.append(fieldName);
                    hashData.append('=');
                    hashData.append(fieldValue); // <-- KHÔNG ENCODE

                    if (itr.hasNext()) {
                        hashData.append('&');
                    }
                }
            }
            System.out.println("DEBUG (Verify): Raw hashData String = " + hashData.toString());

            String expectedHash = hmacSHA512(secretKey, hashData.toString());
            System.out.println("DEBUG (Verify): Calculated Hash = " + expectedHash);
            System.out.println("DEBUG (Verify): Input Hash        = " + inputHash);

            boolean isValid = expectedHash.equalsIgnoreCase(inputHash);
            System.out.println("DEBUG (Verify): Signature valid = " + isValid);
            return isValid;
        } catch (Exception e) {
            System.err.println("Error verifying VNPAY signature: " + e.getMessage());
            e.printStackTrace(); // In stack trace để debug lỗi
            return false;
        }
    }
}