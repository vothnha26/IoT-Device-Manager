package com.iot.management.controller.api.test;

import java.time.Instant;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.iot.management.model.dto.device.TelemetryMessage;
import com.iot.management.service.TelemetryPublisher;

@RestController
@RequestMapping("/api/test")
public class FakeTelemetryController {

    private final TelemetryPublisher publisher;

    public FakeTelemetryController(TelemetryPublisher publisher) {
        this.publisher = publisher;
    }

    @PostMapping("/fake-telemetry")
    public ResponseEntity<String> sendFake(
            @RequestParam(required = false) Long deviceId,
            @RequestParam(required = false) Long roomId,
            @RequestParam String type,
            @RequestParam(required = false) Double value,
            @RequestParam(required = false) String state
    ) {
        TelemetryMessage m = new TelemetryMessage(
                deviceId, roomId, Instant.now(), type, value, state
        );
        publisher.publish(m);
        return ResponseEntity.ok("published");
    }
}
