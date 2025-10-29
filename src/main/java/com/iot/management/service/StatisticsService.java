package com.iot.management.service;

import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.cache.annotation.CacheEvict;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class StatisticsService {
    
    // Cache keys
    private static final String ACTIVITY_CACHE = "activityStats";
    private static final String ALERT_CACHE = "alertStats";
    private static final String DEVICE_CACHE = "deviceStats";
    
    @Cacheable(ACTIVITY_CACHE)
    public Map<String, Object> getActivityStatistics(String timeRange) {
        Map<String, Object> stats = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        List<String> labels = new ArrayList<>();
        List<Integer> data = new ArrayList<>();
        
        switch (timeRange) {
            case "day":
                // 24 hours data points
                for (int i = 23; i >= 0; i--) {
                    labels.add(now.minusHours(i).format(DateTimeFormatter.ofPattern("HH:mm")));
                    data.add(generateRandomData(20, 100)); // Replace with real data
                }
                break;
            case "week":
                // 7 days data points
                for (int i = 6; i >= 0; i--) {
                    labels.add(now.minusDays(i).format(DateTimeFormatter.ofPattern("dd/MM")));
                    data.add(generateRandomData(100, 500));
                }
                break;
            case "month":
                // 30 days data points
                for (int i = 29; i >= 0; i--) {
                    labels.add(now.minusDays(i).format(DateTimeFormatter.ofPattern("dd/MM")));
                    data.add(generateRandomData(300, 1500));
                }
                break;
            case "year":
                // 12 months data points
                for (int i = 11; i >= 0; i--) {
                    labels.add(now.minusMonths(i).format(DateTimeFormatter.ofPattern("MM/yyyy")));
                    data.add(generateRandomData(1000, 5000));
                }
                break;
        }
        
        stats.put("labels", labels);
        stats.put("data", data);
        stats.put("totalActivities", data.stream().mapToInt(Integer::intValue).sum());
        stats.put("avgResponseTime", String.format("%.1fs", generateRandomData(1, 5) / 10.0));
        return stats;
    }

    @Cacheable(ALERT_CACHE)
    public Map<String, Object> getAlertStatistics(String timeRange) {
        Map<String, Object> stats = new HashMap<>();
        
        // Alert distribution data
        List<Integer> distribution = Arrays.asList(
            generateRandomData(5, 20),  // Critical
            generateRandomData(10, 30), // Warning
            generateRandomData(20, 50)  // Info
        );
        
        stats.put("distribution", distribution);
        stats.put("totalAlerts", distribution.stream().mapToInt(Integer::intValue).sum());
        return stats;
    }

    @Cacheable(DEVICE_CACHE)
    public List<DeviceStatistics> getDeviceStatistics() {
        // Simulate fetching device statistics
        return Arrays.asList(
            createDeviceStat("Cảm biến nhiệt độ 1", true),
            createDeviceStat("Cảm biến độ ẩm 1", true),
            createDeviceStat("Cảm biến ánh sáng 1", false),
            createDeviceStat("Thiết bị điều khiển 1", true),
            createDeviceStat("Cảm biến khói 1", true)
        );
    }

    // Clear cache every hour
    @Scheduled(cron = "0 0 * * * *")
    @CacheEvict(value = {ACTIVITY_CACHE, ALERT_CACHE, DEVICE_CACHE}, allEntries = true)
    public void clearCache() {
        // This method will clear all caches automatically
    }

    private DeviceStatistics createDeviceStat(String name, boolean active) {
        return new DeviceStatistics(
            name,
            active,
            generateRandomData(95, 100) + "." + generateRandomData(0, 9) + "%",
            generateRandomData(500, 2000),
            generateRandomData(80, 200) + "ms"
        );
    }

    private int generateRandomData(int min, int max) {
        return new Random().nextInt(max - min + 1) + min;
    }

    // Inner class for device statistics
    public static class DeviceStatistics {
        private String name;
        private boolean active;
        private String uptime;
        private int dataPoints;
        private String avgLatency;

        public DeviceStatistics(String name, boolean active, String uptime, int dataPoints, String avgLatency) {
            this.name = name;
            this.active = active;
            this.uptime = uptime;
            this.dataPoints = dataPoints;
            this.avgLatency = avgLatency;
        }

        // Getters
        public String getName() { return name; }
        public boolean isActive() { return active; }
        public String getUptime() { return uptime; }
        public int getDataPoints() { return dataPoints; }
        public String getAvgLatency() { return avgLatency; }
    }
}