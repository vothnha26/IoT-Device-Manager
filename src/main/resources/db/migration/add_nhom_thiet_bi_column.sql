-- Migration: Thêm cột nhom_thiet_bi vào bảng LoaiThietBi
-- Date: 2025-10-16

-- Bước 1: Thêm cột mới
ALTER TABLE LoaiThietBi 
ADD COLUMN nhom_thiet_bi VARCHAR(50);

-- Bước 2: Cập nhật giá trị cho các loại thiết bị hiện có
-- Đèn LED RGB -> CONTROLLER
UPDATE LoaiThietBi 
SET nhom_thiet_bi = 'CONTROLLER' 
WHERE ten_loai LIKE '%đèn%' 
   OR ten_loai LIKE '%LED%' 
   OR ten_loai LIKE '%light%'
   OR ten_loai LIKE '%switch%'
   OR ten_loai LIKE '%công tắc%'
   OR ten_loai LIKE '%relay%';

-- Cảm biến nhiệt độ, độ ẩm -> SENSOR
UPDATE LoaiThietBi 
SET nhom_thiet_bi = 'SENSOR' 
WHERE ten_loai LIKE '%cảm biến%' 
   OR ten_loai LIKE '%sensor%'
   OR ten_loai LIKE '%nhiệt độ%'
   OR ten_loai LIKE '%độ ẩm%'
   OR ten_loai LIKE '%temperature%'
   OR ten_loai LIKE '%humidity%';

-- Thiết bị chấp hành -> ACTUATOR
UPDATE LoaiThietBi 
SET nhom_thiet_bi = 'ACTUATOR' 
WHERE ten_loai LIKE '%motor%' 
   OR ten_loai LIKE '%servo%'
   OR ten_loai LIKE '%van%'
   OR ten_loai LIKE '%quạt%'
   OR ten_loai LIKE '%fan%';

-- Bước 3: Kiểm tra kết quả
SELECT ma_loai_thiet_bi, ten_loai, nhom_thiet_bi 
FROM LoaiThietBi;
