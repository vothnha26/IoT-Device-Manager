package com.iot.management.controller;

import com.iot.management.model.entity.NguoiDung;
import com.iot.management.service.NguoiDungService;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
public class ControllerHelper {

    private final NguoiDungService nguoiDungService;

    public ControllerHelper(NguoiDungService nguoiDungService) {
        this.nguoiDungService = nguoiDungService;
    }

    /**
     * Lấy đối tượng NguoiDung từ Principal.
     * @param principal Đối tượng do Spring Security cung cấp.
     * @return Đối tượng NguoiDung tương ứng.
     */
    public NguoiDung getUserFromPrincipal(Principal principal) {
        String username = principal.getName();
        return nguoiDungService.findByEmail(username) // Giả sử username là email
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng: " + username));
    }

    /**
     * Lấy ID của NguoiDung từ Principal.
     * @param principal Đối tượng do Spring Security cung cấp.
     * @return ID của người dùng.
     */
    public Long getUserIdFromPrincipal(Principal principal) {
        return getUserFromPrincipal(principal).getMaNguoiDung();
    }
}