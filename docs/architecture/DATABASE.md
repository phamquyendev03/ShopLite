# 📦 Database Design - Mini ERP / Order Management

## 1. Overview
Hệ thống quản lý:
- Người dùng (User)
- Đơn hàng (Order)
- Sản phẩm (Product)
- Tồn kho (Inventory)
- Giao dịch (Transaction)

---

## 2. ERD Summary

### Quan hệ chính:
- User 1 - N Order
- Order 1 - N OrderItems
- Product 1 - N OrderItems
- Product 1 - N InventoryLogs
- Order 1 - N Transaction

---

## 3. Tables

---

### 🧑‍💻 User

| Field       | Type    | Description |
|------------|--------|------------|
| id         | int    | PK |
| username   | String | Tên đăng nhập |
| password   | String | Mật khẩu |
| role       | Enum(Admin, Staff) | Vai trò |
| is_active  | boolean | Trạng thái |
| created_at | Date   | Ngày tạo |

---

### 🧾 Order

| Field          | Type    | Description |
|---------------|--------|------------|
| id            | int    | PK |
| user_id       | int    | FK → User |
| code          | String | Mã đơn |
| customer_name | String | Tên khách |
| total_amount  | Double | Tổng tiền |
| discount      | Double | Giảm giá |
| status        | Enum   | Trạng thái |
| payment_method| Enum   | Phương thức thanh toán |
| created_at    | Date   | Ngày tạo |
| paid_at       | Date   | Ngày thanh toán |

---

### 📦 OrderItems

| Field       | Type    | Description |
|------------|--------|------------|
| id         | int    | PK |
| order_id   | int    | FK → Order |
| product_id | int    | FK → Product |
| product_name | String | Snapshot tên sản phẩm |
| quantity   | Long   | Số lượng |
| price      | Double | Giá |
| total_price| Double | Tổng |

---

### 🛒 Product

| Field       | Type    | Description |
|------------|--------|------------|
| id         | int    | PK |
| category_id| int    | FK → Category |
| name       | String | Tên sản phẩm |
| sku        | String | Mã SKU |
| stock      | Long   | Tồn kho |
| price      | Double | Giá |
| is_deleted | boolean | Soft delete |
| created_at | Date   | Ngày tạo |

---

### 🗂 Category

| Field | Type   | Description |
|------|-------|------------|
| id   | int   | PK |
| name | String| Tên danh mục |

---

### 📊 InventoryLogs

| Field          | Type    | Description |
|---------------|--------|------------|
| id            | int    | PK |
| product_id    | int    | FK → Product |
| change_quantity| String | Số lượng thay đổi |
| type          | Enum   | Loại thay đổi |
| reference_id  | int    | ID tham chiếu (Order, etc.) |
| created_at    | String | Ngày tạo |

---

### 💳 Transaction

| Field           | Type     | Description |
|----------------|----------|------------|
| id             | int      | PK |
| external_id    | String   | ID bên ngoài (bank) |
| bank_code      | String   | Mã ngân hàng |
| amount         | Double   | Số tiền |
| type           | Enum     | IN / OUT |
| content        | String   | Nội dung |
| transaction_time | DateTime | Thời gian giao dịch |
| created_at     | DateTime | Ngày tạo |
| order_id       | int      | FK → Order |

---

## 4. Enums

---

### 🔁 TypeTransactionEnum
- IN
- OUT

---

### 📦 TypeInventoryEnum
- IMPORT
- SALE
- ADJUST
- CANCEL

---

### 📌 StatusEnum
- PENDING
- PAID
- CANCELLED

---

### 💰 PaymentMethodEnum
- CASH
- BANK

---

## 5. Key Design Notes

### ✅ Snapshot Product
- `product_name` trong OrderItems giúp giữ lịch sử khi product bị sửa.

### ✅ Inventory Tracking
- InventoryLogs giúp audit thay đổi kho.

### ✅ Transaction riêng biệt
- Tách Transaction để xử lý:
  - Bank API
  - Đối soát
  - Refund

### ✅ Soft Delete Product
- Tránh mất dữ liệu lịch sử đơn hàng.

---

## 6. Possible Improvements (Recommend 🔥)

- ❗ `change_quantity` nên là `Long` (không phải String)
- ❗ `created_at` nên dùng DateTime (không phải String)
- ❗ thêm:
  - `order_code` unique
  - index cho `sku`
- ❗ thêm bảng:
  - `stock_reservation` (giữ hàng khi đặt)

---

## 7. Classic Problems Covered

- Quản lý tồn kho (Inventory consistency)
- Snapshot dữ liệu lịch sử
- Thanh toán & transaction tracking
- Audit log (InventoryLogs)
- Soft delete vs hard delete

---

## 8. Architecture Fit

Phù hợp:
- Monolith (Spring Boot)
- Microservice (tách Order / Inventory / Payment)

---