package com.iot.management.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
import org.springframework.transaction.annotation.Transactional;

import com.iot.management.config.SePayProperties;
import com.iot.management.model.entity.DangKyGoi;
import com.iot.management.model.entity.ThanhToan;
import com.iot.management.repository.DangKyGoiRepository;
import com.iot.management.repository.ThanhToanRepository;
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
            @SuppressWarnings({ "unchecked", "rawtypes" })
            ResponseEntity<Map<String, Object>> raw = restTemplate.postForEntity(endpoint, req, (Class) Map.class);
            Map<String, Object> body = raw != null ? raw.getBody() : null;
            if (raw != null && raw.getStatusCode().is2xxSuccessful() && body != null) {
                Object url = body.get("checkout_url");
                if (url == null)
                    url = body.get("payUrl");
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
    @Transactional
    public void handleWebhook(Map<String, Object> payload, String signatureHeader) {
        log.info("========== SePay webhook START ==========");
        log.info("Full payload: {}", payload);
        log.info("Signature: {}", signatureHeader);
        try {
            String content = Objects.toString(payload.get("content"), "");
            log.info("Extracted content: '{}'", content);

            Integer orderId = extractOrderIdFromContent(content);
            log.info("Extracted orderId: {}", orderId);

            if (orderId == null) {
                log.warn("Could not extract order ID from content: {}", content);
                return;
            }

            Object transferAmountObj = payload.get("transferAmount");
            BigDecimal transferAmount = null;
            if (transferAmountObj instanceof Number) {
                transferAmount = BigDecimal.valueOf(((Number) transferAmountObj).doubleValue());
            }
            log.info("Transfer amount: {}", transferAmount);

            // orderId là maThanhToan, không phải maDangKy
            log.info("Looking for ThanhToan with ID: {}", orderId);
            Optional<ThanhToan> thanhToanOpt = thanhToanRepository.findById(orderId.longValue());

            if (!thanhToanOpt.isPresent()) {
                log.error("❌ ThanhToan not found with ID: {}", orderId);
                return;
            }

            if (thanhToanOpt.isPresent()) {
                ThanhToan thanhToan = thanhToanOpt.get();
                log.info("✅ Found ThanhToan: ID={}, CurrentStatus={}, Amount={}",
                        thanhToan.getMaThanhToan(), thanhToan.getTrangThai(), thanhToan.getSoTien());

                // Kiểm tra xem đã thanh toán chưa
                if ("DA_THANH_TOAN".equals(thanhToan.getTrangThai())) {
                    log.warn("⚠️ ThanhToan {} already paid, skipping", orderId);
                    return;
                }

                DangKyGoi dk = thanhToan.getDangKyGoi();

                if (dk == null) {
                    log.error("❌ ThanhToan {} has no associated DangKyGoi", orderId);
                    return;
                }

                log.info("✅ Found DangKyGoi: ID={}, CurrentStatus={}", dk.getMaDangKy(), dk.getTrangThai());

                // Cập nhật thông tin thanh toán
                log.info("🔄 Updating ThanhToan from {} to DA_THANH_TOAN", thanhToan.getTrangThai());
                thanhToan.setSoTien(transferAmount != null ? transferAmount : thanhToan.getSoTien());
                thanhToan.setNgayThanhToan(LocalDateTime.now());
                thanhToan.setPhuongThuc("QR Transfer - SePay");

                // map SePay referenceCode or id to maGiaoDichCongThanhToan
                String ref = Objects.toString(payload.get("referenceCode"), null);
                if (ref == null)
                    ref = Objects.toString(payload.get("id"), null);
                thanhToan.setMaGiaoDichCongThanhToan(ref);
                thanhToan.setTrangThai("DA_THANH_TOAN");

                log.info("💾 Saving ThanhToan with new status: DA_THANH_TOAN");
                thanhToanRepository.save(thanhToan);
                log.info("✅ ThanhToan saved successfully");

                // EXPIRE tất cả gói ACTIVE cũ của user trước khi kích hoạt gói mới
                Long userId = dk.getNguoiDung().getMaNguoiDung();
                log.info("🔍 Checking for existing ACTIVE packages for user {}", userId);

                List<DangKyGoi> activePackages = dangKyGoiRepository
                        .findByTrangThai(DangKyGoi.TRANG_THAI_ACTIVE)
                        .stream()
                        .filter(d -> d.getNguoiDung().getMaNguoiDung().equals(userId))
                        .filter(d -> !d.getMaDangKy().equals(dk.getMaDangKy())) // Không expire gói hiện tại
                        .collect(java.util.stream.Collectors.toList());

                if (!activePackages.isEmpty()) {
                    log.info("⚠️ Found {} existing ACTIVE package(s) for user {}, expiring them...",
                            activePackages.size(), userId);

                    for (DangKyGoi oldPackage : activePackages) {
                        log.info("🔄 Expiring old package: ID={}, Package={}",
                                oldPackage.getMaDangKy(),
                                oldPackage.getGoiCuoc().getTenGoi());
                        oldPackage.setTrangThai("EXPIRED");
                        oldPackage.setNgayKetThuc(LocalDateTime.now()); // Set end date to now
                        dangKyGoiRepository.save(oldPackage);
                    }
                    log.info("✅ Expired {} old package(s)", activePackages.size());
                }

                // Kích hoạt gói cước mới: Update DangKyGoi từ PENDING → ACTIVE và set ngày kết
                // thúc
                log.info("🔄 Activating new package: DangKyGoi {} from {} to ACTIVE", dk.getMaDangKy(),
                        dk.getTrangThai());
                dk.setTrangThai(DangKyGoi.TRANG_THAI_ACTIVE);
                dk.setNgayBatDau(LocalDateTime.now());
                dk.setNgayKetThuc(LocalDateTime.now().plusDays(30)); // 30 ngày
                dangKyGoiRepository.save(dk);
                log.info("✅ DangKyGoi activated successfully");

                log.info("🎉 Payment {} completed, Package {} activated",
                        orderId, dk.getMaDangKy());

                // Notify via websocket (topic: /topic/payment/{orderId})
                try {
                    Map<String, Object> notificationPayload = Map.of(
                            "type", "PAYMENT_SUCCESS",
                            "orderId", orderId,
                            "amount", transferAmount,
                            "userId", dk.getNguoiDung() != null ? dk.getNguoiDung().getMaNguoiDung() : null);

                    // per-order topic (for clients subscribed to this order)
                    messagingTemplate.convertAndSend("/topic/payment/" + orderId, notificationPayload);

                    // broadcast to admin topic (admin UI should subscribe to this)
                    messagingTemplate.convertAndSend("/topic/admin/payments", notificationPayload);

                    // Optionally send direct user email if email exists
                    if (dk.getNguoiDung() != null && dk.getNguoiDung().getEmail() != null) {
                        String to = dk.getNguoiDung().getEmail();
                        String subject = "[IoT-Manager] Thanh toan thanh cong cho don " + orderId;
                        String text = "Don hang #" + orderId + " da duoc thanh toan. So tien: "
                                + (transferAmount != null ? transferAmount.toPlainString() : "0") + "\nMa tham chieu: "
                                + payload.get("referenceCode");
                        try {
                            emailService.sendSimpleEmail(to, subject, text);
                        } catch (Exception ex) {
                            log.error("Failed to send payment email to user {}: {}", to, ex.getMessage());
                        }
                    }
                } catch (Exception e) {
                    log.error("Error sending WebSocket/Email notification for order {}: {}", orderId, e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("❌ Error handling SePay webhook: {}", e.getMessage(), e);
            e.printStackTrace();
        }
        log.info("========== SePay webhook END ==========");
    }

    private Integer extractOrderIdFromContent(String content) {
        log.info("🔍 Extracting orderId from content: '{}'", content);
        if (content == null || content.isEmpty()) {
            log.warn("Content is null or empty");
            return null;
        }
        try {
            // Support both "SEVQR6 ..." and "SEVQR 6 ..." formats
            Pattern p = Pattern.compile("SEVQR\\s*(\\d+)", Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(content);
            if (m.find()) {
                String orderStr = m.group(1);
                log.info("Order string extracted via regex: '{}'", orderStr);
                Integer result = Integer.parseInt(orderStr);
                log.info("✅ Parsed orderId: {}", result);
                return result;
            }
            log.warn("Content does not match 'SEVQR<id>' pattern: '{}'", content);
        } catch (Exception e) {
            log.error("❌ Error extracting order ID from content: '{}', error: {}", content, e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private String buildCallbackUrl() {
        String base = props.getPublicBaseUrl();
        String path = props.getCallbackPath();
        if (base == null)
            base = "";
        if (path == null)
            path = "/api/payments/webhook";
        return base.replaceAll("/+$", "") + path;
    }
}
