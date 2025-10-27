package com.iot.management.service;

import java.time.Instant;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.iot.management.dto.TelemetryMessage;

@Service
public class SimulationService {

    private final TelemetryPublisher publisher;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
    private final Map<String, ScheduledFuture<?>> tasks = new ConcurrentHashMap<>();
    private final Random random = new Random();

    public SimulationService(TelemetryPublisher publisher) {
        this.publisher = publisher;
    }

    /**
     * Start a simulation that publishes messages at approx `ratePerSec` for `durationSeconds`.
     * If durationSeconds <= 0, it runs until stopped.
     * Returns a simulation id which can be used to stop it.
     */
    public String startSimulation(Long deviceId, Long roomId, String type, double ratePerSec, long durationSeconds) {
        if (ratePerSec <= 0) ratePerSec = 1.0;
        long periodMs = (long) Math.max(1, Math.round(1000.0 / ratePerSec));

        String id = UUID.randomUUID().toString();

        Runnable task = new Runnable() {
            private long sent = 0;

            @Override
            public void run() {
                try {
                    // Build a telemetry message with slight random variation
                    double base = 20 + random.nextDouble() * 15; // base value 20..35
                    double value = Math.round((base + random.nextGaussian() * 0.5) * 100.0) / 100.0;
                    String state = null;
                    if ("switch".equalsIgnoreCase(type)) {
                        state = random.nextBoolean() ? "ON" : "OFF";
                    }

                    TelemetryMessage msg = new TelemetryMessage(deviceId, roomId, Instant.now(), type, value, state);
                    publisher.publish(msg);
                    sent++;

                    if (durationSeconds > 0) {
                        // if total time exceeded, cancel
                        // approximate by checking sent * periodMs
                        long approxMs = sent * periodMs;
                        if (approxMs >= durationSeconds * 1000L) {
                            stopSimulation(id);
                        }
                    }
                } catch (Exception e) {
                    // swallow to keep scheduler running
                    e.printStackTrace();
                }
            }
        };

        ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(task, 0, periodMs, TimeUnit.MILLISECONDS);
        tasks.put(id, future);
        return id;
    }

    public boolean stopSimulation(String id) {
        ScheduledFuture<?> f = tasks.remove(id);
        if (f != null) {
            return f.cancel(false);
        }
        return false;
    }

    public Map<String, ScheduledFuture<?>> listSimulations() {
        return tasks;
    }
}
