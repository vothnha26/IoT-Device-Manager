package com.iot.management.service;

import com.iot.management.model.entity.CauHinhTruongDuLieu;
import com.iot.management.model.entity.ThietBi;

import java.util.List;
import java.util.Optional;
import java.util.Map;


public interface CauHinhTruongDuLieuService {
    // Lấy tất cả trường dữ liệu của một loại thiết bị
    List<CauHinhTruongDuLieu> layTatCaTruongTheoLoaiThietBi(Long maLoaiThietBi);
    
    // Tạo mới một trường dữ liệu
    CauHinhTruongDuLieu taoMoiTruongDuLieu(CauHinhTruongDuLieu cauHinhTruongDuLieu);
    
    // Cập nhật một trường dữ liệu
    CauHinhTruongDuLieu capNhatTruongDuLieu(Long id, CauHinhTruongDuLieu cauHinhTruongDuLieu);
    
    // Xóa một trường dữ liệu
    void xoaTruongDuLieu(Long id);
    
    // Lấy chi tiết một trường dữ liệu
    Optional<CauHinhTruongDuLieu> layChiTietTruongDuLieu(Long id);
    
    // Kiểm tra tên trường đã tồn tại cho loại thiết bị
    boolean kiemTraTenTruongTonTai(String tenTruong, Long maLoaiThietBi);
    
    // Xác thực giá trị dữ liệu theo cấu hình
    boolean xacThucGiaTri(Long maTruongDuLieu, String giaTri);
    
    // Lấy cấu hình hiển thị cho một loại thiết bị
    List<CauHinhTruongDuLieu> layCauHinhHienThiTheoLoaiThietBi(Long maLoaiThietBi);

    void luuCauHinhThietBi(ThietBi thietBi, Map<String, Object> payload);
}