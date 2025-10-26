package com.iot.management.service.impl;

import com.iot.management.model.entity.CauHinhTruongDuLieu;
import com.iot.management.model.entity.GiaTriThietBi;
import com.iot.management.model.entity.LoaiThietBi;
import com.iot.management.model.entity.ThietBi;
import com.iot.management.model.repository.CauHinhTruongDuLieuRepository;
import com.iot.management.model.repository.GiaTriThietBiRepository;
import com.iot.management.service.CauHinhTruongDuLieuService;
import com.iot.management.service.GiaTriThietBiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Map;

@Service
@Transactional
public class CauHinhTruongDuLieuServiceImpl implements CauHinhTruongDuLieuService {

    @Autowired
    private CauHinhTruongDuLieuRepository cauHinhTruongDuLieuRepository;
    @Autowired
    private GiaTriThietBiService giaTriThietBiService;

    @Override
    public List<CauHinhTruongDuLieu> layTatCaTruongTheoLoaiThietBi(Long maLoaiThietBi) {
        return cauHinhTruongDuLieuRepository.findByLoaiThietBi_MaLoaiThietBi(maLoaiThietBi);
    }

    @Override
    public CauHinhTruongDuLieu taoMoiTruongDuLieu(CauHinhTruongDuLieu cauHinhTruongDuLieu) {
        // Kiểm tra tên trường đã tồn tại chưa
        if (kiemTraTenTruongTonTai(cauHinhTruongDuLieu.getTenTruong(), 
                                  cauHinhTruongDuLieu.getLoaiThietBi().getMaLoaiThietBi())) {
            throw new IllegalArgumentException("Tên trường đã tồn tại cho loại thiết bị này");
        }

        // Xác thực dữ liệu đầu vào
        xacThucDuLieuDauVao(cauHinhTruongDuLieu);

        return cauHinhTruongDuLieuRepository.save(cauHinhTruongDuLieu);
    }

    @Override
    public CauHinhTruongDuLieu capNhatTruongDuLieu(Long id, CauHinhTruongDuLieu cauHinhTruongDuLieu) {
        Optional<CauHinhTruongDuLieu> existingConfig = cauHinhTruongDuLieuRepository.findById(id);
        if (existingConfig.isEmpty()) {
            throw new IllegalArgumentException("Không tìm thấy cấu hình trường dữ liệu với ID: " + id);
        }

        CauHinhTruongDuLieu existing = existingConfig.get();

        // Kiểm tra nếu tên trường thay đổi và đã tồn tại
        if (!existing.getTenTruong().equals(cauHinhTruongDuLieu.getTenTruong()) &&
            kiemTraTenTruongTonTai(cauHinhTruongDuLieu.getTenTruong(), existing.getLoaiThietBi().getMaLoaiThietBi())) {
            throw new IllegalArgumentException("Tên trường đã tồn tại cho loại thiết bị này");
        }

        // Xác thực dữ liệu đầu vào
        xacThucDuLieuDauVao(cauHinhTruongDuLieu);

        // Cập nhật thông tin
        existing.setTenTruong(cauHinhTruongDuLieu.getTenTruong());
        existing.setTenHienThi(cauHinhTruongDuLieu.getTenHienThi());
        existing.setKieuDuLieu(cauHinhTruongDuLieu.getKieuDuLieu());
        existing.setDonVi(cauHinhTruongDuLieu.getDonVi());
        existing.setGiaTriMin(cauHinhTruongDuLieu.getGiaTriMin());
        existing.setGiaTriMax(cauHinhTruongDuLieu.getGiaTriMax());
        existing.setGhiChu(cauHinhTruongDuLieu.getGhiChu());

        return cauHinhTruongDuLieuRepository.save(existing);
    }

    @Override
    public void xoaTruongDuLieu(Long id) {
        if (!cauHinhTruongDuLieuRepository.existsById(id)) {
            throw new IllegalArgumentException("Không tìm thấy cấu hình trường dữ liệu với ID: " + id);
        }
        cauHinhTruongDuLieuRepository.deleteById(id);
    }

    @Override
    public Optional<CauHinhTruongDuLieu> layChiTietTruongDuLieu(Long id) {
        return cauHinhTruongDuLieuRepository.findById(id);
    }

    @Override
    public boolean kiemTraTenTruongTonTai(String tenTruong, Long maLoaiThietBi) {
        return cauHinhTruongDuLieuRepository.existsByTenTruongAndLoaiThietBi_MaLoaiThietBi(tenTruong, maLoaiThietBi);
    }

    @Override
    public boolean xacThucGiaTri(Long maTruongDuLieu, String giaTri) {
        Optional<CauHinhTruongDuLieu> cauHinh = cauHinhTruongDuLieuRepository.findById(maTruongDuLieu);
        if (cauHinh.isEmpty()) {
            return false;
        }

        CauHinhTruongDuLieu config = cauHinh.get();
        try {
            switch (config.getKieuDuLieu().toLowerCase()) {
                case "integer":
                    int intValue = Integer.parseInt(giaTri);
                    return kiemTraGioiHan(intValue, config);
                case "float":
                case "double":
                    double doubleValue = Double.parseDouble(giaTri);
                    return kiemTraGioiHan(doubleValue, config);
                case "boolean":
                    return giaTri.equalsIgnoreCase("true") || giaTri.equalsIgnoreCase("false");
                case "string":
                    return true; // Có thể thêm validation cho độ dài chuỗi nếu cần
                case "enum":
                    // Giả sử giá trị enum được lưu trong ghi chú, cách nhau bởi dấu phẩy
                    if (config.getGhiChu() != null) {
                        String[] allowedValues = config.getGhiChu().split(",");
                        return java.util.Arrays.asList(allowedValues).contains(giaTri.trim());
                    }
                    return false;
                default:
                    return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public List<CauHinhTruongDuLieu> layCauHinhHienThiTheoLoaiThietBi(Long maLoaiThietBi) {
        return cauHinhTruongDuLieuRepository.findByLoaiThietBi_MaLoaiThietBiOrderByMaCauHinhTruongAsc(maLoaiThietBi);
    }

    private void xacThucDuLieuDauVao(CauHinhTruongDuLieu cauHinhTruongDuLieu) {
        if (cauHinhTruongDuLieu.getTenTruong() == null || cauHinhTruongDuLieu.getTenTruong().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên trường không được để trống");
        }
        
        if (cauHinhTruongDuLieu.getTenHienThi() == null || cauHinhTruongDuLieu.getTenHienThi().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên hiển thị không được để trống");
        }

        if (cauHinhTruongDuLieu.getKieuDuLieu() == null || cauHinhTruongDuLieu.getKieuDuLieu().trim().isEmpty()) {
            throw new IllegalArgumentException("Kiểu dữ liệu không được để trống");
        }

        // Kiểm tra giá trị min/max nếu có
        if (cauHinhTruongDuLieu.getGiaTriMin() != null && cauHinhTruongDuLieu.getGiaTriMax() != null) {
            if (cauHinhTruongDuLieu.getGiaTriMin().compareTo(cauHinhTruongDuLieu.getGiaTriMax()) > 0) {
                throw new IllegalArgumentException("Giá trị min không được lớn hơn giá trị max");
            }
        }
    }

    private boolean kiemTraGioiHan(Number value, CauHinhTruongDuLieu config) {
        if (config.getGiaTriMin() != null && value.doubleValue() < config.getGiaTriMin().doubleValue()) {
            return false;
        }
        if (config.getGiaTriMax() != null && value.doubleValue() > config.getGiaTriMax().doubleValue()) {
            return false;
        }
        return true;
    }
    @Override
    public void luuCauHinhThietBi(ThietBi thietBi, Map<String, Object> payload) {
    List<CauHinhTruongDuLieu> configs = layCauHinhHienThiTheoLoaiThietBi(thietBi.getLoaiThietBi().getMaLoaiThietBi());

    for (CauHinhTruongDuLieu config : configs) {
        String key = config.getTenTruong();
        if (payload.containsKey(key)) {
            Object value = payload.get(key);

            // Validate
            if (!xacThucGiaTri(config.getMaCauHinhTruong(), value.toString())) {
                throw new IllegalArgumentException("Giá trị không hợp lệ cho trường: " + key);
            }

            // Lưu vào bảng GiaTriThietBi
            giaTriThietBiService.luuGiaTri(thietBi, config, value);
        }
    }
}

}


