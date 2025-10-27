package com.iot.management.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class VietQRProperties {

    @Value("${vietqr.bankBin:}")
    private String bankBin;

    @Value("${vietqr.bankNumber:}")
    private String bankNumber;

    @Value("${vietqr.accountName:}")
    private String accountName;

    public String getBankBin() {
        return bankBin;
    }

    public String getBankNumber() {
        return bankNumber;
    }

    public String getAccountName() {
        return accountName;
    }
}
