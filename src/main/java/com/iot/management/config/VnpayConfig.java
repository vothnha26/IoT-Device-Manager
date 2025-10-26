package com.iot.management.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class VnpayConfig {

    // --- Đã sửa lại để khớp với application.properties ---
    @Value("${vnpay.tmnCode}")
    private String tmnCode;

    @Value("${vnpay.hashSecret}")
    private String hashSecret;

    @Value("${vnpay.url}")
    private String url;

    @Value("${vnpay.returnUrl}")
    private String returnUrl;

    // --- Các hằng số của VNPAY ---
    public static final String VNP_VERSION = "2.1.0";
    public static final String VNP_COMMAND_PAY = "pay";
    public static final String VNP_CURR_CODE = "VND";
    public static final String VNP_LOCALE = "vn";
    public static final String VNP_ORDER_TYPE = "other";

    // --- Getters ---
    public String getTmnCode() {
        return tmnCode;
    }

    public String getHashSecret() {
        return hashSecret;
    }

    public String getUrl() {
        return url;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    // ================== HÀM LẤY MÔ TẢ LỖI ==================
    private static final Map<String, String> VNPAY_RESPONSE_DESCRIPTIONS = new HashMap<>();

    static {
        VNPAY_RESPONSE_DESCRIPTIONS.put("00", "Giao dịch thành công");
        VNPAY_RESPONSE_DESCRIPTIONS.put("07", "Trừ tiền thành công. Giao dịch bị nghi ngờ (liên hệ VNPAY)");
        VNPAY_RESPONSE_DESCRIPTIONS.put("09", "Thẻ/Tài khoản của khách hàng chưa đăng ký dịch vụ InternetBanking tại ngân hàng.");
        VNPAY_RESPONSE_DESCRIPTIONS.put("10", "Thẻ/Tài khoản của khách hàng đã bị khóa.");
        VNPAY_RESPONSE_DESCRIPTIONS.put("11", "Giao dịch không thành công do khách hàng nhập sai mật khẩu xác thực giao dịch (OTP).");
        VNPAY_RESPONSE_DESCRIPTIONS.put("12", "Giao dịch không thành công do hết hạn sử dụng thẻ/tài khoản.");
        VNPAY_RESPONSE_DESCRIPTIONS.put("13", "Giao dịch không thành công do khách hàng nhập sai mật khẩu/");
        VNPAY_RESPONSE_DESCRIPTIONS.put("24", "Giao dịch không thành công do khách hàng hủy giao dịch.");
        VNPAY_RESPONSE_DESCRIPTIONS.put("51", "Tài khoản của khách hàng không đủ số dư để thực hiện giao dịch.");
        VNPAY_RESPONSE_DESCRIPTIONS.put("65", "Tài khoản của khách hàng đã vượt quá hạn mức giao dịch trong ngày.");
        VNPAY_RESPONSE_DESCRIPTIONS.put("75", "Ngân hàng thanh toán đang bảo trì.");
        VNPAY_RESPONSE_DESCRIPTIONS.put("79", "Giao dịch không thành công do KH nhập sai mật khẩu thanh toán quá số lần quy định.");
        VNPAY_RESPONSE_DESCRIPTIONS.put("99", "Lỗi không xác định (Vui lòng liên hệ VNPAY)");
        // Thêm các mã lỗi khác nếu cần từ tài liệu VNPAY
    }

    /**
     * Lấy mô tả tiếng Việt cho mã phản hồi của VNPAY.
     * @param responseCode Mã phản hồi từ VNPAY (ví dụ: "00", "07", "99")
     * @return Mô tả tiếng Việt hoặc null nếu không tìm thấy.
     */
    public static String getResponseDescription(String responseCode) {
        return VNPAY_RESPONSE_DESCRIPTIONS.get(responseCode);
    }
    // =========================================================

}