-- Tạo bảng LoiMoiDuAn để lưu lời mời tham gia dự án

CREATE TABLE LoiMoiDuAn (
    ma_loi_moi BIGINT IDENTITY(1,1) PRIMARY KEY,
    ma_du_an BIGINT NOT NULL,
    email_nguoi_nhan NVARCHAR(255) NOT NULL,
    ma_nguoi_moi BIGINT NOT NULL,
    vai_tro NVARCHAR(50) NOT NULL,
    token NVARCHAR(255) UNIQUE NOT NULL,
    ngay_tao DATETIME DEFAULT GETDATE(),
    ngay_het_han DATETIME,
    trang_thai NVARCHAR(50) DEFAULT 'PENDING',
    
    FOREIGN KEY (ma_du_an) REFERENCES DuAn(ma_du_an),
    FOREIGN KEY (ma_nguoi_moi) REFERENCES NguoiDung(ma_nguoi_dung)
);

-- Tạo index để tìm kiếm nhanh
CREATE INDEX idx_loimoi_token ON LoiMoiDuAn(token);
CREATE INDEX idx_loimoi_email_duAn ON LoiMoiDuAn(email_nguoi_nhan, ma_du_an, trang_thai);
