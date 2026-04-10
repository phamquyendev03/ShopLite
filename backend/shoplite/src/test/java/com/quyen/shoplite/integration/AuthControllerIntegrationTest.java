package com.quyen.shoplite.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quyen.shoplite.domain.Permission;
import com.quyen.shoplite.domain.Role;
import com.quyen.shoplite.domain.User;
import com.quyen.shoplite.domain.request.ReqLoginDTO;
import com.quyen.shoplite.repository.PermissionRepository;
import com.quyen.shoplite.repository.RoleRepository;
import com.quyen.shoplite.repository.UserRepository;
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
 * ✅ AuthControllerIntegrationTest
 *
 * Bao phủ:
 *  1. Login thành công → trả về accessToken, refreshToken, userInfo
 *  2. Login sai mật khẩu → 400
 *  3. Login username không tồn tại → 400
 *  4. JWT hoạt động: dùng token gọi endpoint bảo mật → 200
 *  5. Gọi endpoint bảo mật không có token → 401
 *  6. Gọi /auth/me với token hợp lệ → trả về username
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthControllerIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired UserRepository userRepository;
    @Autowired RoleRepository roleRepository;
    @Autowired PermissionRepository permissionRepository;
    @Autowired PasswordEncoder passwordEncoder;

    private static String accessToken;   // chia sẻ giữa các test

    @BeforeEach
    void setUp() {
        // Seed permissions cần thiết cho /auth/me
        Permission pAuthMe = findOrCreatePermission(
                "Xem current user", "/api/v1/auth/me", "GET", "AUTH");
        // Seed permissions cho /orders (dùng khi test JWT với orders)
        Permission pOrderGet = findOrCreatePermission(
                "Xem orders", "/api/v1/orders", "GET", "ORDERS");

        // Tạo role ADMIN_AUTH với đầy đủ quyền cần test
        Role adminRole = roleRepository.findByName("ADMIN_AUTH").orElseGet(() ->
                roleRepository.save(Role.builder()
                        .name("ADMIN_AUTH")
                        .description("Admin role for auth tests")
                        .active(true)
                        .permissions(List.of(pAuthMe, pOrderGet))
                        .createdAt(LocalDateTime.now())
                        .build())
        );

        // Tạo user test nếu chưa có
        if (!userRepository.existsByUsername("testadmin")) {
            userRepository.save(User.builder()
                    .username("testadmin")
                    .password(passwordEncoder.encode("secret123"))
                    .role(adminRole)
                    .isActive(true)
                    .createdAt(LocalDateTime.now())
                    .build());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 1. LOGIN THÀNH CÔNG
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @Order(1)
    @DisplayName("✅ POST /auth/login - Login thành công → 200, có đủ token và userInfo")
    void login_Success_Returns200WithTokens() throws Exception {
        ReqLoginDTO req = new ReqLoginDTO();
        req.setUsername("testadmin");
        req.setPassword("secret123");

        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.refreshToken").isNotEmpty())
                .andExpect(jsonPath("$.data.user.username").value("testadmin"))
                .andExpect(jsonPath("$.data.user.roleName").value("ADMIN_AUTH"))
                .andReturn();

        // Lưu lại token cho các test sau
        String body = result.getResponse().getContentAsString();
        accessToken = objectMapper.readTree(body)
                .path("data").path("accessToken").asText();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 2. LOGIN SAI MẬT KHẨU
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @Order(2)
    @DisplayName("❌ POST /auth/login - Sai mật khẩu → 400 Bad Request")
    void login_WrongPassword_Returns400() throws Exception {
        ReqLoginDTO req = new ReqLoginDTO();
        req.setUsername("testadmin");
        req.setPassword("wrong_password");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",
                        containsStringIgnoringCase("không đúng")));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 3. USERNAME KHÔNG TỒN TẠI
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @Order(3)
    @DisplayName("❌ POST /auth/login - Username không tồn tại → 400 Bad Request")
    void login_UserNotFound_Returns400() throws Exception {
        ReqLoginDTO req = new ReqLoginDTO();
        req.setUsername("ghost_user_xyz");
        req.setPassword("anyPass");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 4. JWT HOẠT ĐỘNG: gọi endpoint cần auth với token hợp lệ → 200
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @Order(4)
    @DisplayName("✅ GET /auth/me - Token hợp lệ → trả về username")
    void getMe_WithValidToken_ReturnsUsername() throws Exception {
        // Lấy fresh token trực tiếp (không phụ thuộc vào static field từ test khác)
        ReqLoginDTO loginReq = new ReqLoginDTO();
        loginReq.setUsername("testadmin");
        loginReq.setPassword("secret123");

        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andReturn();

        String freshToken = objectMapper.readTree(loginResult.getResponse().getContentAsString())
                .path("data").path("accessToken").asText();

        mockMvc.perform(get("/api/v1/auth/me")
                        .header("Authorization", "Bearer " + freshToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("testadmin"));
    }

    // Helper
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

    // ─────────────────────────────────────────────────────────────────────────
    // 5. KHÔNG CÓ TOKEN → 401
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @Order(5)
    @DisplayName("❌ GET /auth/me - Không có token → 401 Unauthorized")
    void getMe_WithoutToken_Returns401() throws Exception {
        mockMvc.perform(get("/api/v1/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 6. TOKEN GIẢ → 401
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @Order(6)
    @DisplayName("❌ GET /auth/me - Token giả → 401 Unauthorized")
    void getMe_WithFakeToken_Returns401() throws Exception {
        mockMvc.perform(get("/api/v1/auth/me")
                        .header("Authorization", "Bearer this.is.a.fake.token"))
                .andExpect(status().isUnauthorized());
    }
}
