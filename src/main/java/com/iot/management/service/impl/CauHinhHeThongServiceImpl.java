package com.iot.management.service.impl;

import com.iot.management.model.entity.CauHinhHeThong;
import com.iot.management.repository.CauHinhHeThongRepository;
import com.iot.management.service.CauHinhHeThongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CauHinhHeThongServiceImpl implements CauHinhHeThongService {

    @Autowired
    private CauHinhHeThongRepository cauHinhRepository;

    @Override
    public List<CauHinhHeThong> getAllCauHinh() {
        return cauHinhRepository.findAll();
    }

    @Override
    public Optional<CauHinhHeThong> getCauHinhByTen(String tenCauHinh) {
        return cauHinhRepository.findByTenCauHinh(tenCauHinh);
    }

    @Override
    public String getGiaTri(String tenCauHinh, String defaultValue) {
        return cauHinhRepository.findByTenCauHinh(tenCauHinh)
                .map(CauHinhHeThong::getGiaTriCauHinh)
                .orElse(defaultValue);
    }

    @Override
    @Transactional
    public CauHinhHeThong saveCauHinh(String tenCauHinh, String giaTriCauHinh) {
        CauHinhHeThong cauHinh = cauHinhRepository.findByTenCauHinh(tenCauHinh)
                .orElse(new CauHinhHeThong());
        
        cauHinh.setTenCauHinh(tenCauHinh);
        cauHinh.setGiaTriCauHinh(giaTriCauHinh);
        
        return cauHinhRepository.save(cauHinh);
    }

    @Override
    @Transactional
    public void deleteCauHinh(String tenCauHinh) {
        cauHinhRepository.deleteById(tenCauHinh);
    }

    @Override
    @Transactional
    public void initDefaultConfig() {
        // Khởi tạo các cấu hình mặc định nếu chưa tồn tại
        initIfNotExists("app.name", "IoT Device Manager");
        initIfNotExists("app.version", "1.0.0");
        initIfNotExists("app.description", "Hệ thống quản lý thiết bị IoT");
        
        // Cấu hình thông báo
        initIfNotExists("notification.email.enabled", "false");
        initIfNotExists("notification.sound.enabled", "false");
        initIfNotExists("notification.device.enabled", "true");
        
        // Cấu hình giao diện
        initIfNotExists("ui.theme", "light");
        initIfNotExists("ui.language", "vi");
        
        // Cấu hình bảo mật
        initIfNotExists("security.2fa.enabled", "false");
        initIfNotExists("security.session.timeout", "30");
        initIfNotExists("security.password.min_length", "6");
        
        // Cấu hình dữ liệu
        initIfNotExists("data.backup.enabled", "false");
        initIfNotExists("data.backup.interval", "daily");
        initIfNotExists("data.retention.days", "30");
        
        // Cấu hình hỗ trợ
        initIfNotExists("support.email", "support@iotmanager.com");
        initIfNotExists("support.phone", "");
    }

    private void initIfNotExists(String tenCauHinh, String giaTriMacDinh) {
        if (!cauHinhRepository.existsByTenCauHinh(tenCauHinh)) {
            saveCauHinh(tenCauHinh, giaTriMacDinh);
        }
    }
}
