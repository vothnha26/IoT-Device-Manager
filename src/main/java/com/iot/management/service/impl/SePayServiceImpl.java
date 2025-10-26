package com.iot.management.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.iot.management.config.SePayProperties;
import com.iot.management.model.entity.DangKyGoi;
import com.iot.management.model.entity.ThanhToan;
import com.iot.management.model.repository.DangKyGoiRepository;
import com.iot.management.model.repository.ThanhToanRepository;
import com.iot.management.service.SePayService;

@Service
public class SePayServiceImpl implements SePayService {

    private static final Logger log = LoggerFactory.getLogger(SePayServiceImpl.class);

    private final SePayProperties props;
    private final DangKyGoiRepository dangKyGoiRepository;
    private final ThanhToanRepository thanhToanRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final RestTemplate restTemplate = new RestTemplate();
    private final com.iot.management.service.EmailService emailService;

    public SePayServiceImpl(SePayProperties props,
                            DangKyGoiRepository dangKyGoiRepository,
                            ThanhToanRepository thanhToanRepository,
                            SimpMessagingTemplate messagingTemplate,
                            com.iot.management.service.EmailService emailService) {
        this.props = props;
        this.dangKyGoiRepository = dangKyGoiRepository;
        this.thanhToanRepository = thanhToanRepository;
        this.messagingTemplate = messagingTemplate;
        this.emailService = emailService;
    }

    @Override
    public String createPaymentUrl(Integer orderId, BigDecimal amount, String description) {
        String callbackUrl = buildCallbackUrl();
        String returnUrl = props.getReturnUrl();

        String endpoint = props.getApiBaseUrl().replaceAll("/+$", "") + "/v1/transactions/create";

    // Log the URLs used for debugging (do not log secrets)
    log.info("SePay createPaymentUrl called: orderId={}, amount={}, callbackUrl={}, returnUrl={}, endpoint={}",
        orderId, amount, callbackUrl, returnUrl, endpoint);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("X-API-KEY", props.getApiKey());

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("order_id", String.valueOf(orderId));
        form.add("amount", amount.toPlainString());
        form.add("description", description != null ? description : ("Thanh toan #" + orderId));
        form.add("callback_url", callbackUrl);
        form.add("return_url", returnUrl);

        HttpEntity<MultiValueMap<String, String>> req = new HttpEntity<>(form, headers);
        try {
            @SuppressWarnings({"unchecked", "rawtypes"})
            ResponseEntity<Map<String, Object>> raw = restTemplate.postForEntity(endpoint, req, (Class) Map.class);
            Map<String, Object> body = raw != null ? raw.getBody() : null;
            if (raw != null && raw.getStatusCode().is2xxSuccessful() && body != null) {
                Object url = body.get("checkout_url");
                if (url == null) url = body.get("payUrl");
                String checkoutUrl = Objects.toString(url, null);
                log.info("SePay createPaymentUrl response checkoutUrl={}", checkoutUrl);
                return checkoutUrl;
            }
        } catch (Exception ex) {
            log.error("SePay createPaymentUrl error: {}", ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public void handleWebhook(Map<String, Object> payload, String signatureHeader) {
        log.info("SePay webhook received: {}", payload);
        try {
            String content = Objects.toString(payload.get("content"), "");
            Integer orderId = extractOrderIdFromContent(content);
            if (orderId == null) {
                log.warn("Could not extract order ID from content: {}", content);
                return;
            }

            Object transferAmountObj = payload.get("transferAmount");
            BigDecimal transferAmount = null;
            if (transferAmountObj instanceof Number) {
                transferAmount = BigDecimal.valueOf(((Number) transferAmountObj).doubleValue());
            }

            Optional<DangKyGoi> dangKyOpt = dangKyGoiRepository.findById(orderId.longValue());
            if (dangKyOpt.isPresent()) {
                DangKyGoi dk = dangKyOpt.get();
                // Update payment status on registration
                dk.setTrangThai(DangKyGoi.TRANG_THAI_ACTIVE);
                dangKyGoiRepository.save(dk);

                ThanhToan thanhToan = new ThanhToan();
                thanhToan.setDangKyGoi(dk);
                thanhToan.setSoTien(transferAmount != null ? transferAmount : BigDecimal.ZERO);
                thanhToan.setNgayThanhToan(LocalDateTime.now());
                thanhToan.setPhuongThuc("QR Transfer - SePay");
                // map SePay referenceCode or id to maGiaoDichCongThanhToan
                String ref = Objects.toString(payload.get("referenceCode"), null);
                if (ref == null) ref = Objects.toString(payload.get("id"), null);
                thanhToan.setMaGiaoDichCongThanhToan(ref);
                thanhToan.setTrangThai("COMPLETED");
                thanhToanRepository.save(thanhToan);

                log.info("Order {} marked as PAID and transaction recorded", orderId);

                // Notify via websocket (topic: /topic/payment/{orderId})
                try {
            Map<String, Object> notificationPayload = Map.of(
                "type", "PAYMENT_SUCCESS",
                "orderId", orderId,
                "amount", transferAmount,
                "userId", dk.getNguoiDung() != null ? dk.getNguoiDung().getMaNguoiDung() : null
            );

                    // per-order topic (for clients subscribed to this order)
                    messagingTemplate.convertAndSend("/topic/payment/" + orderId, notificationPayload);

                    // broadcast to admin topic (admin UI should subscribe to this)
                    messagingTemplate.convertAndSend("/topic/admin/payments", notificationPayload);

                    // Optionally send direct user email if email exists
                    if (dk.getNguoiDung() != null && dk.getNguoiDung().getEmail() != null) {
                        String to = dk.getNguoiDung().getEmail();
                        String subject = "[IoT-Manager] Thanh toan thanh cong cho don " + orderId;
                        String text = "Don hang #" + orderId + " da duoc thanh toan. So tien: " + (transferAmount != null ? transferAmount.toPlainString() : "0") + "\nMa tham chieu: " + payload.get("referenceCode");
                        try {
                            emailService.sendSimpleEmail(to, subject, text);
                        } catch (Exception ex) {
                            log.error("Failed to send payment email to user {}: {}", to, ex.getMessage());
                        }
                    }
                } catch (Exception e) {
                    log.error("Error sending WebSocket/Email notification for order {}: {}", orderId, e.getMessage());
                }
            } else {
                log.warn("Order {} not found", orderId);
            }
        } catch (Exception e) {
            log.error("Error handling SePay webhook: {}", e.getMessage(), e);
        }
    }

    private Integer extractOrderIdFromContent(String content) {
        if (content == null || content.isEmpty()) return null;
        try {
            if (content.startsWith("SEVQR")) {
                String after = content.substring(5);
                int spaceIdx = after.indexOf(' ');
                String orderStr = spaceIdx > 0 ? after.substring(0, spaceIdx) : after;
                return Integer.parseInt(orderStr);
            }
        } catch (Exception e) {
            log.error("Error extracting order ID from content: {}", content, e);
        }
        return null;
    }

    private String buildCallbackUrl() {
        String base = props.getPublicBaseUrl();
        String path = props.getCallbackPath();
        if (base == null) base = "";
        if (path == null) path = "/api/payments/webhook";
        return base.replaceAll("/+$", "") + path;
    }
}
