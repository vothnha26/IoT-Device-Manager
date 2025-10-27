package com.iot.management.controller.api.test;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.iot.management.service.SimulationService;

@RestController
@RequestMapping("/api/test/sim")
public class SimulationController {

    private final SimulationService simulationService;

    public SimulationController(SimulationService simulationService) {
        this.simulationService = simulationService;
    }

    @PostMapping("/start")
    public ResponseEntity<?> start(
            @RequestParam Long deviceId,
            @RequestParam Long roomId,
            @RequestParam(defaultValue = "temperature") String type,
            @RequestParam(defaultValue = "5") double ratePerSec,
            @RequestParam(defaultValue = "0") long durationSeconds
    ) {
        String id = simulationService.startSimulation(deviceId, roomId, type, ratePerSec, durationSeconds);
        return ResponseEntity.ok(Map.of("simulationId", id));
    }

    @PostMapping("/stop")
    public ResponseEntity<?> stop(@RequestParam String id) {
        boolean ok = simulationService.stopSimulation(id);
        return ResponseEntity.ok(Map.of("stopped", ok));
    }

    @GetMapping("/list")
    public ResponseEntity<?> list() {
        Map<String, ?> tasks = simulationService.listSimulations();
        return ResponseEntity.ok(Map.of("count", tasks.size(), "ids", tasks.keySet()));
    }
}
