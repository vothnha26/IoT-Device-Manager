package com.iot.management.model.dto.backup;

import java.time.LocalDateTime;
import java.util.List;

public class BackupDataDTO {
    private UserInfoDTO userInfo;
    private LocalDateTime backupDate;
    private List<DuAnBackupDTO> duAn;

    public static class UserInfoDTO {
        private String tenDangNhap;
        private String email;
        private String hoTen;

        public UserInfoDTO() {}

        public UserInfoDTO(String tenDangNhap, String email, String hoTen) {
            this.tenDangNhap = tenDangNhap;
            this.email = email;
            this.hoTen = hoTen;
        }

        // Getters and Setters
        public String getTenDangNhap() { return tenDangNhap; }
        public void setTenDangNhap(String tenDangNhap) { this.tenDangNhap = tenDangNhap; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getHoTen() { return hoTen; }
        public void setHoTen(String hoTen) { this.hoTen = hoTen; }
    }

    // Constructors
    public BackupDataDTO() {}

    public BackupDataDTO(UserInfoDTO userInfo, LocalDateTime backupDate, List<DuAnBackupDTO> duAn) {
        this.userInfo = userInfo;
        this.backupDate = backupDate;
        this.duAn = duAn;
    }

    // Getters and Setters
    public UserInfoDTO getUserInfo() { return userInfo; }
    public void setUserInfo(UserInfoDTO userInfo) { this.userInfo = userInfo; }

    public LocalDateTime getBackupDate() { return backupDate; }
    public void setBackupDate(LocalDateTime backupDate) { this.backupDate = backupDate; }

    public List<DuAnBackupDTO> getDuAn() { return duAn; }
    public void setDuAn(List<DuAnBackupDTO> duAn) { this.duAn = duAn; }
}
