# Trang Chi Tiết Thiết Bị - Tài Liệu Hướng Dẫn

## Tổng Quan

Trang chi tiết thiết bị (`/thiet-bi/{id}`) là một trang web toàn diện cho phép người dùng xem thông tin chi tiết, điều khiển trực tiếp, theo dõi dữ liệu thời gian thực, xem biểu đồ lịch sử, và quản lý lịch trình/luật ngưỡng cho từng thiết bị IoT.

## Các Tính Năng Đã Triển Khai

### 1. **Thông Tin Thiết Bị (Section 1)**
- **Mục đích**: Hiển thị thông tin cơ bản của thiết bị
- **Nội dung bao gồm**:
  - Tên thiết bị
  - Loại thiết bị và nhóm thiết bị (CONTROLLER, SENSOR, ACTUATOR)
  - Khu vực đặt thiết bị
  - Trạng thái (Hoạt động/Không hoạt động)
  - Ngày lắp đặt
  - Lần hoạt động cuối
  - Token thiết bị

### 2. **Điều Khiển Trực Tiếp (Section 2)**
- **Mục đích**: Cho phép điều khiển thiết bị ngay lập tức
- **Hiển thị với**: Thiết bị thuộc nhóm CONTROLLER hoặc ACTUATOR
- **Chức năng**:
  - **Nút BẬT/TẮT**: Gửi lệnh ON/OFF đến thiết bị
  - **Chọn màu RGB**: Hiển thị cho thiết bị LED RGB
    - Color picker để chọn màu
    - Hiển thị màu hiện tại
    - Nút "Áp dụng màu" để gửi lệnh RGB (r,g,b)
- **API Endpoint**: `/api/commands/send` (POST)

### 3. **Dữ Liệu Thời Gian Thực (Section 3)**
- **Mục đích**: Hiển thị dữ liệu cảm biến theo thời gian thực
- **Công nghệ**: WebSocket (SockJS + STOMP)
- **Chức năng**:
  - Kết nối WebSocket đến `/ws`
  - Subscribe vào kênh `/topic/device/{deviceId}`
  - Nhận và hiển thị dữ liệu real-time dưới dạng data cards
  - Hiển thị trạng thái kết nối (Đã kết nối/Mất kết nối)
  - Tự động kết nối lại khi mất kết nối

### 4. **Biểu Đồ Lịch Sử Dữ Liệu (Section 4)**
- **Mục đích**: Hiển thị biểu đồ xu hướng dữ liệu
- **Công nghệ**: Chart.js
- **Chức năng**:
  - Chọn khoảng thời gian: 1 giờ, 6 giờ, 24 giờ, 7 ngày
  - Tự động nhóm dữ liệu theo tên trường (tenTruong)
  - Hiển thị nhiều đường biểu đồ cho nhiều trường dữ liệu
  - Trục X: Thời gian, Trục Y: Giá trị
- **API Endpoint**: `/api/data-logs/history/{deviceId}?startTime=&endTime=`

### 5. **Quản Lý Lịch Trình (Section 5)**
- **Mục đích**: Tạo và quản lý lịch trình tự động
- **Chức năng**:
  - Hiển thị danh sách lịch trình của thiết bị
  - Nút "Thêm" mở modal tạo lịch trình mới
  - **Thông tin lịch trình**:
    - Tên lịch trình
    - Thời gian bắt đầu/kết thúc
    - Lệnh khi bắt đầu/kết thúc
    - Ngày trong tuần (*, 1-5, 0,6, hoặc từng ngày cụ thể)
    - Trạng thái kích hoạt
  - Nút xóa lịch trình
- **API Endpoints**:
  - `GET /api/schedules/device/{deviceId}` - Lấy danh sách
  - `POST /api/schedules` - Tạo mới
  - `DELETE /api/schedules/{id}` - Xóa

### 6. **Quản Lý Luật Ngưỡng (Section 6)**
- **Mục đích**: Tạo và quản lý luật tự động dựa trên ngưỡng
- **Chức năng**:
  - Hiển thị danh sách luật ngưỡng
  - Nút "Thêm" mở modal tạo luật mới
  - **Thông tin luật**:
    - Tên trường dữ liệu (temperature, humidity, etc.)
    - Phép toán (>, >=, <, <=, ==, !=)
    - Giá trị ngưỡng
    - Hành động (lệnh sẽ gửi)
    - Trạng thái kích hoạt
  - Nút xóa luật
- **API Endpoints**:
  - `GET /api/rules/device/{deviceId}` - Lấy danh sách
  - `POST /api/rules` - Tạo mới
  - `DELETE /api/rules/{id}` - Xóa

## Cấu Trúc File

### Backend

1. **ThietBiDetailController.java** (`controller/ui/`)
   - Controller UI để render trang device-detail.html
   - Endpoint: `GET /thiet-bi/{id}`
   - Lấy thông tin thiết bị và truyền vào model

2. **LenhDieuKhienController.java** (`controller/api/`)
   - REST API để gửi lệnh điều khiển
   - Endpoints:
     - `POST /api/commands/send` - Gửi lệnh đến thiết bị
     - `GET /api/commands/device/{deviceId}` - Lấy lịch sử lệnh

3. **CommandRequest.java** (`controller/api/`)
   - DTO cho request gửi lệnh
   - Fields: `maThietBi`, `tenLenh`, `giaTriLenh`

4. **LenhDieuKhienRepository.java** (đã cập nhật)
   - Thêm method: `findTop50ByThietBi_MaThietBiOrderByNgayTaoDesc(Long maThietBi)`

### Frontend

1. **device-detail.html** (`templates/`)
   - Template Thymeleaf cho trang chi tiết
   - 6 sections chính như đã mô tả
   - WebSocket client integration
   - Chart.js integration
   - Bootstrap 5 modals cho thêm lịch trình/luật

## API Đã Sử Dụng

### Dữ Liệu (NhatKyDuLieuController)
- `GET /api/data-logs/history/{deviceId}?startTime=&endTime=` - Lấy lịch sử dữ liệu
- `GET /api/data-logs/device/{deviceId}` - Lấy dữ liệu mới nhất

### Lịch Trình (LichTrinhController)
- `GET /api/schedules/device/{deviceId}` - Lấy lịch trình theo thiết bị
- `POST /api/schedules` - Tạo lịch trình mới
- `PUT /api/schedules/{id}` - Cập nhật lịch trình
- `DELETE /api/schedules/{id}` - Xóa lịch trình

### Luật Ngưỡng (LuatNguongController)
- `GET /api/rules/device/{deviceId}` - Lấy luật theo thiết bị
- `POST /api/rules` - Tạo luật mới
- `PUT /api/rules/{id}` - Cập nhật luật
- `DELETE /api/rules/{id}` - Xóa luật

### Lệnh Điều Khiển (LenhDieuKhienController - MỚI)
- `POST /api/commands/send` - Gửi lệnh đến thiết bị
- `GET /api/commands/device/{deviceId}` - Lấy lịch sử lệnh

## WebSocket Integration

### Kết Nối
```javascript
const socket = new SockJS('/ws');
stompClient = Stomp.over(socket);
stompClient.connect({}, callback);
```

### Subscribe
```javascript
stompClient.subscribe('/topic/device/' + deviceId, function(message) {
    const data = JSON.parse(message.body);
    updateRealtimeData(data);
});
```

### Gửi Lệnh qua WebSocket
Khi người dùng gửi lệnh, hệ thống:
1. Lưu lệnh vào database (LenhDieuKhien)
2. Gửi lệnh qua WebSocket đến kênh `/topic/device/{deviceId}`
3. Thiết bị IoT nhận lệnh và thực thi

## Cách Sử Dụng

### 1. Xem Chi Tiết Thiết Bị
- Từ trang `/thiet-bi`, click nút "Xem" trên bất kỳ thiết bị nào
- Tự động chuyển đến `/thiet-bi/{id}`

### 2. Điều Khiển Thiết Bị
- **Bật/Tắt**: Click nút BẬT hoặc TẮT
- **Đổi màu LED RGB**: 
  - Click vào color picker
  - Chọn màu mong muốn
  - Click "Áp dụng màu"

### 3. Theo Dõi Dữ Liệu Real-time
- Dữ liệu tự động cập nhật khi thiết bị gửi dữ liệu mới
- Kiểm tra trạng thái kết nối WebSocket ở góc phải header

### 4. Xem Biểu Đồ Lịch Sử
- Chọn khoảng thời gian (1h, 6h, 24h, 7 ngày)
- Biểu đồ tự động load và hiển thị

### 5. Quản Lý Lịch Trình
- Click "Thêm" để tạo lịch trình mới
- Điền thông tin:
  - Tên lịch trình
  - Thời gian bắt đầu/kết thúc (HH:mm)
  - Lệnh bắt đầu (ví dụ: ON)
  - Lệnh kết thúc (ví dụ: OFF)
  - Chọn ngày trong tuần
- Click "Lưu lịch trình"
- Click nút xóa (🗑️) để xóa lịch trình

### 6. Quản Lý Luật Ngưỡng
- Click "Thêm" để tạo luật mới
- Điền thông tin:
  - Tên trường (temperature, humidity, etc.)
  - Phép toán (>, <, ==, etc.)
  - Giá trị ngưỡng (số)
  - Hành động (lệnh sẽ gửi, ví dụ: OFF)
- Click "Lưu luật"
- Click nút xóa (🗑️) để xóa luật

## Lưu Ý Kỹ Thuật

### 1. Trạng Thái Thiết Bị
- Chỉ có 2 trạng thái: `hoat_dong` và `khong_hoat_dong`
- Sử dụng underscore (_) chứ không phải space

### 2. Nhóm Thiết Bị
- **CONTROLLER**: Hiển thị điều khiển trực tiếp (BẬT/TẮT, RGB)
- **SENSOR**: Chỉ hiển thị dữ liệu, không có điều khiển
- **ACTUATOR**: Hiển thị điều khiển BẬT/TẮT

### 3. WebSocket
- Tự động reconnect khi mất kết nối (sau 5 giây)
- Subscribe vào kênh riêng cho từng thiết bị
- Cần đảm bảo WebSocketConfig đã được cấu hình đúng

### 4. Chart.js
- Sử dụng time scale cho trục X
- Tự động nhóm dữ liệu theo `tenTruong`
- Màu sắc random cho mỗi đường biểu đồ

## Cải Tiến Tương Lai

1. **Thông Báo Toast**: Thay alert() bằng toast library (Bootstrap Toast, Toastify, etc.)
2. **Pagination**: Thêm phân trang cho danh sách lịch trình/luật
3. **Edit Schedule/Rule**: Thêm chức năng chỉnh sửa (hiện tại chỉ có thêm/xóa)
4. **Command History**: Hiển thị lịch sử lệnh đã gửi
5. **Real-time Chart**: Cập nhật biểu đồ real-time khi có dữ liệu mới
6. **Export Data**: Xuất dữ liệu lịch sử ra CSV/Excel
7. **Mobile Responsive**: Tối ưu hóa cho mobile devices
8. **Loading States**: Thêm loading spinner khi call API

## Troubleshooting

### Lỗi 404 khi truy cập /thiet-bi/{id}
- Kiểm tra ThietBiDetailController đã được Spring Boot scan
- Kiểm tra bean name không bị conflict

### WebSocket không kết nối
- Kiểm tra WebSocketConfig đã được cấu hình
- Kiểm tra endpoint /ws có accessible
- Mở console để xem lỗi WebSocket

### Biểu đồ không hiển thị
- Kiểm tra Chart.js đã được load
- Kiểm tra API `/api/data-logs/history/{deviceId}` trả về dữ liệu
- Mở console để xem lỗi

### Lịch trình/Luật không hiển thị
- Kiểm tra API endpoints `/api/schedules/device/{deviceId}` và `/api/rules/device/{deviceId}`
- Kiểm tra controller đã được scan
- Kiểm tra service layer (TuDongHoaService)

## Kết Luận

Trang chi tiết thiết bị đã được triển khai đầy đủ với 6 chức năng chính:
1. ✅ Thông tin thiết bị
2. ✅ Điều khiển trực tiếp (ON/OFF, RGB)
3. ✅ Dữ liệu thời gian thực (WebSocket)
4. ✅ Biểu đồ lịch sử (Chart.js)
5. ✅ Quản lý lịch trình
6. ✅ Quản lý luật ngưỡng

Tất cả các API backend đã có sẵn, frontend đã được tích hợp đầy đủ, và hệ thống sẵn sàng để sử dụng!
