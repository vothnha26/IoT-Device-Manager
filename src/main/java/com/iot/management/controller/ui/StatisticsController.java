package com.iot.management.controller.ui;

import com.iot.management.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@Controller
@RequestMapping("/thong-ke")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("")
    public String index(@RequestParam(defaultValue = "day") String timeRange, Model model) {
        // Get activity statistics
        Map<String, Object> activityStats = statisticsService.getActivityStatistics(timeRange);
        model.addAttribute("activityLabels", activityStats.get("labels"));
        model.addAttribute("activityData", activityStats.get("data"));
        model.addAttribute("totalActivities", activityStats.get("totalActivities"));
        model.addAttribute("avgResponseTime", activityStats.get("avgResponseTime"));

        // Get alert statistics
        Map<String, Object> alertStats = statisticsService.getAlertStatistics(timeRange);
        model.addAttribute("alertDistribution", alertStats.get("distribution"));
        model.addAttribute("totalAlerts", alertStats.get("totalAlerts"));

        // Get device statistics
        model.addAttribute("deviceStats", statisticsService.getDeviceStatistics());
        model.addAttribute("selectedTimeRange", timeRange);

        return "thong-ke/index";
    }

    @GetMapping("/export/excel")
    @ResponseBody
    public ResponseEntity<InputStreamResource> exportExcel(
            @RequestParam(defaultValue = "day") String timeRange) {
        try {
            // Create workbook
            Workbook workbook = new XSSFWorkbook();
            
            // Create activity sheet
            Sheet activitySheet = workbook.createSheet("Hoạt động");
            Map<String, Object> activityStats = statisticsService.getActivityStatistics(timeRange);
            
            // Create headers
            Row headerRow = activitySheet.createRow(0);
            headerRow.createCell(0).setCellValue("Thời gian");
            headerRow.createCell(1).setCellValue("Số hoạt động");
            
            // Add activity data
            List<String> labels = (List<String>) activityStats.get("labels");
            List<Integer> data = (List<Integer>) activityStats.get("data");
            for (int i = 0; i < labels.size(); i++) {
                Row row = activitySheet.createRow(i + 1);
                row.createCell(0).setCellValue(labels.get(i));
                row.createCell(1).setCellValue(data.get(i));
            }
            
            // Create device statistics sheet
            Sheet deviceSheet = workbook.createSheet("Thiết bị");
            headerRow = deviceSheet.createRow(0);
            headerRow.createCell(0).setCellValue("Tên thiết bị");
            headerRow.createCell(1).setCellValue("Trạng thái");
            headerRow.createCell(2).setCellValue("Uptime");
            headerRow.createCell(3).setCellValue("Số điểm dữ liệu");
            headerRow.createCell(4).setCellValue("Độ trễ TB");
            
            // Add device data
            List<StatisticsService.DeviceStatistics> devices = statisticsService.getDeviceStatistics();
            for (int i = 0; i < devices.size(); i++) {
                Row row = deviceSheet.createRow(i + 1);
                StatisticsService.DeviceStatistics device = devices.get(i);
                row.createCell(0).setCellValue(device.getName());
                row.createCell(1).setCellValue(device.isActive() ? "Hoạt động" : "Không hoạt động");
                row.createCell(2).setCellValue(device.getUptime());
                row.createCell(3).setCellValue(device.getDataPoints());
                row.createCell(4).setCellValue(device.getAvgLatency());
            }
            
            // Write to ByteArrayOutputStream
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();
            
            // Create resource
            ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            InputStreamResource resource = new InputStreamResource(inputStream);
            
            // Set headers
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filename = "thong_ke_" + timestamp + ".xlsx";
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                    .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                    .body(resource);
                    
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}