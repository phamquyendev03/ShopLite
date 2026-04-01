# Cấu Trúc Dự Án (Project Structure Guidelines)

Tài liệu này quy định cấu trúc thư mục của một dự án Spring Boot tiêu chuẩn để các dự án sau dễ dàng kế thừa, tái sử dụng mã nguồn và duy trì tính nhất quán, dễ đọc, dễ bảo trì.

## Tổng quan cấu trúc thư mục

Tất cả code Java sẽ được đặt bên trong package gốc: `vn.hoidanit.<project_name>`

```text
src/main/java/vn/hoidanit/jobhunter/
├── config/             # Cấu hình ứng dụng (Security, CORS, Bean config, v.v.)
├── controller/         # Các API Enpoints (REST Controllers)
├── domain/             # Entities và các đối tượng DTO (Data Transfer Object)
│   ├── request/        # Các class DTO dùng để nhận dữ liệu từ Client (Payload)
│   └── response/       # Các class DTO dùng để trả dữ liệu về cho Client
├── repository/         # Data Access Layer (Spring Data JPA Repositories)
├── service/            # Business Logic Layer (Xử lý nghiệp vụ)
└── util/               # Các class tiện ích (Utilities/Helpers) được dùng chung
    ├── annotation/     # Custom Annotations (ví dụ dùng cho Validation)
    ├── constant/       # Khai báo các hằng số (Constants/Enums)
    └── error/          # Cấu hình xử lý ngoại lệ (Global Exception Handler, Exceptions)
```

## Chi tiết từng Package

### 1. `config/` (Configuration Layer)
- **Nhiệm vụ:** Chứa các class cấu hình của Spring Boot sử dụng annotation `@Configuration`.
- **Phân tích các tệp tiêu biểu cấu thành (dữ liệu mẫu JobHunter):**
  - `SecurityConfiguration.java`: Cấu hình bảo mật chính của Spring Security (phân quyền endpoints, JWT filter, cấu hình CSRF/Session).
  - `CorsConfig.java`: Cấu hình Cross-Origin Resource Sharing (CORS) cho phép frontend ở domain khác gọi API một cách an toàn.
  - `OpenAPIConfig.java`: Cấu hình tự động sinh tài liệu API bằng Swagger/OpenAPI.
  - `DatabaseInitializer.java`: Logic tự động khởi tạo dữ liệu ban đầu (ví dụ: tạo Role, admin template) khi ứng dụng chạy trống.
  - `CustomAuthenticationEntryPoint.java` / `UserDetailsCustom.java`: Tùy chỉnh xử lý ngoại lệ của Security khi bị lỗi xác thực và định nghĩa chi tiết entity để map với Spring Security context.
  - `PermissionInterceptor.java` / `PermissionInterceptorConfiguration.java`: Cấu hình chặn bắt các request (Interceptor) để kiểm tra quyền truy cập động theo user role.
  - `StaticResourcesWebConfiguration.java`: Cấu hình ResourceHandlerRegistry mapping cho tài nguyên tĩnh (phục vụ upload/load files từ storage).
  - `DateTimeFormatConfiguration.java`: Điều chỉnh định dạng `LocalDate`, `LocalDateTime` trả về hoặc nhận vào thống nhất toàn bộ hệ thống API.

### 2. `controller/` (Presentation Layer)
- **Nhiệm vụ:** Tiếp nhận request từ client, điều hướng đến Service xử lý và trả về phản hồi (response).
- **Quy tắc:** 
  - Đánh dấu với `@RestController`.
  - KHÔNG chứa logic nghiệp vụ phúc tạp ở đây, chỉ làm nhiệm vụ gọi Service.
  - Validate dữ liệu đầu vào cơ bản (ví dụ sài `@Valid`).

### 3. `domain/` (Data Model Layer)
Chứa tất cả các Model/Class đại diện cho dữ liệu.
- **Root `domain/`:** Chứa các class Entities map trực tiếp với các bảng trong cơ sở dữ liệu (đánh dấu `@Entity`).
- **`request/`:** Chứa các DTO nhận dữ liệu payload từ phía người dùng (tạo mới, cập nhật).
- **`response/`:** Chứa các DTO trả dữ liệu (đã được định dạng hoặc che giấu thông tin nhạy cảm) về phía Client.

### 4. `repository/` (Data Access Layer)
- **Nhiệm vụ:** Tương tác với Database để CRUD dữ liệu.
- **Quy tắc:** 
  - Thường là các interface kế thừa `JpaRepository` hoặc `PagingAndSortingRepository`.
  - Hạn chế viết native query trừ khi quá phức tạp để tối ưu hiệu suất.

### 5. `service/` (Business Logic Layer)
- **Nhiệm vụ:** Xử lý nghiệp vụ chính của ứng dụng.
- **Quy tắc:**
  - Được tiêm (inject) các Repository hoặc các Service khác.
  - Nên tách ra Interface (tùy chọn nhưng khuyến khích ở dự án lớn).
  - Controller chỉ được phép giao tiếp với hệ thống qua Service.

### 6. `util/` (Utility & Cross-cutting Concerns)
Nơi chứa các công cụ hỗ trợ chung, xử lý chéo cho cả dự án, có thể được gọi từ bất kỳ Layer nào.
- **Các thành phần gốc của `util/`:**
  - `FormatRestResponse.java`: Class chứa `ResponseBodyAdvice`. Mục đích là chặn (intercept) tất cả data trả về (ResponseEntity) và đóng gói vào 1 Object duy nhất JSON tiêu chuẩn ví dụ `{"statusCode": 200, "message": "...", "data": {...}}`.
  - `SecurityUtil.java`: Cung cấp các hàm tĩnh tiện ích sinh/lấy Jwt và trích xuất Current User đang đăng nhập từ `SecurityContextHolder`.
- **`annotation/`**: Chứa Custom Analytics hoặc Validator do Dev tự định nghĩa:
  - `ApiMessage.java`: Dùng để annotate lên hàm API ở Controller, truyền tham số "message" cần trả ra, `FormatRestResponse` sẽ lấy được thông báo đó gắn vào JSON đầu ra.
- **`constant/`**: Chứa hằng số dùng chung (Enum) giúp loại bỏ chuỗi hard-code:
  - Gồm các enum trong dụ án giúp quản lý và mapping CSDL an toàn.
- **`error/`**: Tập trung mọi logic bắt và xử lý lỗi Exception:
  - `GlobalException.java`: Lớp `@RestControllerAdvice`, bắt tất cả các ngoại lệ của dự án (`MethodArgumentNotValidException`, `IdInvalidException`...) và trả về Error JSON thống nhất (thay vì Whitelabel Error Page của Spring).
  - **Custom Exceptions**: Các class lỗi tự định nghĩa (Kế thừa Exception/RuntimeException). Ví dụ `IdInvalidException` (không tìm thấy/ID lỗi), `PermissionException` (truy cập quá quyền hạn), `StorageException` (lỗi thao tác tệp đính kèm).
