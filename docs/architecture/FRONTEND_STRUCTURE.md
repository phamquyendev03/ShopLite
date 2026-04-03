src/app/
├── core/                         # Các dịch vụ hệ thống và cấu hình bảo mật
│   ├── guards/
│   │   └── auth.guard.ts         # Kiểm tra trạng thái đăng nhập
│   ├── interceptors/
│   │   └── jwt.interceptor.ts    # Tự động đính kèm Token vào Header
│   └── services/
│       ├── auth.service.ts       # Xử lý Login, Logout, Refresh Token
│       └── storage.service.ts    # Lưu trữ Token vào LocalStorage/Preferences
├── models/                       # Định nghĩa TypeScript Interfaces (Khớp với Backend DTOs)
│   ├── auth.model.ts             # Mapping ResLoginDTO
│   ├── product.model.ts          # Mapping ResProductDTO
│   ├── category.model.ts         # Mapping ResCategoryDTO
│   └── order.model.ts            # Mapping ReqOrderDTO & ResOrderDTO
├── services/                     # Lớp giao tiếp API
│   ├── product.service.ts        # Gọi API /api/v1/products
│   ├── category.service.ts       # Gọi API /api/v1/categories
│   └── order.service.ts          # Gọi API /api/v1/orders
├── pages/                        # Các trang giao diện (Standalone Components)
│   ├── login/                    # Trang đăng nhập
│   ├── product-list/             # Danh sách sản phẩm (có phân trang)
│   ├── product-detail/           # Chi tiết sản phẩm
│   └── cart/                     # Giỏ hàng & Thanh toán
└── shared/                       # Các thành phần dùng chung
    └── components/               # ProductCard, Header, v.v.