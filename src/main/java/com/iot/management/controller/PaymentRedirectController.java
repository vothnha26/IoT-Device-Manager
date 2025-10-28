package com.iot.management.controller;

import java.math.BigDecimal;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

import com.iot.management.model.entity.DangKyGoi;
import com.iot.management.model.repository.DangKyGoiRepository;
import com.iot.management.service.SePayService;

@Controller
@RequestMapping("/payment")
public class PaymentRedirectController {

    private static final Logger log = LoggerFactory.getLogger(PaymentRedirectController.class);

    private final DangKyGoiRepository dangKyGoiRepository;
    private final SePayService sePayService;

    public PaymentRedirectController(DangKyGoiRepository dangKyGoiRepository, SePayService sePayService) {
        this.dangKyGoiRepository = dangKyGoiRepository;
        this.sePayService = sePayService;
    }

    /**
     * Redirect helper that generates SePay checkout URL for an existing order and redirects the browser to it.
     * Usage: GET /payment/redirect/{orderId}
     */
    @GetMapping("/redirect/{orderId}")
    public RedirectView redirectToCheckout(@PathVariable("orderId") Long orderId) {
        try {
            Optional<DangKyGoi> opt = dangKyGoiRepository.findById(orderId);
            if (opt.isEmpty()) {
                log.warn("Redirect requested for unknown order {}", orderId);
                RedirectView rv = new RedirectView("/error");
                rv.setExposeModelAttributes(false);
                return rv;
            }

            DangKyGoi dk = opt.get();
            BigDecimal amount = dk.getGoiCuoc() != null ? dk.getGoiCuoc().getGiaTien() : BigDecimal.ZERO;
            String description = "Thanh toan #" + orderId;

            String checkoutUrl = sePayService.createPaymentUrl(orderId.intValue(), amount, description);
            if (checkoutUrl == null || checkoutUrl.isBlank()) {
                log.error("SePay returned null checkoutUrl for order {}", orderId);
                RedirectView rv = new RedirectView("/payment/return?order_id=" + orderId + "&status=error");
                rv.setExposeModelAttributes(false);
                return rv;
            }

            log.info("Redirecting order {} to SePay checkout: {}", orderId, checkoutUrl);
            RedirectView rv = new RedirectView(checkoutUrl);
            rv.setExposeModelAttributes(false);
            return rv;
        } catch (Exception ex) {
            log.error("Error redirecting to checkout for order {}: {}", orderId, ex.getMessage(), ex);
            RedirectView rv = new RedirectView("/payment/return?order_id=" + orderId + "&status=error");
            rv.setExposeModelAttributes(false);
            return rv;
        }
    }
}
