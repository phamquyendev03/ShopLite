package com.quyen.shoplite.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quyen.shoplite.domain.Category;
import com.quyen.shoplite.domain.Permission;
import com.quyen.shoplite.domain.Product;
import com.quyen.shoplite.domain.Role;
import com.quyen.shoplite.domain.Unit;
import com.quyen.shoplite.domain.User;
import com.quyen.shoplite.domain.request.ReqLoginDTO;
import com.quyen.shoplite.domain.request.ReqOrderDTO;
import com.quyen.shoplite.domain.request.ReqOrderItemDTO;
import com.quyen.shoplite.repository.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;


import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 🔥 OrderControllerIntegrationTest
 *
 * Bao phủ:
 *  1. Tạo đơn hàng thành công → 201, đầy đủ dữ liệu, stock bị trừ
 *  2. Hết hàng → 400, rollback — stock không đổi
 *  3. Product không tồn tại → 400
 *  4. User (role USER) tạo đơn hàng → THÀNH CÔNG (USER có quyền POST /orders)
 *  5. Không có token → 401 Unauthorized
 *  6. Lấy đơn hàng theo ID thành công → 200
 *  7. Lấy đơn hàng không tồn tại → 400 / 404
 *  8. Role ADMIN cập nhật trạng thái đơn hàng → 200
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OrderControllerIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired UserRepository userRepository;
    @Autowired RoleRepository roleRepository;
    @Autowired PermissionRepository permissionRepository;
    @Autowired ProductRepository productRepository;
    @Autowired CategoryRepository categoryRepository;
    @Autowired UnitRepository unitRepository;
    @Autowired OrderRepository orderRepository;
    @Autowired PasswordEncoder passwordEncoder;

    // Chia sẻ giữa các test
    private static String adminToken;
    private static String userToken;
    private static Integer adminUserId;
    private static Integer regularUserId;
    private static Integer productId;
    private static Integer outOfStockProductId;
    private static Integer createdOrderId;

    // ─────────────────────────────────────────────────────────────────────────
    // SETUP: tạo dữ liệu ban đầu
    // ─────────────────────────────────────────────────────────────────────────

    @BeforeEach
    void setUp() throws Exception {
        // ── Seed permissions cho /api/v1/orders ──────────────────────────────
        Permission pOrderGet  = findOrCreatePermission("Xem orders",   "/api/v1/orders",      "GET",    "ORDERS");
        Permission pOrderPost = findOrCreatePermission("Tạo order",    "/api/v1/orders",       "POST",   "ORDERS");
        Permission pOrderPatch= findOrCreatePermission("Update status", "/api/v1/orders/{id}/status","PATCH","ORDERS");
        Permission pOrderGetId= findOrCreatePermission("Xem order ID", "/api/v1/orders/{id}",  "GET",    "ORDERS");

        // ── Role ADMIN ───────────────────────────────────────────────────────
        Role adminRole = roleRepository.findByName("ADMIN_IT").orElseGet(() ->
                roleRepository.save(Role.builder()
                        .name("ADMIN_IT")
                        .description("Admin integration test")
                        .active(true)
                        .permissions(List.of(pOrderGet, pOrderPost, pOrderPatch, pOrderGetId))
                        .createdAt(LocalDateTime.now())
                        .build())
        );

        // ── Role USER ────────────────────────────────────────────────────────
        Role userRole = roleRepository.findByName("USER_IT").orElseGet(() ->
                roleRepository.save(Role.builder()
                        .name("USER_IT")
                        .description("User integration test - có quyền tạo order")
                        .active(true)
                        .permissions(List.of(pOrderPost, pOrderGet, pOrderGetId))
                        .createdAt(LocalDateTime.now())
                        .build())
        );

        // ── Tạo admin user ────────────────────────────────────────────────────
        if (!userRepository.existsByUsername("it_admin")) {
            User admin = userRepository.save(User.builder()
                    .username("it_admin")
                    .password(passwordEncoder.encode("admin_pass"))
                    .role(adminRole)
                    .isActive(true)
                    .createdAt(LocalDateTime.now())
                    .build());
            adminUserId = admin.getId();
        } else {
            adminUserId = userRepository.findByUsername("it_admin").get().getId();
        }

        // ── Tạo regular user ──────────────────────────────────────────────────
        if (!userRepository.existsByUsername("it_user")) {
            User regular = userRepository.save(User.builder()
                    .username("it_user")
                    .password(passwordEncoder.encode("user_pass"))
                    .role(userRole)
                    .isActive(true)
                    .createdAt(LocalDateTime.now())
                    .build());
            regularUserId = regular.getId();
        } else {
            regularUserId = userRepository.findByUsername("it_user").get().getId();
        }

        // ── Tạo category và product ───────────────────────────────────────────
        Category category = categoryRepository.findAll().stream()
                .findFirst()
                .orElseGet(() -> categoryRepository.save(
                        Category.builder().name("IT-Test-Category").build()
                ));
        Unit unit = unitRepository.findAll().stream()
                .findFirst()
                .orElseGet(() -> unitRepository.save(
                        Unit.builder().name("piece").description("default test unit").build()
                ));

        if (!productRepository.existsBySku("IT-SKU-001")) {
            Product p = productRepository.save(Product.builder()
                    .name("IT Product")
                    .sku("IT-SKU-001")
                    .stock(10)
                    .price(100.0)
                    .category(category)
                    .unit(unit)
                    .isDeleted(false)
                    .createdAt(LocalDateTime.now())
                    .build());
            productId = p.getId();
        } else {
            productId = productRepository.findBySku("IT-SKU-001").get().getId();
        }

        if (!productRepository.existsBySku("IT-SKU-OOS")) {
            Product oos = productRepository.save(Product.builder()
                    .name("Out-of-Stock Product")
                    .sku("IT-SKU-OOS")
                    .stock(0)          // 👈 hết hàng
                    .price(99.0)
                    .category(category)
                    .unit(unit)
                    .isDeleted(false)
                    .createdAt(LocalDateTime.now())
                    .build());
            outOfStockProductId = oos.getId();
        } else {
            outOfStockProductId = productRepository.findBySku("IT-SKU-OOS").get().getId();
        }

        // ── Lấy token nếu chưa có ────────────────────────────────────────────
        if (adminToken == null) {
            adminToken = loginAndGetToken("it_admin", "admin_pass");
        }
        if (userToken == null) {
            userToken = loginAndGetToken("it_user", "user_pass");
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // TEST 1: TẠO ĐƠN HÀNG THÀNH CÔNG (ADMIN)
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @Order(1)
    @DisplayName("✅ POST /orders - Tạo đơn hàng thành công → 201, stock bị trừ")
    void createOrder_Success_Returns201_AndStockDecreased() throws Exception {
        long stockBefore = productRepository.findById(productId).get().getStock();

        ReqOrderDTO req = buildOrder(adminUserId, productId, 2L, 100.0);

        MvcResult result = mockMvc.perform(post("/api/v1/orders")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").isNumber())
                .andExpect(jsonPath("$.data.code", startsWith("ORD-")))
                .andExpect(jsonPath("$.data.status").value("COMPLETED"))
                .andExpect(jsonPath("$.data.totalAmount").value(200.0))
                .andExpect(jsonPath("$.data.items", hasSize(1)))
                .andReturn();

        // Lưu orderId cho test sau
        String body = result.getResponse().getContentAsString();
        createdOrderId = objectMapper.readTree(body)
                .path("data").path("id").asInt();

        // Verify stock đã giảm
        long stockAfter = productRepository.findById(productId).get().getStock();
        Assertions.assertEquals(stockBefore - 2, stockAfter,
                "Stock phải giảm đúng 2 sau khi tạo order");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // TEST 2: HẾT HÀNG → FAIL + ROLLBACK
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @Order(2)
    @DisplayName("❌ POST /orders - Hết hàng → 400, rollback (stock không đổi)")
    void createOrder_OutOfStock_Returns400_AndTransactionRolledBack() throws Exception {
        long stockBefore = productRepository.findById(outOfStockProductId).get().getStock();
        long orderCountBefore = orderRepository.count();

        ReqOrderDTO req = buildOrder(adminUserId, outOfStockProductId, 1L, 99.0);

        mockMvc.perform(post("/api/v1/orders")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",
                        containsStringIgnoringCase("không đủ tồn kho")));

        // Verify rollback: stock không đổi, order không được tạo
        long stockAfter = productRepository.findById(outOfStockProductId).get().getStock();
        long orderCountAfter = orderRepository.count();

        Assertions.assertEquals(stockBefore, stockAfter,
                "🚫 Rollback FAILED: stock đã bị trừ dù transaction lỗi!");
        Assertions.assertEquals(orderCountBefore, orderCountAfter,
                "🚫 Rollback FAILED: order đã được lưu dù transaction lỗi!");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // TEST 3: PRODUCT KHÔNG TỒN TẠI
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @Order(3)
    @DisplayName("❌ POST /orders - Product không tồn tại → 400")
    void createOrder_ProductNotFound_Returns400() throws Exception {
        ReqOrderDTO req = buildOrder(adminUserId, 99999, 1L, 50.0);

        mockMvc.perform(post("/api/v1/orders")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",
                        containsStringIgnoringCase("Không tìm thấy Product")));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // TEST 4: ROLE USER TẠO ORDER → CÓ QUYỀN (USER_IT có POST permission)
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @Order(4)
    @DisplayName("✅ POST /orders - Role USER (có quyền POST) → 201 thành công")
    void createOrder_AsUser_WithPermission_Returns201() throws Exception {
        ReqOrderDTO req = buildOrder(regularUserId, productId, 1L, 100.0);

        mockMvc.perform(post("/api/v1/orders")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").isNumber());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // TEST 5: KHÔNG CÓ TOKEN → 401
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @Order(5)
    @DisplayName("❌ POST /orders - Không có token → 401 Unauthorized")
    void createOrder_WithoutToken_Returns401() throws Exception {
        ReqOrderDTO req = buildOrder(adminUserId, productId, 1L, 100.0);

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // TEST 6: LẤY ĐƠN HÀNG THEO ID
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @Order(6)
    @DisplayName("✅ GET /orders/{id} - Đơn hàng tồn tại → 200 với đầy đủ thông tin")
    void getOrderById_Exists_Returns200() throws Exception {
        if (createdOrderId == null) {
            createOrder_Success_Returns201_AndStockDecreased();
        }

        mockMvc.perform(get("/api/v1/orders/" + createdOrderId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(createdOrderId))
                .andExpect(jsonPath("$.data.code", startsWith("ORD-")))
                .andExpect(jsonPath("$.data.items").isArray());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // TEST 7: ĐƠN HÀNG KHÔNG TỒN TẠI
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @Order(7)
    @DisplayName("❌ GET /orders/{id} - ID không tồn tại → 400")
    void getOrderById_NotFound_Returns400() throws Exception {
        mockMvc.perform(get("/api/v1/orders/999999")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",
                        containsStringIgnoringCase("Không tìm thấy Order")));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // TEST 8: ADMIN CẬP NHẬT TRẠNG THÁI ĐƠN HÀNG
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @Order(8)
    @DisplayName("✅ PATCH /orders/{id}/status - ADMIN cập nhật trạng thái → 200")
    void updateOrderStatus_AsAdmin_Returns200() throws Exception {
        if (createdOrderId == null) {
            createOrder_Success_Returns201_AndStockDecreased();
        }

        mockMvc.perform(patch("/api/v1/orders/" + createdOrderId + "/status")
                        .param("status", "COMPLETED")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("COMPLETED"));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // HELPER METHODS
    // ─────────────────────────────────────────────────────────────────────────

    private String loginAndGetToken(String username, String password) throws Exception {
        ReqLoginDTO req = new ReqLoginDTO();
        req.setUsername(username);
        req.setPassword(password);

        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn();

        String body = result.getResponse().getContentAsString();
        return objectMapper.readTree(body).path("data").path("accessToken").asText();
    }

    private ReqOrderDTO buildOrder(Integer userId, Integer productId, long qty, double price) {
        ReqOrderItemDTO item = new ReqOrderItemDTO();
        item.setProductId(productId);
        item.setQuantity(qty);
        item.setPrice(price);

        ReqOrderDTO order = new ReqOrderDTO();
        order.setUserId(userId);
        order.setDiscount(0.0);
        order.setItems(List.of(item));
        return order;
    }

    private Permission findOrCreatePermission(String name, String apiPath, String method, String module) {
        return permissionRepository.findByApiPathAndMethod(apiPath, method)
                .orElseGet(() -> permissionRepository.save(
                        Permission.builder()
                                .name(name)
                                .apiPath(apiPath)
                                .method(method)
                                .module(module)
                                .createdAt(LocalDateTime.now())
                                .build()
                ));
    }
}
