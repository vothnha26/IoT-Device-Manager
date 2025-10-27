package com.iot.management.controller.api.test;

import org.springframework.http.ResponseEntity;
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

    @GetMapping("/manager")
    // @PreAuthorize("hasAuthority('MANAGER')")  // Tạm thời bỏ kiểm tra quyền
    public ResponseEntity<String> managerAccess() {
        return ResponseEntity.ok("This is a MANAGER endpoint.");
    }
}