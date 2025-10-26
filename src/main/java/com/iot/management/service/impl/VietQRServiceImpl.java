package com.iot.management.service.impl;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Service;

import com.iot.management.config.VietQRProperties;
import com.iot.management.service.VietQRService;

@Service
public class VietQRServiceImpl implements VietQRService {

    private final VietQRProperties props;

    public VietQRServiceImpl(VietQRProperties props) {
        this.props = props;
    }

    @Override
    public String generateQRCode(Integer orderId, BigDecimal amount, String description) {
        try {
            String bankBin = props.getBankBin();
            String accountNo = props.getBankNumber();
            String accountName = props.getAccountName();

            if (bankBin == null || bankBin.isEmpty() || accountNo == null || accountNo.isEmpty()) {
                throw new RuntimeException("VietQR configuration not found");
            }

            String template = "compact2";
            String sevqrDescription = "SEVQR" + (orderId != null ? orderId : "0") + " " + (description != null ? description : "Thanh toan");
            String encodedDesc = URLEncoder.encode(sevqrDescription, StandardCharsets.UTF_8.toString());
            String encodedAccountName = accountName != null ? URLEncoder.encode(accountName, StandardCharsets.UTF_8.toString()) : "";

            StringBuilder qrUrl = new StringBuilder();
            qrUrl.append("https://img.vietqr.io/image/")
                .append(bankBin).append("-")
                .append(accountNo).append("-")
                .append(template).append(".png");

            qrUrl.append("?amount=").append(amount.longValue());
            qrUrl.append("&addInfo=").append(encodedDesc);
            if (!encodedAccountName.isEmpty()) {
                qrUrl.append("&accountName=").append(encodedAccountName);
            }

            return qrUrl.toString();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Failed to generate VietQR code", e);
        }
    }
}
