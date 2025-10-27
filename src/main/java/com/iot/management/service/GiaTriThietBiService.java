package com.iot.management.service;

import com.iot.management.model.entity.CauHinhTruongDuLieu;
import com.iot.management.model.entity.GiaTriThietBi;
import com.iot.management.model.entity.ThietBi;

import java.util.List;
import java.util.Optional;

public interface GiaTriThietBiService {

    Optional<GiaTriThietBi> layGiaTriTheoThietBiVaTruong(ThietBi thietBi, CauHinhTruongDuLieu cauHinh);

    List<GiaTriThietBi> layTatCaGiaTriTheoThietBi(ThietBi thietBi);

    GiaTriThietBi luuGiaTri(ThietBi thietBi, CauHinhTruongDuLieu cauHinh, Object giaTri);
}
