package com.iot.management.service.impl;

import com.iot.management.model.entity.CauHinhTruongDuLieu;
import com.iot.management.model.entity.GiaTriThietBi;
import com.iot.management.model.entity.ThietBi;
import com.iot.management.model.repository.GiaTriThietBiRepository;
import com.iot.management.service.GiaTriThietBiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class GiaTriThietBiServiceImpl implements GiaTriThietBiService {

    @Autowired
    private GiaTriThietBiRepository giaTriThietBiRepository;

    @Override
    public Optional<GiaTriThietBi> layGiaTriTheoThietBiVaTruong(ThietBi thietBi, CauHinhTruongDuLieu cauHinh) {
        return giaTriThietBiRepository.findByThietBiAndCauHinhTruongDuLieu(thietBi, cauHinh);
    }

    @Override
    public List<GiaTriThietBi> layTatCaGiaTriTheoThietBi(ThietBi thietBi) {
        return giaTriThietBiRepository.findByThietBi(thietBi);
    }

    @Override
    public GiaTriThietBi luuGiaTri(ThietBi thietBi, CauHinhTruongDuLieu cauHinh, Object giaTri) {
        GiaTriThietBi giaTriThietBi = giaTriThietBiRepository.findByThietBiAndCauHinhTruongDuLieu(thietBi, cauHinh)
                .orElse(new GiaTriThietBi(thietBi, cauHinh));

        // Chuyển giá trị sang Double nếu kiểu số, hoặc String
        switch (cauHinh.getKieuDuLieu().toLowerCase()) {
            case "integer":
                giaTriThietBi.setGiaTri(Double.valueOf(Integer.parseInt(giaTri.toString())));
                break;
            case "float":
            case "double":
                giaTriThietBi.setGiaTri(Double.parseDouble(giaTri.toString()));
                break;
            case "boolean":
                giaTriThietBi.setGiaTri(Boolean.parseBoolean(giaTri.toString()) ? 1.0 : 0.0);
                break;
            default:
                giaTriThietBi.setGiaTriStr(giaTri.toString());
        }

        return giaTriThietBiRepository.save(giaTriThietBi);
    }
}
