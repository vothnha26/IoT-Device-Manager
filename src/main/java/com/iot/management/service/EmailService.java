package com.iot.management.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final String fromAddress;

    public EmailService(JavaMailSender mailSender, @Value("${spring.mail.username}") String fromAddress) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
    }

    public void sendVerificationCode(String toEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        // Use configured mail username as From address so emails come from the account in .env
        message.setFrom(fromAddress);
        message.setTo(toEmail);
        message.setSubject("[IoT-Manager] Mã xác thực");
        message.setText("Mã xác thực/OTP của bạn là: " + code + "\nMã có hiệu lực trong 15 phút.");
        mailSender.send(message);
    }
}