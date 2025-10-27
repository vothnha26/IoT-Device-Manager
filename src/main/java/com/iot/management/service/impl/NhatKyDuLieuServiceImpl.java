package com.iot.management.service.impl;

import com.iot.management.model.entity.NhatKyDuLieu;
import com.iot.management.model.entity.ThietBi;
import com.iot.management.model.repository.NhatKyDuLieuRepository;
import com.iot.management.model.repository.ThietBiRepository;
import com.iot.management.service.NhatKyDuLieuService;
import com.iot.management.service.TuDongHoaService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NhatKyDuLieuServiceImpl implements NhatKyDuLieuService {

    private final NhatKyDuLieuRepository nhatKyDuLieuRepository;
    private final ThietBiRepository thietBiRepository;
    private final TuDongHoaService tuDongHoaService;

    public NhatKyDuLieuServiceImpl(NhatKyDuLieuRepository nhatKyDuLieuRepository, ThietBiRepository thietBiRepository, TuDongHoaService tuDongHoaService) {
        this.nhatKyDuLieuRepository = nhatKyDuLieuRepository;
        this.thietBiRepository = thietBiRepository;
        this.tuDongHoaService = tuDongHoaService;
    }

    @Override
    @Transactional
    public NhatKyDuLieu saveDataLog(String deviceToken, NhatKyDuLieu dataLog) {
        ThietBi thietBi = thietBiRepository.findByTokenThietBi(deviceToken)
                .orElseThrow(() -> new RuntimeException("Token thiết bị không hợp lệ: " + deviceToken));

        dataLog.setThietBi(thietBi);
        // Nếu đã có thoiGian từ request thì giữ nguyên, nếu chưa có thì set bây giờ
        if (dataLog.getThoiGian() == null) {
            dataLog.setThoiGian(LocalDateTime.now());
        }
        
        thietBi.setTrangThai("dang_ket_noi");
        thietBi.setLanHoatDongCuoi(LocalDateTime.now());
        thietBiRepository.save(thietBi);

        NhatKyDuLieu savedLog = nhatKyDuLieuRepository.save(dataLog);
        
        // Sau khi lưu thành công, gọi bộ xử lý luật
        tuDongHoaService.processRules(savedLog);
        
        return savedLog;
    }

    @Override
    public List<NhatKyDuLieu> getHistory(Long deviceId, LocalDateTime startTime, LocalDateTime endTime) {
        // Ở đây không dùng phân trang, nhưng có thể thêm Pageable vào tham số
        return nhatKyDuLieuRepository.findByThietBi_MaThietBiAndThoiGianBetween(deviceId, startTime, endTime, null);
    }

    @Override
    public List<NhatKyDuLieu> getLatestData(Long deviceId) {
        return nhatKyDuLieuRepository.findTop10ByThietBi_MaThietBiOrderByThoiGianDesc(deviceId);
    }
}