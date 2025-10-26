package com.iot.management.model.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Set;
import java.util.List;
@Entity
@Table(name = "NguoiDung")
public class NguoiDung {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ma_nguoi_dung")
	private Long maNguoiDung;

	@Column(name = "ten_dang_nhap", unique = true, nullable = false)
	private String tenDangNhap;

	@Column(name = "mat_khau_bam", nullable = false)
	private String matKhauBam;
	@Column(name = "email", unique = true)
	private String email;

	@Column(name = "kich_hoat", columnDefinition = "bit default 1")
	private Boolean kichHoat = true;

	@Column(name = "ngay_tao")
	private LocalDateTime ngayTao = LocalDateTime.now();

	@Column(name = "verification_code")
	private String verificationCode;

	@Column(name = "verification_code_expiry")
	private LocalDateTime verificationCodeExpiry;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
		name = "PhanQuyen",
		joinColumns = @JoinColumn(name = "ma_nguoi_dung"),
		inverseJoinColumns = @JoinColumn(name = "ma_vai_tro")
	)
	private Set<VaiTro> vaiTro;

	@JsonManagedReference("owner-regions")
	@OneToMany(mappedBy = "chuSoHuu")
	private Set<KhuVuc> khuVucs;

	@OneToMany(mappedBy = "nguoiDung", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<DangKyGoi> dangKyGois = new ArrayList<>();

	// Getters and Setters
	public Long getMaNguoiDung() {
		return maNguoiDung;
	}

	public void setMaNguoiDung(Long maNguoiDung) {
		this.maNguoiDung = maNguoiDung;
	}

	public String getTenDangNhap() {
		return tenDangNhap;
	}

	public void setTenDangNhap(String tenDangNhap) {
		this.tenDangNhap = tenDangNhap;
	}

	public String getMatKhauBam() {
		return matKhauBam;
	}

	public void setMatKhauBam(String matKhauBam) {
		this.matKhauBam = matKhauBam;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Boolean getKichHoat() {
		return kichHoat;
	}

	public void setKichHoat(Boolean kichHoat) {
		this.kichHoat = kichHoat;
	}

	public LocalDateTime getNgayTao() {
		return ngayTao;
	}

	public void setNgayTao(LocalDateTime ngayTao) {
		this.ngayTao = ngayTao;
	}

	public Set<VaiTro> getVaiTro() {
		return vaiTro;
	}

	public void setVaiTro(Set<VaiTro> vaiTro) {
		this.vaiTro = vaiTro;
	}

	public Set<KhuVuc> getKhuVucs() {
		return khuVucs;
	}

	public void setKhuVucs(Set<KhuVuc> khuVucs) {
		this.khuVucs = khuVucs;
	}

	public String getVerificationCode() {
		return verificationCode;
	}

	public void setVerificationCode(String verificationCode) {
		this.verificationCode = verificationCode;
	}

	public LocalDateTime getVerificationCodeExpiry() {
		return verificationCodeExpiry;
	}

	public void setVerificationCodeExpiry(LocalDateTime verificationCodeExpiry) {
		this.verificationCodeExpiry = verificationCodeExpiry;
	}

	public List<DangKyGoi> getDangKyGois() {
		return dangKyGois;
	}

	public void setDangKyGois(List<DangKyGoi> dangKyGois) {
		this.dangKyGois = dangKyGois;
	}
}