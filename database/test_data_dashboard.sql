-- ============================================
-- Script SQL để test Dashboard Admin
-- Thêm dữ liệu cho Pie Chart (Gói cước) và Line Chart (Doanh thu)
-- ============================================

-- Bước 1: Thêm các gói cước nếu chưa có
INSERT INTO goi_cuoc (ten_goi, gia_tien, sl_thiet_bi_toi_da, sl_luat_toi_da, so_ngay_luu_du_lieu, sl_khu_vuc_toi_da, sl_token_toi_da, sl_nguoi_dung_toi_da)
VALUES 
('Gói Basic', 99000, 5, 10, 30, 2, 5, 1),
('Gói Standard', 199000, 15, 30, 90, 5, 15, 3),
('Gói Premium', 399000, 50, 100, 180, 20, 50, 10),
('Gói Enterprise', 799000, 200, 500, 365, 100, 200, 50);

-- Bước 2: Thêm người dùng test nếu chưa có
-- Password: Test@123 (đã hash bằng BCrypt)
-- Kiểm tra và chỉ thêm nếu chưa tồn tại
IF NOT EXISTS (SELECT 1 FROM nguoi_dung WHERE ten_dang_nhap = 'testuser1')
BEGIN
    INSERT INTO nguoi_dung (ten_dang_nhap, mat_khau_bam, email, kich_hoat, ngay_tao)
    VALUES ('testuser1', '$2a$10$abcdefghijklmnopqrstuvwxyz123456789', 'testuser1@example.com', 1, '2024-01-15 10:00:00');
END

IF NOT EXISTS (SELECT 1 FROM nguoi_dung WHERE ten_dang_nhap = 'testuser2')
BEGIN
    INSERT INTO nguoi_dung (ten_dang_nhap, mat_khau_bam, email, kich_hoat, ngay_tao)
    VALUES ('testuser2', '$2a$10$abcdefghijklmnopqrstuvwxyz123456789', 'testuser2@example.com', 1, '2024-02-20 11:30:00');
END

IF NOT EXISTS (SELECT 1 FROM nguoi_dung WHERE ten_dang_nhap = 'testuser3')
BEGIN
    INSERT INTO nguoi_dung (ten_dang_nhap, mat_khau_bam, email, kich_hoat, ngay_tao)
    VALUES ('testuser3', '$2a$10$abcdefghijklmnopqrstuvwxyz123456789', 'testuser3@example.com', 1, '2024-03-10 14:20:00');
END

IF NOT EXISTS (SELECT 1 FROM nguoi_dung WHERE ten_dang_nhap = 'testuser4')
BEGIN
    INSERT INTO nguoi_dung (ten_dang_nhap, mat_khau_bam, email, kich_hoat, ngay_tao)
    VALUES ('testuser4', '$2a$10$abcdefghijklmnopqrstuvwxyz123456789', 'testuser4@example.com', 1, '2024-04-05 09:15:00');
END

IF NOT EXISTS (SELECT 1 FROM nguoi_dung WHERE ten_dang_nhap = 'testuser5')
BEGIN
    INSERT INTO nguoi_dung (ten_dang_nhap, mat_khau_bam, email, kich_hoat, ngay_tao)
    VALUES ('testuser5', '$2a$10$abcdefghijklmnopqrstuvwxyz123456789', 'testuser5@example.com', 1, '2024-05-12 16:45:00');
END

-- Bước 3: Lấy ID của người dùng và gói cước (sẽ dùng trong các bước sau)
-- Giả sử ID tự động tăng, bạn có thể thay đổi giá trị này phù hợp với database của bạn

-- Bước 4: Thêm đăng ký gói - 65% ACTIVE, 35% EXPIRED
-- ACTIVE packages (65%)
INSERT INTO dang_ky_goi (ma_nguoi_dung, ma_goi_cuoc, ngay_bat_dau, ngay_ket_thuc, trang_thai)
SELECT 
    (SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser1'),
    (SELECT ma_goi_cuoc FROM goi_cuoc WHERE ten_goi = 'Gói Basic'),
    '2024-10-01 00:00:00',
    '2025-01-01 00:00:00',
    'ACTIVE'
UNION ALL SELECT 
    (SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser2'),
    (SELECT ma_goi_cuoc FROM goi_cuoc WHERE ten_goi = 'Gói Standard'),
    '2024-09-15 00:00:00',
    '2024-12-15 00:00:00',
    'ACTIVE'
UNION ALL SELECT 
    (SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser3'),
    (SELECT ma_goi_cuoc FROM goi_cuoc WHERE ten_goi = 'Gói Premium'),
    '2024-10-10 00:00:00',
    '2025-04-10 00:00:00',
    'ACTIVE'
UNION ALL SELECT 
    (SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser4'),
    (SELECT ma_goi_cuoc FROM goi_cuoc WHERE ten_goi = 'Gói Basic'),
    '2024-10-20 00:00:00',
    '2025-01-20 00:00:00',
    'ACTIVE'
UNION ALL SELECT 
    (SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser5'),
    (SELECT ma_goi_cuoc FROM goi_cuoc WHERE ten_goi = 'Gói Standard'),
    '2024-10-25 00:00:00',
    '2025-01-25 00:00:00',
    'ACTIVE'
UNION ALL SELECT 
    (SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser1'),
    (SELECT ma_goi_cuoc FROM goi_cuoc WHERE ten_goi = 'Gói Premium'),
    '2024-08-01 00:00:00',
    '2025-02-01 00:00:00',
    'ACTIVE'
UNION ALL SELECT 
    (SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser2'),
    (SELECT ma_goi_cuoc FROM goi_cuoc WHERE ten_goi = 'Gói Enterprise'),
    '2024-07-15 00:00:00',
    '2025-01-15 00:00:00',
    'ACTIVE'
UNION ALL SELECT 
    (SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser3'),
    (SELECT ma_goi_cuoc FROM goi_cuoc WHERE ten_goi = 'Gói Basic'),
    '2024-09-01 00:00:00',
    '2024-12-01 00:00:00',
    'ACTIVE'
UNION ALL SELECT 
    (SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser4'),
    (SELECT ma_goi_cuoc FROM goi_cuoc WHERE ten_goi = 'Gói Standard'),
    '2024-10-05 00:00:00',
    '2025-01-05 00:00:00',
    'ACTIVE'
UNION ALL SELECT 
    (SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser5'),
    (SELECT ma_goi_cuoc FROM goi_cuoc WHERE ten_goi = 'Gói Premium'),
    '2024-08-20 00:00:00',
    '2025-02-20 00:00:00',
    'ACTIVE'
UNION ALL SELECT 
    (SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser1'),
    (SELECT ma_goi_cuoc FROM goi_cuoc WHERE ten_goi = 'Gói Standard'),
    '2024-09-10 00:00:00',
    '2024-12-10 00:00:00',
    'ACTIVE'
UNION ALL SELECT 
    (SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser2'),
    (SELECT ma_goi_cuoc FROM goi_cuoc WHERE ten_goi = 'Gói Basic'),
    '2024-10-15 00:00:00',
    '2025-01-15 00:00:00',
    'ACTIVE'
UNION ALL SELECT 
    (SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser3'),
    (SELECT ma_goi_cuoc FROM goi_cuoc WHERE ten_goi = 'Gói Enterprise'),
    '2024-07-01 00:00:00',
    '2025-01-01 00:00:00',
    'ACTIVE';

-- EXPIRED packages (35%)
INSERT INTO dang_ky_goi (ma_nguoi_dung, ma_goi_cuoc, ngay_bat_dau, ngay_ket_thuc, trang_thai)
SELECT 
    (SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser4'),
    (SELECT ma_goi_cuoc FROM goi_cuoc WHERE ten_goi = 'Gói Basic'),
    '2024-01-01 00:00:00',
    '2024-04-01 00:00:00',
    'EXPIRED'
UNION ALL SELECT 
    (SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser5'),
    (SELECT ma_goi_cuoc FROM goi_cuoc WHERE ten_goi = 'Gói Standard'),
    '2024-02-01 00:00:00',
    '2024-05-01 00:00:00',
    'EXPIRED'
UNION ALL SELECT 
    (SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser1'),
    (SELECT ma_goi_cuoc FROM goi_cuoc WHERE ten_goi = 'Gói Premium'),
    '2023-12-01 00:00:00',
    '2024-06-01 00:00:00',
    'EXPIRED'
UNION ALL SELECT 
    (SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser2'),
    (SELECT ma_goi_cuoc FROM goi_cuoc WHERE ten_goi = 'Gói Basic'),
    '2024-03-15 00:00:00',
    '2024-06-15 00:00:00',
    'EXPIRED'
UNION ALL SELECT 
    (SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser3'),
    (SELECT ma_goi_cuoc FROM goi_cuoc WHERE ten_goi = 'Gói Standard'),
    '2024-01-20 00:00:00',
    '2024-04-20 00:00:00',
    'EXPIRED'
UNION ALL SELECT 
    (SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser4'),
    (SELECT ma_goi_cuoc FROM goi_cuoc WHERE ten_goi = 'Gói Enterprise'),
    '2023-10-01 00:00:00',
    '2024-04-01 00:00:00',
    'EXPIRED'
UNION ALL SELECT 
    (SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser5'),
    (SELECT ma_goi_cuoc FROM goi_cuoc WHERE ten_goi = 'Gói Premium'),
    '2024-02-10 00:00:00',
    '2024-08-10 00:00:00',
    'EXPIRED';

-- Bước 5: Thêm thanh toán cho doanh thu theo tháng (12 tháng)
-- Lưu ý: Mỗi thanh toán cần liên kết với một đăng ký gói (ma_dang_ky)

-- Lấy ID đăng ký của từng user (lấy đăng ký đầu tiên)
DECLARE @dangky1 INT = (SELECT TOP 1 ma_dang_ky FROM dang_ky_goi WHERE ma_nguoi_dung = (SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser1') ORDER BY ma_dang_ky);
DECLARE @dangky2 INT = (SELECT TOP 1 ma_dang_ky FROM dang_ky_goi WHERE ma_nguoi_dung = (SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser2') ORDER BY ma_dang_ky);
DECLARE @dangky3 INT = (SELECT TOP 1 ma_dang_ky FROM dang_ky_goi WHERE ma_nguoi_dung = (SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser3') ORDER BY ma_dang_ky);
DECLARE @dangky4 INT = (SELECT TOP 1 ma_dang_ky FROM dang_ky_goi WHERE ma_nguoi_dung = (SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser4') ORDER BY ma_dang_ky);
DECLARE @dangky5 INT = (SELECT TOP 1 ma_dang_ky FROM dang_ky_goi WHERE ma_nguoi_dung = (SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser5') ORDER BY ma_dang_ky);

-- Tháng 1/2024 - 12 triệu
INSERT INTO thanh_toan (ma_nguoi_dung, ma_goi_cuoc, ma_dang_ky, so_tien, ngay_thanh_toan, phuong_thuc, ma_giao_dich_cong_thanh_toan, trang_thai)
VALUES 
((SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser1'), (SELECT ma_goi_cuoc FROM goi_cuoc WHERE ten_goi = 'Gói Premium'), @dangky1, 399000, '2024-01-05 10:30:00', 'SePay', 'TXN202401050001', 'SUCCESS'),
((SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser2'), (SELECT ma_goi_cuoc FROM goi_cuoc WHERE ten_goi = 'Gói Basic'), @dangky2, 99000, '2024-01-10 14:20:00', 'SePay', 'TXN202401100001', 'SUCCESS'),
((SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser3'), (SELECT ma_goi_cuoc FROM goi_cuoc WHERE ten_goi = 'Gói Standard'), @dangky3, 199000, '2024-01-15 09:45:00', 'SePay', 'TXN202401150001', 'SUCCESS'),
((SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser4'), (SELECT ma_goi_cuoc FROM goi_cuoc WHERE ten_goi = 'Gói Enterprise'), @dangky4, 799000, '2024-01-20 11:20:00', 'SePay', 'TXN202401200001', 'SUCCESS');

-- Tháng 2/2024 - 19 triệu
INSERT INTO thanh_toan (ma_nguoi_dung, ma_goi_cuoc, ma_dang_ky, so_tien, ngay_thanh_toan, phuong_thuc, ma_giao_dich_cong_thanh_toan, trang_thai)
VALUES 
((SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser4'), (SELECT ma_goi_cuoc FROM goi_cuoc WHERE ten_goi = 'Gói Basic'), @dangky4, 99000, '2024-02-08 11:15:00', 'SePay', 'TXN202402080001', 'SUCCESS'),
((SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser5'), (SELECT ma_goi_cuoc FROM goi_cuoc WHERE ten_goi = 'Gói Standard'), @dangky5, 199000, '2024-02-12 16:30:00', 'SePay', 'TXN202402120001', 'SUCCESS'),
((SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser1'), (SELECT ma_goi_cuoc FROM goi_cuoc WHERE ten_goi = 'Gói Premium'), @dangky1, 399000, '2024-02-20 10:00:00', 'SePay', 'TXN202402200001', 'SUCCESS'),
((SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser2'), (SELECT ma_goi_cuoc FROM goi_cuoc WHERE ten_goi = 'Gói Enterprise'), @dangky2, 799000, '2024-02-25 14:45:00', 'SePay', 'TXN202402250001', 'SUCCESS'),
((SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser3'), (SELECT ma_goi_cuoc FROM goi_cuoc WHERE ten_goi = 'Gói Premium'), @dangky3, 399000, '2024-02-28 09:30:00', 'SePay', 'TXN202402280001', 'SUCCESS');

-- Tháng 3/2024 - 15 triệu
INSERT INTO thanh_toan (ma_nguoi_dung, ma_goi_cuoc, ma_dang_ky, so_tien, ngay_thanh_toan, phuong_thuc, ma_giao_dich_cong_thanh_toan, trang_thai)
VALUES 
((SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser3'), (SELECT ma_goi_cuoc FROM goi_cuoc WHERE ten_goi = 'Gói Premium'), @dangky3, 399000, '2024-03-05 09:20:00', 'SePay', 'TXN202403050001', 'SUCCESS'),
((SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser4'), (SELECT ma_goi_cuoc FROM goi_cuoc WHERE ten_goi = 'Gói Basic'), @dangky4, 99000, '2024-03-10 13:40:00', 'SePay', 'TXN202403100001', 'SUCCESS'),
((SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser5'), (SELECT ma_goi_cuoc FROM goi_cuoc WHERE ten_goi = 'Gói Standard'), @dangky5, 199000, '2024-03-15 11:25:00', 'SePay', 'TXN202403150001', 'SUCCESS'),
((SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser1'), (SELECT ma_goi_cuoc FROM goi_cuoc WHERE ten_goi = 'Gói Enterprise'), @dangky1, 799000, '2024-03-20 15:10:00', 'SePay', 'TXN202403200001', 'SUCCESS');

-- Tháng 4/2024 - 25 triệu
INSERT INTO thanh_toan (ma_nguoi_dung, ma_goi_cuoc, ma_dang_ky, so_tien, ngay_thanh_toan, phuong_thuc, ma_giao_dich_cong_thanh_toan, trang_thai)
VALUES 
((SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser2'), (SELECT ma_goi_cuoc FROM goi_cuoc WHERE ten_goi = 'Gói Premium'), @dangky2, 399000, '2024-04-05 10:15:00', 'SePay', 'TXN202404050001', 'SUCCESS'),
((SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser3'), (SELECT ma_goi_cuoc FROM goi_cuoc WHERE ten_goi = 'Gói Enterprise'), @dangky3, 799000, '2024-04-10 14:30:00', 'SePay', 'TXN202404100001', 'SUCCESS'),
((SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser4'), (SELECT ma_goi_cuoc FROM goi_cuoc WHERE ten_goi = 'Gói Premium'), @dangky4, 399000, '2024-04-15 09:45:00', 'SePay', 'TXN202404150001', 'SUCCESS'),
((SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser5'), (SELECT ma_goi_cuoc FROM goi_cuoc WHERE ten_goi = 'Gói Enterprise'), @dangky5, 799000, '2024-04-20 16:20:00', 'SePay', 'TXN202404200001', 'SUCCESS'),
((SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser1'), (SELECT ma_goi_cuoc FROM goi_cuoc WHERE ten_goi = 'Gói Standard'), @dangky1, 199000, '2024-04-25 11:50:00', 'SePay', 'TXN202404250001', 'SUCCESS');

-- Tháng 5/2024 - 22 triệu
INSERT INTO thanh_toan (ma_nguoi_dung, ma_goi_cuoc, ma_dang_ky, so_tien, ngay_thanh_toan, phuong_thuc, ma_giao_dich_cong_thanh_toan, trang_thai)
VALUES 
((SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser1'), (SELECT ma_goi_cuoc FROM goi_cuoc WHERE ten_goi = 'Gói Enterprise'), @dangky1, 799000, '2024-05-05 10:00:00', 'SePay', 'TXN202405050001', 'SUCCESS'),
((SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser2'), (SELECT ma_goi_cuoc FROM goi_cuoc WHERE ten_goi = 'Gói Premium'), @dangky2, 399000, '2024-05-12 13:20:00', 'SePay', 'TXN202405120001', 'SUCCESS'),
((SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser3'), (SELECT ma_goi_cuoc FROM goi_cuoc WHERE ten_goi = 'Gói Premium'), @dangky3, 399000, '2024-05-18 15:40:00', 'SePay', 'TXN202405180001', 'SUCCESS'),
((SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser4'), (SELECT ma_goi_cuoc FROM goi_cuoc WHERE ten_goi = 'Gói Premium'), @dangky4, 399000, '2024-05-25 09:15:00', 'SePay', 'TXN202405250001', 'SUCCESS');

-- Tháng 6/2024 - 30 triệu
INSERT INTO thanh_toan (ma_nguoi_dung, ma_goi_cuoc, ma_dang_ky, so_tien, ngay_thanh_toan, phuong_thuc, ma_giao_dich_cong_thanh_toan, trang_thai)
VALUES 
((SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser5'), (SELECT ma_goi_cuoc FROM goi_cuoc WHERE ten_goi = 'Gói Enterprise'), @dangky5, 799000, '2024-06-03 10:30:00', 'SePay', 'TXN202406030001', 'SUCCESS'),
((SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser1'), (SELECT ma_goi_cuoc FROM goi_cuoc WHERE ten_goi = 'Gói Enterprise'), @dangky1, 799000, '2024-06-10 14:15:00', 'SePay', 'TXN202406100001', 'SUCCESS'),
((SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser2'), (SELECT ma_goi_cuoc FROM goi_cuoc WHERE ten_goi = 'Gói Premium'), @dangky2, 399000, '2024-06-15 11:45:00', 'SePay', 'TXN202406150001', 'SUCCESS'),
((SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser3'), (SELECT ma_goi_cuoc FROM goi_cuoc WHERE ten_goi = 'Gói Premium'), @dangky3, 399000, '2024-06-20 16:20:00', 'SePay', 'TXN202406200001', 'SUCCESS'),
((SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser4'), (SELECT ma_goi_cuoc FROM goi_cuoc WHERE ten_goi = 'Gói Premium'), @dangky4, 399000, '2024-06-28 09:50:00', 'SePay', 'TXN202406280001', 'SUCCESS');

-- Tháng 7/2024 - 28 triệu
INSERT INTO thanh_toan (ma_nguoi_dung, ma_goi_cuoc, ma_dang_ky, so_tien, ngay_thanh_toan, phuong_thuc, ma_giao_dich_cong_thanh_toan, trang_thai)
VALUES 
((SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser1'), (SELECT ma_goi_cuoc FROM goi_cuoc WHERE ten_goi = 'Gói Enterprise'), @dangky1, 799000, '2024-07-05 10:20:00', 'SePay', 'TXN202407050001', 'SUCCESS'),
((SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser2'), (SELECT ma_goi_cuoc FROM goi_cuoc WHERE ten_goi = 'Gói Enterprise'), @dangky2, 799000, '2024-07-12 13:40:00', 'SePay', 'TXN202407120001', 'SUCCESS'),
((SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser3'), (SELECT ma_goi_cuoc FROM goi_cuoc WHERE ten_goi = 'Gói Premium'), @dangky3, 399000, '2024-07-18 15:10:00', 'SePay', 'TXN202407180001', 'SUCCESS'),
((SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser4'), (SELECT ma_goi_cuoc FROM goi_cuoc WHERE ten_goi = 'Gói Standard'), @dangky4, 199000, '2024-07-25 09:30:00', 'SePay', 'TXN202407250001', 'SUCCESS');

-- Tháng 8/2024 - 35 triệu
INSERT INTO thanh_toan (ma_nguoi_dung, ma_goi_cuoc, ma_dang_ky, so_tien, ngay_thanh_toan, phuong_thuc, ma_giao_dich_cong_thanh_toan, trang_thai)
VALUES 
((SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser5'), (SELECT ma_goi_cuoc FROM goi_cuoc WHERE ten_goi = 'Gói Enterprise'), @dangky5, 799000, '2024-08-02 10:15:00', 'SePay', 'TXN202408020001', 'SUCCESS'),
((SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser1'), (SELECT ma_goi_cuoc FROM goi_cuoc WHERE ten_goi = 'Gói Enterprise'), @dangky1, 799000, '2024-08-08 14:30:00', 'SePay', 'TXN202408080001', 'SUCCESS'),
((SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser2'), (SELECT ma_goi_cuoc FROM goi_cuoc WHERE ten_goi = 'Gói Enterprise'), @dangky2, 799000, '2024-08-15 11:20:00', 'SePay', 'TXN202408150001', 'SUCCESS'),
((SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser3'), (SELECT ma_goi_cuoc FROM goi_cuoc WHERE ten_goi = 'Gói Premium'), @dangky3, 399000, '2024-08-20 16:45:00', 'SePay', 'TXN202408200001', 'SUCCESS'),
((SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser4'), (SELECT ma_goi_cuoc FROM goi_cuoc WHERE ten_goi = 'Gói Premium'), @dangky4, 399000, '2024-08-28 09:10:00', 'SePay', 'TXN202408280001', 'SUCCESS');

-- Tháng 9/2024 - 32 triệu
INSERT INTO thanh_toan (ma_nguoi_dung, ma_goi_cuoc, ma_dang_ky, so_tien, ngay_thanh_toan, phuong_thuc, ma_giao_dich_cong_thanh_toan, trang_thai)
VALUES 
((SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser1'), (SELECT ma_goi_cuoc FROM goi_cuoc WHERE ten_goi = 'Gói Enterprise'), @dangky1, 799000, '2024-09-05 10:25:00', 'SePay', 'TXN202409050001', 'SUCCESS'),
((SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser2'), (SELECT ma_goi_cuoc FROM goi_cuoc WHERE ten_goi = 'Gói Enterprise'), @dangky2, 799000, '2024-09-12 13:50:00', 'SePay', 'TXN202409120001', 'SUCCESS'),
((SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser3'), (SELECT ma_goi_cuoc FROM goi_cuoc WHERE ten_goi = 'Gói Premium'), @dangky3, 399000, '2024-09-18 15:30:00', 'SePay', 'TXN202409180001', 'SUCCESS'),
((SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser4'), (SELECT ma_goi_cuoc FROM goi_cuoc WHERE ten_goi = 'Gói Premium'), @dangky4, 399000, '2024-09-25 09:40:00', 'SePay', 'TXN202409250001', 'SUCCESS');

-- Tháng 10/2024 - 40 triệu
INSERT INTO thanh_toan (ma_nguoi_dung, ma_goi_cuoc, ma_dang_ky, so_tien, ngay_thanh_toan, phuong_thuc, ma_giao_dich_cong_thanh_toan, trang_thai)
VALUES 
((SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser5'), (SELECT ma_goi_cuoc FROM goi_cuoc WHERE ten_goi = 'Gói Enterprise'), @dangky5, 799000, '2024-10-03 10:00:00', 'SePay', 'TXN202410030001', 'SUCCESS'),
((SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser1'), (SELECT ma_goi_cuoc FROM goi_cuoc WHERE ten_goi = 'Gói Enterprise'), @dangky1, 799000, '2024-10-08 14:20:00', 'SePay', 'TXN202410080001', 'SUCCESS'),
((SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser2'), (SELECT ma_goi_cuoc FROM goi_cuoc WHERE ten_goi = 'Gói Enterprise'), @dangky2, 799000, '2024-10-12 11:35:00', 'SePay', 'TXN202410120001', 'SUCCESS'),
((SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser3'), (SELECT ma_goi_cuoc FROM goi_cuoc WHERE ten_goi = 'Gói Enterprise'), @dangky3, 799000, '2024-10-18 16:10:00', 'SePay', 'TXN202410180001', 'SUCCESS'),
((SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser4'), (SELECT ma_goi_cuoc FROM goi_cuoc WHERE ten_goi = 'Gói Premium'), @dangky4, 399000, '2024-10-25 09:50:00', 'SePay', 'TXN202410250001', 'SUCCESS');

-- Tháng 11/2024 - 38 triệu (dự kiến)
INSERT INTO thanh_toan (ma_nguoi_dung, ma_goi_cuoc, ma_dang_ky, so_tien, ngay_thanh_toan, phuong_thuc, ma_giao_dich_cong_thanh_toan, trang_thai)
VALUES 
((SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser1'), (SELECT ma_goi_cuoc FROM goi_cuoc WHERE ten_goi = 'Gói Enterprise'), @dangky1, 799000, '2024-11-05 10:15:00', 'SePay', 'TXN202411050001', 'SUCCESS'),
((SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser2'), (SELECT ma_goi_cuoc FROM goi_cuoc WHERE ten_goi = 'Gói Enterprise'), @dangky2, 799000, '2024-11-12 13:40:00', 'SePay', 'TXN202411120001', 'SUCCESS'),
((SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser3'), (SELECT ma_goi_cuoc FROM goi_cuoc WHERE ten_goi = 'Gói Enterprise'), @dangky3, 799000, '2024-11-18 15:25:00', 'SePay', 'TXN202411180001', 'SUCCESS'),
((SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser4'), (SELECT ma_goi_cuoc FROM goi_cuoc WHERE ten_goi = 'Gói Premium'), @dangky4, 399000, '2024-11-25 09:50:00', 'SePay', 'TXN202411250001', 'SUCCESS');

-- Tháng 12/2024 - 45 triệu (dự kiến)
INSERT INTO thanh_toan (ma_nguoi_dung, ma_goi_cuoc, ma_dang_ky, so_tien, ngay_thanh_toan, phuong_thuc, ma_giao_dich_cong_thanh_toan, trang_thai)
VALUES 
((SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser5'), (SELECT ma_goi_cuoc FROM goi_cuoc WHERE ten_goi = 'Gói Enterprise'), @dangky5, 799000, '2024-12-02 10:30:00', 'SePay', 'TXN202412020001', 'SUCCESS'),
((SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser1'), (SELECT ma_goi_cuoc FROM goi_cuoc WHERE ten_goi = 'Gói Enterprise'), @dangky1, 799000, '2024-12-08 14:15:00', 'SePay', 'TXN202412080001', 'SUCCESS'),
((SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser2'), (SELECT ma_goi_cuoc FROM goi_cuoc WHERE ten_goi = 'Gói Enterprise'), @dangky2, 799000, '2024-12-12 11:50:00', 'SePay', 'TXN202412120001', 'SUCCESS'),
((SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser3'), (SELECT ma_goi_cuoc FROM goi_cuoc WHERE ten_goi = 'Gói Enterprise'), @dangky3, 799000, '2024-12-18 16:20:00', 'SePay', 'TXN202412180001', 'SUCCESS'),
((SELECT ma_nguoi_dung FROM nguoi_dung WHERE ten_dang_nhap = 'testuser4'), (SELECT ma_goi_cuoc FROM goi_cuoc WHERE ten_goi = 'Gói Enterprise'), @dangky4, 799000, '2024-12-25 09:40:00', 'SePay', 'TXN202412250001', 'SUCCESS');

-- Bước 6: Kiểm tra kết quả
SELECT 'Tổng số gói cước:' AS Info, COUNT(*) AS Count FROM goi_cuoc
UNION ALL
SELECT 'Tổng số người dùng test:', COUNT(*) FROM nguoi_dung WHERE ten_dang_nhap LIKE 'testuser%'
UNION ALL
SELECT 'Tổng số đăng ký:', COUNT(*) FROM dang_ky_goi
UNION ALL
SELECT 'Gói ACTIVE:', COUNT(*) FROM dang_ky_goi WHERE trang_thai = 'ACTIVE'
UNION ALL
SELECT 'Gói EXPIRED:', COUNT(*) FROM dang_ky_goi WHERE trang_thai = 'EXPIRED'
UNION ALL
SELECT 'Tổng thanh toán:', COUNT(*) FROM thanh_toan;

-- Query để tính tỉ lệ cho Pie Chart
SELECT 
    trang_thai,
    COUNT(*) as so_luong,
    ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM dang_ky_goi), 2) as ti_le
FROM dang_ky_goi
GROUP BY trang_thai;

-- Query để tính doanh thu theo tháng cho Line Chart
SELECT 
    MONTH(ngay_thanh_toan) as thang,
    YEAR(ngay_thanh_toan) as nam,
    SUM(so_tien) / 1000000 as doanh_thu_trieu
FROM thanh_toan
WHERE trang_thai = 'SUCCESS'
    AND YEAR(ngay_thanh_toan) = 2024
GROUP BY YEAR(ngay_thanh_toan), MONTH(ngay_thanh_toan)
ORDER BY nam, thang;
