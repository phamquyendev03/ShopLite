package com.quyen.shoplite.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quyen.shoplite.domain.Permission;
import com.quyen.shoplite.domain.Role;
import com.quyen.shoplite.domain.User;
import com.quyen.shoplite.domain.request.ReqLoginDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration Test - Auth + Product API với RBAC
 * Dùng H2 in-memory DB (profile "test")
 *
 * Yêu cầu: H2 dependency trong build.gradle.kts:
 *   testRuntimeOnly("com.h2database:h2")
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtEncoder jwtEncoder;

    @Autowired
    private com.quyen.shoplite.repository.UserRepository userRepository;

    @Autowired
    private com.quyen.shoplite.repository.RoleRepository roleRepository;

    @Autowired
    private com.quyen.shoplite.repository.PermissionRepository permissionRepository;

    @org.junit.jupiter.api.BeforeEach
    void setUpUsers() {
        // ── Tạo permission "GET /api/v1/products" nếu chưa có ────────────────
        Permission getProductsPerm = permissionRepository
                .findByApiPathAndMethod("/api/v1/products", "GET")
                .orElseGet(() -> permissionRepository.save(
                        Permission.builder()
                                .name("View Products")
                                .apiPath("/api/v1/products")
                                .method("GET")
                                .module("Product")
                                .build()));

        // ── STAFF ─────────────────────────────────────────────────────────────
        if (!userRepository.existsByUsername("staff1")) {
            // Tìm hoặc tạo role STAFF và gán permission ngay khi tạo mới
            Role staffRole = roleRepository.findByName("STAFF").orElseGet(() -> {
                Role r = new Role();
                r.setName("STAFF");
                r.setActive(true);
                r.getPermissions().add(getProductsPerm);   // Gán trước khi save
                return roleRepository.save(r);
            });
            User staff = new User();
            staff.setUsername("staff1");
            staff.setPassword("encoded");
            staff.setRole(staffRole);
            staff.setActive(true);
            userRepository.save(staff);
        }

        // ── USER ──────────────────────────────────────────────────────────────
        if (!userRepository.existsByUsername("user1")) {
            Role userRole = roleRepository.findByName("USER").orElseGet(() -> {
                Role r = new Role();
                r.setName("USER");
                r.setActive(true);
                r.getPermissions().add(getProductsPerm);   // Gán trước khi save
                return roleRepository.save(r);
            });
            User user = new User();
            user.setUsername("user1");
            user.setPassword("encoded");
            user.setRole(userRole);
            user.setActive(true);
            userRepository.save(user);
        }
    }


    // ─── Helper: tạo JWT token giả ───────────────────────────────────────────

    private String generateToken(String username, String role) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("shoplite")
                .issuedAt(now)
                .expiresAt(now.plus(1, ChronoUnit.HOURS))
                .subject(username)
                .claim("role", role)
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS512).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    private String bearerToken(String username, String role) {
        return "Bearer " + generateToken(username, role);
    }

    // ─── AUTH TESTS ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("Login thành công → 200 + có accessToken")
    void login_Success_Returns200() throws Exception {
        ReqLoginDTO req = new ReqLoginDTO();
        req.setUsername("admin");
        req.setPassword("admin123");  // phải khớp với DatabaseInitializer

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.user.roleName").value("ADMIN"));
    }

    @Test
    @DisplayName("Login sai password → 401 hoặc 400")
    void login_WrongPassword_ReturnsError() throws Exception {
        ReqLoginDTO req = new ReqLoginDTO();
        req.setUsername("admin");
        req.setPassword("wrongpass");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    // ─── NO TOKEN / INVALID TOKEN ────────────────────────────────────────────

    @Test
    @DisplayName("Không có token → GET /api/v1/products → 401")
    void getProducts_NoToken_Returns401() throws Exception {
        mockMvc.perform(get("/api/v1/products"))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Token sai/giả mạo → 401")
    void getProducts_InvalidToken_Returns401() throws Exception {
        mockMvc.perform(get("/api/v1/products")
                        .header("Authorization", "Bearer this.is.not.a.valid.token"))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Token hết hạn → 401")
    void getProducts_ExpiredToken_Returns401() throws Exception {
        // Tạo token đã hết hạn (expiresAt trong quá khứ)
        Instant past = Instant.now().minus(2, ChronoUnit.HOURS);
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("shoplite")
                .issuedAt(past.minus(1, ChronoUnit.HOURS))
                .expiresAt(past)
                .subject("admin")
                .claim("role", "ADMIN")
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS512).build();
        String expiredToken = jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();

        mockMvc.perform(get("/api/v1/products")
                        .header("Authorization", "Bearer " + expiredToken))
                .andExpect(status().is4xxClientError());
    }

    // ─── PRODUCT RBAC TESTS ──────────────────────────────────────────────────

    @Test
    @DisplayName("ADMIN GET /api/v1/products → 200")
    void getProducts_Admin_Returns200() throws Exception {
        mockMvc.perform(get("/api/v1/products")
                        .header("Authorization", bearerToken("admin", "ADMIN")))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("STAFF GET /api/v1/products → 200")
    void getProducts_Staff_Returns200() throws Exception {
        mockMvc.perform(get("/api/v1/products")
                        .header("Authorization", bearerToken("staff1", "STAFF")))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("USER GET /api/v1/products → 200 (xem được)")
    void getProducts_User_Returns200() throws Exception {
        mockMvc.perform(get("/api/v1/products")
                        .header("Authorization", bearerToken("user1", "USER")))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("USER DELETE /api/v1/products/1 → 403 (không có quyền xóa)")
    void deleteProduct_User_Returns403() throws Exception {
        mockMvc.perform(delete("/api/v1/products/1")
                        .header("Authorization", bearerToken("user1", "USER")))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("STAFF DELETE /api/v1/products/1 → 403")
    void deleteProduct_Staff_Returns403() throws Exception {
        mockMvc.perform(delete("/api/v1/products/1")
                        .header("Authorization", bearerToken("staff1", "STAFF")))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("ADMIN DELETE /api/v1/products/999 → 400 (not found, not 403)")
    void deleteProduct_Admin_ProductNotFound_Returns400() throws Exception {
        // ADMIN có quyền, nhưng product 999 không tồn tại → IdInvalidException → 400
        mockMvc.perform(delete("/api/v1/products/999")
                        .header("Authorization", bearerToken("admin", "ADMIN")))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    // ─── USER MANAGEMENT (ADMIN ONLY) ────────────────────────────────────────

    @Test
    @DisplayName("ADMIN GET /api/v1/users → 200")
    void getUsers_Admin_Returns200() throws Exception {
        mockMvc.perform(get("/api/v1/users")
                        .header("Authorization", bearerToken("admin", "ADMIN")))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("USER GET /api/v1/users → 403 (Spring Security level)")
    void getUsers_User_Returns403() throws Exception {
        mockMvc.perform(get("/api/v1/users")
                        .header("Authorization", bearerToken("user1", "USER")))    
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("STAFF GET /api/v1/users → 403")
    void getUsers_Staff_Returns403() throws Exception {
        mockMvc.perform(get("/api/v1/users")
                        .header("Authorization", bearerToken("staff1", "STAFF")))
                .andDo(print())
                .andExpect(status().isForbidden());
    }
}
