package com.iot.management.controller.api;

public class CommandRequest {
    private Long maThietBi;
    private String tenLenh;
    private String giaTriLenh;

    public CommandRequest() {
    }

    public Long getMaThietBi() {
        return maThietBi;
    }

    public void setMaThietBi(Long maThietBi) {
        this.maThietBi = maThietBi;
    }

    public String getTenLenh() {
        return tenLenh;
    }

    public void setTenLenh(String tenLenh) {
        this.tenLenh = tenLenh;
    }

    public String getGiaTriLenh() {
        return giaTriLenh;
    }

    public void setGiaTriLenh(String giaTriLenh) {
        this.giaTriLenh = giaTriLenh;
    }
}
