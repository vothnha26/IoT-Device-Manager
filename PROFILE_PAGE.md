# Trang Thông Tin Cá Nhân - Tài Liệu Hướng Dẫn

## Tổng Quan

Trang thông tin cá nhân (`/profile`) cho phép người dùng xem và chỉnh sửa thông tin tài khoản của mình, bao gồm đổi mật khẩu, cập nhật email, và xem thống kê sử dụng.

## Các Files Đã Tạo

### Backend

1. **ProfileController.java** (`controller/ui/`)
   - Controller UI để render trang profile
   - Endpoint: `GET /profile`
   - Lấy thông tin người dùng hiện tại và thống kê

2. **UserController.java** (`controller/api/`)
   - REST API để quản lý thông tin người dùng
   - Endpoints:
     - `POST /api/users/change-password` - Đổi mật khẩu
     - `PUT /api/users/update-profile` - Cập nhật thông tin
     - `GET /api/users/profile` - Lấy thông tin profile

### Frontend

3. **profile.html** (`templates/`)
   - Template Thymeleaf cho trang thông tin cá nhân
   - Hiển thị thông tin user, thống kê, và các modal

### DTOs

- **ChangePasswordRequest** - Request đổi mật khẩu
  - Fields: `currentPassword`, `newPassword`
  
- **UpdateProfileRequest** - Request cập nhật profile
  - Fields: `email`

## Tính Năng

### 1. **Hiển Thị Thông Tin Cơ Bản**

**Phần Header (Purple Gradient):**
- Avatar tròn với chữ cái đầu của username
- Tên đăng nhập
- Email

**Thông tin chi tiết:**
- Tên đăng nhập
- Email
- Vai trò (User/Manager) - hiển thị dưới dạng badges
- Trạng thái tài khoản (Đã kích hoạt/Chưa kích hoạt)
- Ngày tạo tài khoản
- Mã người dùng

### 2. **Bảo Mật**

**Đổi Mật Khẩu:**
- Modal form với 3 trường:
  - Mật khẩu hiện tại
  - Mật khẩu mới (tối thiểu 6 ký tự)
  - Xác nhận mật khẩu mới
- Validation:
  - Kiểm tra mật khẩu hiện tại có đúng không
  - Kiểm tra mật khẩu mới >= 6 ký tự
  - Kiểm tra xác nhận khớp với mật khẩu mới
- API: `POST /api/users/change-password`

**Xác thực 2 yếu tố:**
- Hiển thị "Chưa được hỗ trợ" (tính năng tương lai)

### 3. **Chỉnh Sửa Thông Tin**

**Modal Edit Profile:**
- Tên đăng nhập (read-only)
- Email (có thể chỉnh sửa)
- Kiểm tra email đã tồn tại
- API: `PUT /api/users/update-profile`

### 4. **Thống Kê Sử Dụng**

**Stat Boxes (Cột phải):**
- Khu vực quản lý (đếm từ user.khuVucs)
- Thiết bị đang hoạt động (TODO: cần implement)
- Lịch trình tự động (TODO: cần implement)
- Luật ngưỡng (TODO: cần implement)

### 5. **Liên Kết Nhanh**

**Quick Links:**
- Dashboard
- Quản lý khu vực
- Quản lý thiết bị

### 6. **Hành Động Tài Khoản**

- **Chỉnh sửa thông tin**: Mở modal edit profile
- **Xóa tài khoản**: Confirmation dialog (2 lần confirm)
  - Tính năng đang được phát triển (TODO)

## Giao Diện

### Layout

```
+------------------+
|   Navigation     |
+------------------+
|  Profile Header  |
|  (Purple, Avatar)|
+------------------+
| +-------------+--+
| | Left (8col) |R |
| |             |i |
| | - Basic Info|g |
| | - Security  |h |
| | - Actions   |t |
| |             |(4|
| |             |c)|
| +-------------+--+
```

### Màu Sắc

- **Header**: Purple gradient (#667eea → #764ba2)
- **Avatar**: White background, purple text
- **Badges**:
  - User: #3498db (blue)
  - Manager: #e74c3c (red)
- **Status**:
  - Active: #27ae60 (green)
  - Inactive: #e74c3c (red)
- **Stat Numbers**: #667eea (purple)

### Typography

- **Header Title**: 48px bold (trong avatar)
- **Username**: h3
- **Email**: Regular paragraph
- **Stat Numbers**: 36px bold
- **Info Labels**: 14px, #7f8c8d
- **Info Values**: 14px, #2c3e50

## API Endpoints

### 1. Change Password

**Endpoint:** `POST /api/users/change-password`

**Request Body:**
```json
{
  "currentPassword": "old123",
  "newPassword": "new123456"
}
```

**Response Success:**
```json
{
  "success": true,
  "message": "Đổi mật khẩu thành công"
}
```

**Response Error:**
- 400: "Mật khẩu hiện tại không đúng"
- 400: "Mật khẩu mới phải có ít nhất 6 ký tự"
- 401: Unauthorized
- 404: User not found

### 2. Update Profile

**Endpoint:** `PUT /api/users/update-profile`

**Request Body:**
```json
{
  "email": "newemail@example.com"
}
```

**Response Success:**
```json
{
  "success": true,
  "message": "Cập nhật thông tin thành công"
}
```

**Response Error:**
- 400: "Email đã được sử dụng"
- 401: Unauthorized
- 404: User not found

### 3. Get Profile

**Endpoint:** `GET /api/users/profile`

**Response:**
```json
{
  "maNguoiDung": 1,
  "tenDangNhap": "test",
  "email": "test@example.com",
  "kichHoat": true,
  "ngayTao": "2024-01-01T00:00:00",
  "vaiTro": [
    {
      "tenVaiTro": "ROLE_USER"
    }
  ]
}
```

## Cách Sử Dụng

### 1. Truy Cập Trang Profile

**Từ Dashboard:**
- Click vào "Thông tin cá nhân" trong sidebar
- Hoặc truy cập trực tiếp: `/profile`

**Từ Navigation bar:**
- Click vào "Thông tin cá nhân" ở top navigation

### 2. Đổi Mật Khẩu

1. Click nút "Đổi mật khẩu" trong phần Bảo mật
2. Modal hiển thị
3. Nhập:
   - Mật khẩu hiện tại
   - Mật khẩu mới (tối thiểu 6 ký tự)
   - Xác nhận mật khẩu mới
4. Click "Lưu thay đổi"
5. Nếu thành công, modal đóng và hiển thị thông báo

### 3. Cập Nhật Email

1. Click nút "Chỉnh sửa thông tin"
2. Modal hiển thị
3. Sửa email (tên đăng nhập không thể sửa)
4. Click "Lưu thay đổi"
5. Trang reload với thông tin mới

### 4. Xem Thống Kê

- Thống kê tự động hiển thị ở cột bên phải
- Bao gồm:
  - Số khu vực quản lý
  - Thiết bị, lịch trình, luật (đang phát triển)

## Bảo Mật

### Authentication

- Yêu cầu đăng nhập (Authentication required)
- Tự động redirect về `/auth/login` nếu chưa đăng nhập

### Authorization

- User chỉ có thể xem/sửa thông tin của chính mình
- Lấy user từ `Authentication.getName()` (email)

### Password Validation

- Kiểm tra mật khẩu hiện tại bằng `PasswordEncoder.matches()`
- Mật khẩu mới tối thiểu 6 ký tự
- Mã hóa mật khẩu mới bằng `PasswordEncoder.encode()`

### Email Validation

- Kiểm tra email đã tồn tại trong database
- Không cho phép trùng email với user khác

## TODO - Cải Tiến Tương Lai

### 1. Thống Kê Chi Tiết
- [ ] Đếm số thiết bị đang hoạt động
- [ ] Đếm số lịch trình tự động
- [ ] Đếm số luật ngưỡng
- [ ] Biểu đồ hoạt động theo thời gian

### 2. Tính Năng Bổ Sung
- [ ] Upload avatar
- [ ] Xác thực 2 yếu tố (2FA)
- [ ] Lịch sử đăng nhập
- [ ] Quản lý sessions
- [ ] Xóa tài khoản (soft delete)

### 3. UI/UX
- [ ] Toast notifications (thay alert)
- [ ] Loading states
- [ ] Form validation real-time
- [ ] Password strength indicator
- [ ] Dark mode

### 4. Security
- [ ] Rate limiting for password change
- [ ] Email verification for email change
- [ ] Activity log
- [ ] Trusted devices

## Troubleshooting

### Lỗi 401 Unauthorized
- Kiểm tra đã đăng nhập chưa
- Kiểm tra JWT token còn hiệu lực
- Clear cookies và đăng nhập lại

### Không thể đổi mật khẩu
- Kiểm tra mật khẩu hiện tại có đúng không
- Mật khẩu mới phải >= 6 ký tự
- Mật khẩu xác nhận phải khớp

### Email đã tồn tại
- Chọn email khác
- Hoặc kiểm tra email hiện tại của bạn

### Thống kê không hiển thị đúng
- Kiểm tra relationship `user.khuVucs` trong entity
- Kiểm tra FetchType (EAGER/LAZY)

## Kết Luận

Trang thông tin cá nhân đã được triển khai hoàn chỉnh với:

✅ Hiển thị thông tin user đầy đủ
✅ Đổi mật khẩu với validation
✅ Cập nhật email
✅ Thống kê khu vực quản lý
✅ Quick links và navigation
✅ UI/UX đồng nhất với project
✅ Security và authorization

Giao diện được thiết kế theo format của project với:
- Navigation bar giống các trang khác
- Bootstrap 5 components
- Icon từ Bootstrap Icons
- Màu sắc và typography nhất quán
- Responsive design
