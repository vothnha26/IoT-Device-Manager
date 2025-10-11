package com.iot.management.controller.api.admin;

import com.iot.management.model.dto.request.GoiCuocRequest;
import com.iot.management.model.entity.GoiCuoc;
import com.iot.management.service.GoiCuocService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/packages")
// @PreAuthorize("hasAuthority('MANAGER')") // Tạm thời tắt để test
public class AdminPackageController {

    private final GoiCuocService goiCuocService;

    public AdminPackageController(GoiCuocService goiCuocService) {
        this.goiCuocService = goiCuocService;
    }

    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        return ResponseEntity.ok("Admin Package Controller is working! 🎉");
    }

    @GetMapping
    public ResponseEntity<List<GoiCuoc>> getAllPackages() {
        return ResponseEntity.ok(goiCuocService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GoiCuoc> getPackageById(@PathVariable Integer id) {
        return goiCuocService.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<GoiCuoc> createPackage(@RequestBody GoiCuocRequest request) {
        try {
            System.out.println("📨 Received request: " + request.getTenGoi());
            GoiCuoc savedGoiCuoc = goiCuocService.save(request);
            System.out.println("✅ Package created successfully: " + savedGoiCuoc.getTenGoi());
            return new ResponseEntity<>(savedGoiCuoc, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            // Duplicate name
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            System.err.println("❌ Error creating package: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<GoiCuoc> updatePackage(@PathVariable Integer id, @RequestBody GoiCuocRequest request) {
        try {
            GoiCuoc updatedGoiCuoc = goiCuocService.update(id, request);
            return ResponseEntity.ok(updatedGoiCuoc);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePackage(@PathVariable Integer id) {
        try {
            goiCuocService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}