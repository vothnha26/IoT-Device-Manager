package com.iot.management.controller.api.device;

import com.iot.management.controller.api.CommandRequest;
import com.iot.management.model.dto.device.TelemetryMessage;
import com.iot.management.model.entity.LenhDieuKhien;
import com.iot.management.model.entity.NguoiDung;
import com.iot.management.model.entity.ThietBi;
import com.iot.management.repository.LenhDieuKhienRepository;
import com.iot.management.repository.NguoiDungRepository;
import com.iot.management.service.ThietBiService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/commands")
public class LenhDieuKhienController {

    private final ThietBiService thietBiService;
    private final LenhDieuKhienRepository lenhDieuKhienRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public LenhDieuKhienController(
            ThietBiService thietBiService,
            LenhDieuKhienRepository lenhDieuKhienRepository,
            NguoiDungRepository nguoiDungRepository,
            SimpMessagingTemplate messagingTemplate) {
        this.thietBiService = thietBiService;
        this.lenhDieuKhienRepository = lenhDieuKhienRepository;
        this.nguoiDungRepository = nguoiDungRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendCommand(@RequestBody CommandRequest request, Authentication authentication) {
        try {
            // Validate request
            if (request.getMaThietBi() == null) {
                return ResponseEntity.badRequest().body("maThietBi is required");
            }
            if (request.getTenLenh() == null || request.getTenLenh().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("tenLenh is required");
            }

            // Find device
            Optional<ThietBi> deviceOpt = thietBiService.findDeviceById(request.getMaThietBi());
            if (deviceOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Device not found");
            }

            ThietBi device = deviceOpt.get();

            // Get current user
            String email = authentication.getName();
            Optional<NguoiDung> userOpt = nguoiDungRepository.findByEmail(email);
            
            // Create command entity
            LenhDieuKhien command = new LenhDieuKhien();
            command.setThietBi(device);
            command.setTenLenh(request.getTenLenh());
            command.setGiaTriLenh(request.getGiaTriLenh());
            command.setTrangThai("pending");
            if (userOpt.isPresent()) {
                command.setNguoiGui(userOpt.get());
            }

            // Save command
            LenhDieuKhien savedCommand = lenhDieuKhienRepository.save(command);

            // Send command via WebSocket to device
            TelemetryMessage message = new TelemetryMessage();
            message.setDeviceId(device.getMaThietBi());
            message.setRoomId(device.getKhuVuc() != null ? device.getKhuVuc().getMaKhuVuc() : null);
            message.setType(request.getTenLenh());
            message.setState(request.getGiaTriLenh());
            message.setTimestamp(java.time.Instant.now());

            // Send to device-specific channel
            messagingTemplate.convertAndSend("/topic/device/" + device.getMaThietBi(), message);

            // Return success response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Command sent successfully");
            response.put("commandId", savedCommand.getMaLenh());
            
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to send command: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/device/{deviceId}")
    public ResponseEntity<?> getCommandHistory(@PathVariable Long deviceId) {
        try {
            Optional<ThietBi> deviceOpt = thietBiService.findDeviceById(deviceId);
            if (deviceOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Device not found");
            }

            // Get recent commands for this device (limit to last 50)
            var commands = lenhDieuKhienRepository.findTop50ByThietBi_MaThietBiOrderByNgayTaoDesc(deviceId);
            
            return ResponseEntity.ok(commands);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to get command history: " + e.getMessage());
        }
    }
}
