// Tệp: PaymentRequest.java
package com.iot.management.model.dto.payment;

import jakarta.validation.constraints.NotNull;

public class PaymentRequest {
    @NotNull(message = "Mã gói cước không được để trống")
    private Integer maGoiCuoc;
    
    private String bankCode; 

    // Constructor mặc định
    public PaymentRequest() {
    }

    // Getters và Setters
    public Integer getMaGoiCuoc() {
        return maGoiCuoc;
    }

    public void setMaGoiCuoc(Integer maGoiCuoc) {
        this.maGoiCuoc = maGoiCuoc;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }
}