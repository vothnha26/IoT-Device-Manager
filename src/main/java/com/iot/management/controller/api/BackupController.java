package com.iot.management.controller.api;

import com.iot.management.service.BackupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/backup")
public class BackupController {
    
    private static final Logger logger = LoggerFactory.getLogger(BackupController.class);
    private final BackupService backupService;

    public BackupController(BackupService backupService) {
        this.backupService = backupService;
    }

    /**
     * Export dữ liệu dự án của user ra file JSON để download
     */
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportData(Authentication authentication) {
        try {
            if (authentication == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            String email = authentication.getName();
            logger.info("User {} requesting data export", email);

            // Generate JSON
            String jsonData = backupService.exportUserData(email);

            // Tạo tên file với timestamp
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filename = "backup_du_an_" + timestamp + ".json";

            // Setup headers để download file
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setContentDispositionFormData("attachment", filename);
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            byte[] bytes = jsonData.getBytes(StandardCharsets.UTF_8);
            
            logger.info("Export successful for user {}, file size: {} bytes", email, bytes.length);
            return new ResponseEntity<>(bytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            logger.error("Error exporting data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Import dữ liệu từ file backup
     */
    @PostMapping("/import")
    public ResponseEntity<Map<String, Object>> importData(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            logger.info("=== IMPORT REQUEST START ===");
            logger.info("Thread: {}", Thread.currentThread().getName());
            logger.info("Timestamp: {}", LocalDateTime.now());
            
            if (authentication == null) {
                response.put("success", false);
                response.put("message", "Unauthorized");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // Validate file
            if (file.isEmpty()) {
                response.put("success", false);
                response.put("message", "File is empty");
                return ResponseEntity.badRequest().body(response);
            }

            String filename = file.getOriginalFilename();
            if (filename == null || !filename.endsWith(".json")) {
                response.put("success", false);
                response.put("message", "File must be JSON format");
                return ResponseEntity.badRequest().body(response);
            }

            String email = authentication.getName();
            logger.info("User {} requesting data import from file: {}", email, file.getOriginalFilename());

            // Import data
            backupService.importUserData(email, file);

            response.put("success", true);
            response.put("message", "Dữ liệu đã được khôi phục thành công");
            
            logger.info("Import successful for user {}", email);
            logger.info("=== IMPORT REQUEST END ===");
            return ResponseEntity.ok(response);

        } catch (IOException e) {
            logger.error("Error reading backup file", e);
            response.put("success", false);
            response.put("message", "Lỗi đọc file: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            
        } catch (Exception e) {
            logger.error("Error importing data", e);
            response.put("success", false);
            response.put("message", "Lỗi khôi phục dữ liệu: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
