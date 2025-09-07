package com.iot.management.controller.api;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Đảm bảo đã import
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/public")
    public ResponseEntity<String> publicAccess() {
        return ResponseEntity.ok("This is a public endpoint.");
    }

    @GetMapping("/authenticated")
    public ResponseEntity<String> authenticatedAccess() {
        return ResponseEntity.ok("This is an authenticated endpoint.");
    }

    @GetMapping("/admin")
    // Dùng @PreAuthorize với hasAuthority thay vì hasRole
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> adminAccess() {
        return ResponseEntity.ok("This is an ADMIN endpoint.");
    }
}