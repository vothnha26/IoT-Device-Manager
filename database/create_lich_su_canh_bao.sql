-- Tạo bảng lịch sử cảnh báo để lưu log khi luật tự động kích hoạt
-- Run this script if table doesn't exist

USE [IotManagerDB]
GO

-- Kiểm tra và tạo bảng nếu chưa có
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'lich_su_canh_bao')
BEGIN
    CREATE TABLE [dbo].[lich_su_canh_bao](
        [ma_canh_bao] BIGINT IDENTITY(1,1) NOT NULL,
        [ma_luat] BIGINT NOT NULL,
        [ma_thiet_bi] BIGINT NOT NULL,
        [noi_dung] NVARCHAR(255) NULL,
        [thoi_gian] DATETIME2(7) NOT NULL DEFAULT GETDATE(),
        CONSTRAINT [PK_lich_su_canh_bao] PRIMARY KEY CLUSTERED ([ma_canh_bao] ASC),
        CONSTRAINT [FK_lich_su_canh_bao_luat] FOREIGN KEY([ma_luat])
            REFERENCES [dbo].[luat_nguong] ([ma_luat])
            ON DELETE CASCADE,
        CONSTRAINT [FK_lich_su_canh_bao_thiet_bi] FOREIGN KEY([ma_thiet_bi])
            REFERENCES [dbo].[thiet_bi] ([ma_thiet_bi])
            ON DELETE NO ACTION
    )
    
    PRINT 'Bảng lich_su_canh_bao đã được tạo thành công!'
END
ELSE
BEGIN
    PRINT 'Bảng lich_su_canh_bao đã tồn tại!'
END
GO

-- Tạo index để tăng tốc truy vấn
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'IX_lich_su_canh_bao_luat' AND object_id = OBJECT_ID('lich_su_canh_bao'))
BEGIN
    CREATE NONCLUSTERED INDEX [IX_lich_su_canh_bao_luat]
    ON [dbo].[lich_su_canh_bao] ([ma_luat], [thoi_gian] DESC)
    PRINT 'Index IX_lich_su_canh_bao_luat đã được tạo!'
END
GO

IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'IX_lich_su_canh_bao_thiet_bi' AND object_id = OBJECT_ID('lich_su_canh_bao'))
BEGIN
    CREATE NONCLUSTERED INDEX [IX_lich_su_canh_bao_thiet_bi]
    ON [dbo].[lich_su_canh_bao] ([ma_thiet_bi], [thoi_gian] DESC)
    PRINT 'Index IX_lich_su_canh_bao_thiet_bi đã được tạo!'
END
GO

-- Test data (optional)
/*
INSERT INTO lich_su_canh_bao (ma_luat, ma_thiet_bi, noi_dung, thoi_gian)
VALUES 
    (1, 1, N'Luật "temperature > 35" đã tự động BẬT thiết bị "Quạt phòng khách"', DATEADD(HOUR, -2, GETDATE())),
    (1, 1, N'Luật "temperature > 35" đã tự động BẬT thiết bị "Quạt phòng khách"', DATEADD(HOUR, -1, GETDATE())),
    (2, 2, N'Luật "humidity < 40" đã tự động BẬT thiết bị "Máy phun sương"', DATEADD(MINUTE, -30, GETDATE()))
*/

PRINT 'Script hoàn tất!'
