-- Kiểm tra tất cả payments
SELECT 
    tt.ma_thanh_toan,
    tt.trang_thai,
    tt.so_tien,
    tt.ngay_thanh_toan,
    dk.ma_dang_ky,
    dk.trang_thai as trang_thai_goi,
    gc.ten_goi,
    nd.ten_dang_nhap
FROM thanh_toan tt
LEFT JOIN dang_ky_goi dk ON tt.ma_dang_ky = dk.ma_dang_ky
LEFT JOIN goi_cuoc gc ON tt.ma_goi_cuoc = gc.ma_goi_cuoc
LEFT JOIN nguoi_dung nd ON tt.ma_nguoi_dung = nd.ma_nguoi_dung
ORDER BY tt.ma_thanh_toan DESC;

-- Kiểm tra payments PENDING
SELECT * FROM thanh_toan WHERE trang_thai = 'PENDING';

-- Test content parse
-- Content sẽ là: SEVQR1 Thanh toan goi Basic
-- hoặc: SEVQR2 Thanh toan goi Premium
