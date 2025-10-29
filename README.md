# 🌐 IoT Device Manager - WEBSITE Quản Lý Thiết Bị IoTs

[![Java](https://img.shields.io/badge/Java-24-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![SQL Server](https://img.shields.io/badge/SQL%20Server-2019+-red.svg)](https://www.microsoft.com/sql-server)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

Hệ thống quản lý và giám sát thiết bị IoT toàn diện, tích hợp WebSocket real-time, tự động hóa thông minh, thanh toán VietQR và dashboard phân tích dữ liệu chuyên sâu.

---

## 📑 Mục lục

- [🎯 Giới thiệu nhanh](#-giới-thiệu-nhanh)
- [🏗️ Kiến trúc và công nghệ](#-kiến-trúc-và-công-nghệ)
- [✨ Tính năng chính](#-tính-năng-chính)
- [📸 Ảnh chụp màn hình](#-ảnh-chụp-màn-hình)
- [📂 Cấu trúc thư mục](#-cấu-trúc-thư-mục)
- [🚀 Thiết lập môi trường](#-thiết-lập-môi-trường)
- [⚙️ Cấu hình ứng dụng](#-cấu-hình-ứng-dụng)
- [🔧 Hướng dẫn chạy](#-hướng-dẫn-chạy)
- [🔐 Bảo mật & Triển khai](#-bảo-mật--triển-khai)
- [🐛 Khắc phục sự cố](#-khắc-phục-sự-cố)
- [📝 Tài liệu API](#-tài-liệu-api)
- [🤝 Đóng góp](#-đóng-góp)
- [📄 Giấy phép](#-giấy-phép)

---

## 🎯 Giới thiệu nhanh

**IoT Device Manager** là một nền tảng quản lý thiết bị IoT toàn diện, được xây dựng với **Spring Boot 3.5.5** và **Java 24**. Hệ thống cung cấp giải pháp hoàn chỉnh cho việc:

- 🔌 **Quản lý thiết bị IoT** real-time qua WebSocket
- 📊 **Dashboard phân tích** với biểu đồ và thống kê chi tiết
- 🤖 **Tự động hóa thông minh** với lịch trình và luật ngưỡng
- 💳 **Thanh toán VietQR** cho gói cước dịch vụ
- 🔔 **Thông báo real-time** qua WebSocket và Email
- 👥 **Phân quyền đa cấp** (Admin, User, Project-based)
- 📈 **Thống kê & báo cáo** với xuất Excel

---

## 🏗️ Kiến trúc và công nghệ

### **Backend**
- **Framework**: Spring Boot 3.5.5 (Java 24)
- **Architecture**: MVC, Service, Repository Pattern
- **Real-time**: WebSocket, STOMP Protocol
- **Database**: Microsoft SQL Server 2019+ / H2 (dev mode)
- **ORM**: Spring Data JPA, Hibernate

### **Frontend**
- **Template Engine**: Thymeleaf
- **UI Framework**: Bootstrap 5, jQuery
- **AJAX**: jQuery AJAX, Fetch API (Asynchronous requests)
- **Real-time**: SockJS, STOMP.js, WebSocket
- **Charts**: Chart.js, ApexCharts

### **Security & Authentication**
- **Spring Security**: Role-based Access Control (RBAC)
- **JWT**: Token-based Authentication cho API
- **OAuth2**: Hỗ trợ đăng nhập bên thứ 3 (future)
- **Email OTP**: Xác thực 2 bước

### **Payment Integration**
- **VietQR**: Thanh toán qua QR Code
- **Webhook**: Xử lý callback tự động

### **Additional Technologies**
- **Email**: Spring Mail (SMTP)
- **Export**: Apache POI (Excel)
- **Build Tool**: Apache Maven 3.9+
- **Validation**: Jakarta Validation (Bean Validation)

### **Phiên bản Stack**

| Thành phần | Phiên bản | Ghi chú |
|------------|-----------|---------|
| Java | 24 | JDK 24 |
| Spring Boot | 3.5.5 | Framework chính |
| SQL Server | 2019+ | Production DB |
| H2 Database | Runtime | Development DB |
| Maven | 3.9+ | Build tool |
| Thymeleaf | 3.x | Template engine |
| Bootstrap | 5.x | UI framework |

### **Các thành phần chính**

```
src/main/java/com/iot/management/
├── config/              # Cấu hình (Security, WebSocket, Mail)
├── controller/          # Controllers (UI + API)
│   ├── ui/             # Giao diện người dùng
│   ├── api/            # RESTful API
│   └── admin/          # Quản trị viên
├── model/
│   ├── entity/         # Entities (JPA)
│   ├── dto/            # Data Transfer Objects
│   └── repository/     # Spring Data Repositories
├── service/             # Business Logic
├── security/            # JWT, UserDetails
├── websocket/           # WebSocket handlers
├── util/                # Utilities
└── event/               # Event handlers
```

---

## ✨ Tính năng chính

### 🔐 **1. Xác thực & Phân quyền**

| Nhóm người dùng | Chức năng chính |
|-----------------|-----------------|
| **👤 Người dùng mới** | - Đăng ký tài khoản với email<br>- Xác thực tài khoản qua OTP (6 chữ số, hết hạn 15 phút)<br>- Quên mật khẩu & đặt lại qua OTP |
| **🔓 Người dùng đã đăng nhập** | - Đăng nhập/Đăng xuất với JWT token<br>- Quản lý thông tin cá nhân<br>- Đổi mật khẩu<br>- Xem lịch sử hoạt động |
| **⚡ Hệ thống bảo mật** | - Spring Security với role-based access<br>- Mã hóa mật khẩu BCrypt<br>- JWT Authentication cho API<br>- Session management |

### 📊 **2. Quản lý Dự án (Project Management)**

- ✅ Tạo và quản lý nhiều dự án IoT
- 👥 Mời thành viên tham gia dự án
- 🔑 Phân quyền dự án (Owner, Editor, Viewer)
- 📍 Quản lý khu vực trong dự án
- 🏢 Tổ chức thiết bị theo khu vực

### 🔌 **3. Quản lý Thiết bị IoT**

#### **Quản lý thiết bị**
- ➕ Thêm/Sửa/Xóa thiết bị
- 📝 Cấu hình thông tin thiết bị (tên, loại, mã thiết bị)
- 📊 Theo dõi trạng thái online/offline
- 🎛️ Quản lý loại thiết bị (Admin)

#### **Điều khiển thiết bị**
- 🎮 Gửi lệnh điều khiển real-time
- 🔄 Cập nhật trạng thái tức thời qua WebSocket
- 📡 Hỗ trợ nhiều giao thức (WebSocket, MQTT ready)

#### **Dữ liệu thiết bị**
- 📈 Xem nhật ký dữ liệu theo thời gian
- 📊 Biểu đồ trực quan hóa dữ liệu
- 💾 Lưu trữ lịch sử dữ liệu
- 📑 Xuất báo cáo Excel

### 🤖 **4. Tự động hóa (Automation)**

#### **Quản lý lịch trình (Schedule)**
- ⏰ Tạo lịch trình điều khiển tự động
- 🔁 Lặp lại theo chu kỳ (hàng ngày, tuần, tháng)
- ⏲️ Thiết lập thời gian cụ thể
- ✏️ Sửa/Xóa lịch trình

#### **Luật ngưỡng (Rule Engine)**
- 📏 Thiết lập ngưỡng cảnh báo
- 🔔 Tự động trigger khi thỏa điều kiện
- 📧 Gửi thông báo email/in-app
- 🎯 Hỗ trợ nhiều điều kiện phức tạp (temp > 30, humidity < 40)

### 💳 **5. Gói cước & Thanh toán**

#### **Quản lý gói cước**
- 📦 Nhiều gói cước với giới hạn khác nhau
- 💰 Giá cả linh hoạt
- 🎁 Tính năng voucher/mã giảm giá

#### **Thanh toán VietQR**
- 🏦 Tích hợp VietQR Banking
- 📱 Tạo mã QR tự động
- ✅ Webhook xác nhận thanh toán
- 📧 Email xác nhận giao dịch
- 📜 Lịch sử giao dịch chi tiết

### 📊 **6. Dashboard & Thống kê**

#### **Dashboard người dùng**
- 📈 Tổng quan: Số dự án, thiết bị, cảnh báo
- 📊 Biểu đồ dữ liệu real-time
- 🔔 Thông báo mới nhất
- 📱 Thiết bị gần đây

#### **Dashboard Admin**
- 👥 Thống kê người dùng (tổng, mới, hoạt động)
- 🔌 Thống kê thiết bị (tổng, online, offline)
- 💰 Doanh thu và giao dịch
- 📊 Biểu đồ tăng trưởng
- 🏆 Top người dùng hoạt động

#### **Báo cáo & Thống kê**
- 📅 Lọc theo khoảng thời gian
- 📈 Biểu đồ xu hướng
- 📊 Bảng thống kê chi tiết
- 📑 Xuất Excel (Apache POI)

### 🔔 **7. Thông báo Real-time**

- 🌐 WebSocket notification
- 📧 Email notification (SMTP)
- 🔔 Badge hiển thị số thông báo chưa đọc
- 📱 Popup thông báo tức thời
- 📋 Quản lý thông báo (đọc/chưa đọc)

### 👨‍💼 **8. Quản trị Admin**

| Module | Chức năng |
|--------|-----------|
| **👥 Quản lý người dùng** | Xem/Thêm/Sửa/Xóa người dùng, Phân quyền |
| **📦 Quản lý gói cước** | CRUD gói cước, Thiết lập giới hạn |
| **🔌 Quản lý loại thiết bị** | Định nghĩa các loại thiết bị, Cấu hình trường dữ liệu |
| **💰 Quản lý giao dịch** | Xem lịch sử thanh toán, Lọc và tìm kiếm |
| **🎫 Quản lý Voucher** | Tạo mã giảm giá, Điều kiện áp dụng |
| **🔐 Quản lý quyền thiết bị** | Phân quyền điều khiển thiết bị |

### 🎨 **9. Giao diện người dùng**

- 🎨 **Responsive Design**: Tương thích mọi thiết bị
- 🌓 **Theme**: Hỗ trợ dark/light mode (future)
- 📱 **Mobile-first**: Tối ưu cho di động
- ⚡ **Performance**: Lazy loading, caching

---

## 📸 Ảnh chụp màn hình

### 🏠 Trang chủ
*Giao diện chính với overview các dự án và thiết bị*

<img width="1742" height="856" alt="image" src="https://github.com/user-attachments/assets/f121ebc8-fe7f-4192-9b93-8346fcaad204" />

### 📊 Dashboard
*Dashboard với biểu đồ thống kê real-time*

<img width="1712" height="712" alt="image" src="https://github.com/user-attachments/assets/f0f08767-c221-4d8f-8bc4-c5e7d6a3051b" />


### 🔌 Quản lý thiết bị
*Danh sách thiết bị với trạng thái và điều khiển*

<img width="1704" height="773" alt="image" src="https://github.com/user-attachments/assets/a76a0c9d-70f8-4f65-9994-d58250879249" />


### 👨‍💼 Admin Dashboard
*Trang quản trị với thống kê tổng quan*

<img width="1511" height="803" alt="image" src="https://github.com/user-attachments/assets/a205da55-be8a-42c5-a271-3c62325bb80d" />


---

## 📂 Cấu trúc thư mục

```
IoT-Device-Manager/
├── src/
│   ├── main/
│   │   ├── java/com/iot/management/
│   │   │   ├── config/                    # Cấu hình Spring
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   ├── WebSocketConfig.java
│   │   │   │   └── MailConfig.java
│   │   │   ├── controller/                # Controllers
│   │   │   │   ├── ui/                    # UI Controllers
│   │   │   │   │   ├── HomeController.java
│   │   │   │   │   ├── DashboardController.java
│   │   │   │   │   ├── DuAnController.java
│   │   │   │   │   ├── KhuVucController.java
│   │   │   │   │   ├── ThietBiUiController.java
│   │   │   │   │   ├── ProfileController.java
│   │   │   │   │   ├── PaymentController.java
│   │   │   │   │   └── StatisticsController.java
│   │   │   │   ├── api/                   # API Controllers
│   │   │   │   │   ├── auth/
│   │   │   │   │   │   └── AuthController.java
│   │   │   │   │   ├── device/
│   │   │   │   │   │   ├── ThietBiController.java
│   │   │   │   │   │   └── LoaiThietBiController.java
│   │   │   │   │   ├── automation/
│   │   │   │   │   │   ├── LichTrinhController.java
│   │   │   │   │   │   └── LuatNguongController.java
│   │   │   │   │   ├── admin/
│   │   │   │   │   │   ├── AdminUserController.java
│   │   │   │   │   │   ├── AdminGoiCuocController.java
│   │   │   │   │   │   └── AdminDeviceTypeController.java
│   │   │   │   │   └── user/
│   │   │   │   │       └── UserController.java
│   │   │   │   └── NotificationController.java
│   │   │   ├── model/
│   │   │   │   ├── entity/                # JPA Entities
│   │   │   │   │   ├── NguoiDung.java
│   │   │   │   │   ├── VaiTro.java
│   │   │   │   │   ├── DuAn.java
│   │   │   │   │   ├── KhuVuc.java
│   │   │   │   │   ├── ThietBi.java
│   │   │   │   │   ├── LoaiThietBi.java
│   │   │   │   │   ├── NhatKyDuLieu.java
│   │   │   │   │   ├── LenhDieuKhien.java
│   │   │   │   │   ├── LichTrinh.java
│   │   │   │   │   ├── LuatNguong.java
│   │   │   │   │   ├── ThongBao.java
│   │   │   │   │   ├── GoiCuoc.java
│   │   │   │   │   ├── DangKyGoi.java
│   │   │   │   │   └── ThanhToan.java
│   │   │   │   ├── dto/                   # Data Transfer Objects
│   │   │   │   │   ├── request/
│   │   │   │   │   └── response/
│   │   │   │   ├── enums/                 # Enumerations
│   │   │   │   └── repository/            # Spring Data Repositories
│   │   │   ├── service/                   # Business Logic
│   │   │   │   ├── NguoiDungService.java
│   │   │   │   ├── DuAnService.java
│   │   │   │   ├── KhuVucService.java
│   │   │   │   ├── ThietBiService.java
│   │   │   │   ├── EmailService.java
│   │   │   │   ├── DashboardService.java
│   │   │   │   ├── GoiCuocService.java
│   │   │   │   ├── VietQRService.java
│   │   │   │   ├── TuDongHoaService.java
│   │   │   │   └── ThongBaoService.java
│   │   │   ├── security/                  # Security components
│   │   │   │   ├── JwtUtil.java
│   │   │   │   ├── SecurityUser.java
│   │   │   │   └── SecurityUserDetailsService.java
│   │   │   ├── websocket/                 # WebSocket handlers
│   │   │   │   ├── DeviceMessagingService.java
│   │   │   │   └── NotificationWebSocketController.java
│   │   │   ├── util/                      # Utilities
│   │   │   ├── event/                     # Event handlers
│   │   │   └── IotManagementApplication.java
│   │   └── resources/
│   │       ├── application.properties     # Cấu hình chính
│   │       ├── application-dev.properties # Cấu hình dev
│   │       ├── static/                    # Tài nguyên tĩnh
│   │       │   ├── css/
│   │       │   ├── js/
│   │       │   └── videos/
│   │       └── templates/                 # Thymeleaf templates
│   │           ├── dashboard.html
│   │           ├── homepage.html
│   │           ├── notifications.html
│   │           ├── profile.html
│   │           ├── admin/                 # Admin pages
│   │           ├── auth/                  # Authentication pages
│   │           ├── du-an/                 # Project pages
│   │           ├── khu-vuc/               # Area pages
│   │           ├── thiet-bi/              # Device pages
│   │           ├── payment/               # Payment pages
│   │           ├── thong-ke/              # Statistics pages
│   │           └── fragments/             # Reusable components
├── database/                              # Database scripts
│   ├── create_lich_su_canh_bao.sql
│   ├── create_loi_moi_du_an.sql
│   └── test_data_dashboard.sql
├── docs/                                  # Documentation
│   ├── screenshots/                       # Screenshots
│   └── API.md                            # API documentation
├── ESP32_WebSocket_Control_Fixed.ino     # ESP32 firmware
├── iotdb.bak                             # Database backup
├── HELP.md
├── pom.xml                               # Maven dependencies
├── mvnw                                  # Maven wrapper
├── mvnw.cmd
└── README.md
```

---

## 🚀 Thiết lập môi trường

### **1. Yêu cầu hệ thống (Prerequisites)**

| Thành phần | Phiên bản | Ghi chú |
|------------|-----------|---------|
| **JDK** | 24+ | [Download Oracle JDK](https://www.oracle.com/java/technologies/downloads/) |
| **SQL Server** | 2019+ | [Download SQL Server](https://www.microsoft.com/sql-server/sql-server-downloads) |
| **Maven** | 3.9+ | Hoặc sử dụng Maven Wrapper (`./mvnw`) |
| **IDE** | IntelliJ IDEA / Eclipse / VS Code | Khuyến nghị IntelliJ IDEA |
| **H2 Database** | - | Tùy chọn cho development (đã có sẵn) |

### **2. Clone Repository**

```bash
git clone https://github.com/vothnha26/IoT-Device-Manager.git
cd IoT-Device-Manager
```

### **3. Cấu hình Database**

#### **Option 1: Sử dụng SQL Server (Production)**

1. Tạo database mới:
```sql
CREATE DATABASE iotdb;
GO
```

2. Restore database từ backup:
```bash
# Sử dụng SQL Server Management Studio (SSMS)
# Restore từ file iotdb.bak
```

3. Chạy migration scripts (nếu cần):
```sql
-- Chạy các file .sql trong thư mục database/
USE iotdb;
GO
-- Execute scripts...
```

#### **Option 2: Sử dụng H2 Database (Development)**

H2 sẽ tự động khởi tạo khi chạy với profile `dev`:
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

Truy cập H2 Console:
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:iotdev`
- Username: `sa`
- Password: (để trống)

---

## ⚙️ Cấu hình ứng dụng

### **1. Cấu hình Database (application.properties)**

```properties
# SQL Server Configuration (Production)
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=iotdb;encrypt=false;trustServerCertificate=true
spring.datasource.username=your-username
spring.datasource.password=your-password
spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.SQLServerDialect

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Server
server.port=8080
server.servlet.encoding.charset=UTF-8
server.servlet.encoding.enabled=true
server.servlet.encoding.force=true

# Thymeleaf
spring.thymeleaf.cache=false

# File Upload
spring.servlet.multipart.max-file-size=20MB
spring.servlet.multipart.max-request-size=21MB
```

### **2. Cấu hình Email (Gmail SMTP)**

```properties
# Email Configuration
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true

# App Email Settings
app.mail.from-name=IoT Device Manager
app.mail.from-address=your-email@gmail.com
```

**⚠️ Lưu ý**: Sử dụng **App Password** của Google, không phải mật khẩu Gmail thường.

[Hướng dẫn tạo App Password](https://support.google.com/accounts/answer/185833)

### **3. Cấu hình JWT**

```properties
# JWT Configuration
jwt.secret=your-secret-key-change-this-in-production
jwt.expiration=86400000
```

### **4. Cấu hình VietQR (Thanh toán)**

```properties
# VietQR Configuration
vietqr.bank-code=VCB
vietqr.account-no=your-account-number
vietqr.account-name=YOUR ACCOUNT NAME
vietqr.template=compact
```

### **5. Biến môi trường (Environment Variables)**

Tạo file `.env` hoặc set biến môi trường:

```bash
# Database
export DB_URL=jdbc:sqlserver://localhost:1433;databaseName=iotdb
export DB_USERNAME=your-username
export DB_PASSWORD=your-password

# Email
export MAIL_USERNAME=your-email@gmail.com
export MAIL_PASSWORD=your-app-password

# JWT
export JWT_SECRET=your-very-long-secret-key-here

# VietQR
export VIETQR_ACCOUNT_NO=your-account-number
export VIETQR_ACCOUNT_NAME=YOUR_NAME
```

---

## 🔧 Hướng dẫn chạy

### **Method 1: Sử dụng IDE (IntelliJ IDEA)**

1. **Import project**:
   - File → Open → Chọn thư mục dự án
   - Chọn "Import as Maven project"

2. **Cấu hình Run Configuration**:
   - Run → Edit Configurations
   - Add New → Spring Boot
   - Main class: `com.iot.management.IotManagementApplication`
   - Environment variables: (optional)

3. **Run application**:
   - Click nút Run hoặc `Shift + F10`

### **Method 2: Sử dụng Maven Wrapper**

```bash
# Clean và compile
./mvnw clean install

# Chạy ứng dụng (Production mode)
./mvnw spring-boot:run

# Chạy với profile dev (H2 Database)
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Build JAR file
./mvnw clean package
java -jar target/iot-management-0.0.1-SNAPSHOT.jar
```

### **Method 3: Sử dụng Docker (Future)**

```bash
# Build Docker image
docker build -t iot-device-manager .

# Run container
docker run -p 8080:8080 \
  -e DB_URL=jdbc:sqlserver://host.docker.internal:1433;databaseName=iotdb \
  -e DB_USERNAME=sa \
  -e DB_PASSWORD=your-password \
  iot-device-manager
```

### **Truy cập ứng dụng**

| Trang | URL | Ghi chú |
|-------|-----|---------|
| **Trang chủ** | http://localhost:8080/ | Public |
| **Đăng nhập** | http://localhost:8080/auth/login | |
| **Đăng ký** | http://localhost:8080/auth/register | |
| **Dashboard** | http://localhost:8080/dashboard | Yêu cầu đăng nhập |
| **Admin** | http://localhost:8080/admin/dashboard | Yêu cầu role ADMIN |
| **H2 Console** | http://localhost:8080/h2-console | Chỉ dev mode |

### **Tài khoản mặc định**

Sau khi chạy lần đầu, hệ thống sẽ tự động tạo tài khoản admin:

```
Email: admin@iot.com
Password: Admin@123
Role: ADMIN
```

**⚠️ Lưu ý**: Đổi mật khẩu ngay sau lần đăng nhập đầu tiên!

---

## 🔐 Bảo mật & Triển khai

### **Checklist bảo mật Production**

- [ ] ✅ Đổi tất cả mật khẩu mặc định
- [ ] ✅ Sử dụng biến môi trường cho secrets
- [ ] ✅ Bật HTTPS (SSL/TLS)
- [ ] ✅ Cấu hình CORS đúng cách
- [ ] ✅ Tắt H2 Console trong production
- [ ] ✅ Set `spring.jpa.show-sql=false`
- [ ] ✅ Sử dụng strong JWT secret key
- [ ] ✅ Cấu hình rate limiting
- [ ] ✅ Enable SQL injection protection
- [ ] ✅ Validate tất cả input từ người dùng

### **Triển khai lên Server**

```properties
# application-prod.properties
spring.profiles.active=prod
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
logging.level.root=WARN
server.error.include-stacktrace=never
```

### **Reverse Proxy (Nginx)**

```nginx
server {
    listen 80;
    server_name your-domain.com;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # WebSocket support
    location /ws {
        proxy_pass http://localhost:8080/ws;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
    }
}
```

---

## 🐛 Khắc phục sự cố

### **Lỗi Database Connection**

```
Error: Cannot create PoolableConnectionFactory
```

**Giải pháp**:
1. Kiểm tra SQL Server đã chạy chưa
2. Xác nhận connection string đúng
3. Kiểm tra firewall cho port 1433
4. Verify username/password

### **Lỗi Port đã được sử dụng**

```
Error: Port 8080 is already in use
```

**Giải pháp**:
```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Linux/Mac
lsof -ti:8080 | xargs kill -9

# Hoặc đổi port trong application.properties
server.port=8081
```

### **Lỗi Email không gửi được**

```
Error: Authentication failed
```

**Giải pháp**:
1. Sử dụng App Password thay vì password Gmail
2. Bật "Less secure app access" (không khuyến nghị)
3. Kiểm tra SMTP settings
4. Verify firewall không chặn port 587

### **Lỗi WebSocket không kết nối**

**Giải pháp**:
1. Kiểm tra browser console có lỗi không
2. Verify WebSocket endpoint đúng
3. Kiểm tra firewall/proxy có block WebSocket không
4. Test với SockJS fallback

### **Lỗi Build Maven**

```
Error: Failed to execute goal
```

**Giải pháp**:
```bash
# Clear cache
./mvnw clean

# Update dependencies
./mvnw clean install -U

# Skip tests
./mvnw clean package -DskipTests
```

---

## 📝 Tài liệu API

### **Swagger/OpenAPI Documentation**

Truy cập Swagger UI khi ứng dụng đang chạy:

```
http://localhost:8080/swagger-ui.html
```

### **Các API endpoint chính**

#### **Authentication**

```http
POST /api/auth/register
POST /api/auth/login
POST /api/auth/logout
POST /api/auth/forgot-password
POST /api/auth/reset-password
POST /api/auth/verify-account
```

#### **Dự án**

```http
GET    /api/du-an
POST   /api/du-an/them-moi
PUT    /api/du-an/{maDuAn}/cap-nhat
DELETE /api/du-an/{maDuAn}/xoa
```

#### **Thiết bị**

```http
GET    /api/thiet-bi
POST   /api/thiet-bi/them-moi
PUT    /api/thiet-bi/{maThietBi}/cap-nhat
DELETE /api/thiet-bi/{maThietBi}/xoa
POST   /api/thiet-bi/{maThietBi}/dieu-khien
```

#### **Tự động hóa**

```http
GET    /api/automation/lich-trinh
POST   /api/automation/lich-trinh/them-moi
GET    /api/automation/luat-nguong
POST   /api/automation/luat-nguong/them-moi
```

#### **Thanh toán**

```http
POST   /api/payment/create-payment/{maGoiCuoc}
GET    /api/payment/return
POST   /api/payment/notify
GET    /api/payment/history
```

#### **Admin**

```http
GET    /api/admin/users
GET    /api/admin/packages
GET    /api/admin/transactions
GET    /api/admin/stats
```

---

## 🤝 Đóng góp

Chúng tôi rất hoan nghênh mọi đóng góp cho dự án! 

### **Quy trình đóng góp**

1. **Fork repository**
2. **Tạo branch mới**:
   ```bash
   git checkout -b feature/TenTinhNang
   ```
3. **Commit changes**:
   ```bash
   git commit -m "Add: Thêm tính năng X"
   ```
4. **Push to branch**:
   ```bash
   git push origin feature/TenTinhNang
   ```
5. **Tạo Pull Request**

### **Coding Convention**

- Sử dụng Java Code Style (Google Java Style)
- Comment code bằng tiếng Việt có dấu
- Đặt tên biến/method có ý nghĩa
- Viết Unit Test cho code mới
- Update README.md nếu có thay đổi lớn

---

## 📄 Giấy phép

Dự án này được cấp phép theo **MIT License** - xem file [LICENSE](LICENSE) để biết thêm chi tiết.

```
MIT License

Copyright © 2025 IoT Device Manager Team

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

---

## 👥 Nhóm phát triển

| Thành viên | GitHub | Vai trò |
|------------|--------|---------|
| **Võ Thanh Nhã** | [@vothnha26](https://github.com/vothnha26) | Project Lead & Backend Dev |
| **Huỳnh Hoài Bảo** | [@BaoBaoIT-maker](https://github.com/BaoBaoIT-maker) | Backend Dev & Frontend Dev |
| **Nguyễn Thành Huy** | [@PhucX](https://github.com/PhucX) | Backend Dev & Frontend Dev|
| **Nguyễn Trọng Phúc** | [@PhucX](https://github.com/PhucX) | Backend Dev & Frontend Dev|

---

## 📞 Liên hệ & Hỗ trợ

- 📧 Email: support@iot-manager.com
- 🐛 Issues: [GitHub Issues](https://github.com/vothnha26/IoT-Device-Manager/issues)
- 📖 Wiki: [GitHub Wiki](https://github.com/vothnha26/IoT-Device-Manager/wiki)
- 💬 Discussions: [GitHub Discussions](https://github.com/vothnha26/IoT-Device-Manager/discussions)

---


## 🙏 Acknowledgments

Cảm ơn các công nghệ và thư viện mã nguồn mở:

- [Spring Boot](https://spring.io/projects/spring-boot)
- [Thymeleaf](https://www.thymeleaf.org/)
- [Bootstrap](https://getbootstrap.com/)
- [Chart.js](https://www.chartjs.org/)
- [Apache POI](https://poi.apache.org/)
- [SockJS](https://github.com/sockjs/sockjs-client)
- [STOMP](https://stomp.github.io/)

---

## 📊 Project Status

![Build Status](https://img.shields.io/badge/build-passing-brightgreen)
![Tests](https://img.shields.io/badge/tests-passing-brightgreen)
![Coverage](https://img.shields.io/badge/coverage-85%25-green)
![Version](https://img.shields.io/badge/version-1.0.0-blue)

---

<div align="center">

**Made with ❤️ by IoT Device Manager Team**

⭐ **Nếu bạn thấy dự án hữu ích, hãy cho chúng tôi một star!** ⭐

[⬆ Back to top](#-iot-device-manager---website-quản-lý-thiết-bị-iot)

</div>
