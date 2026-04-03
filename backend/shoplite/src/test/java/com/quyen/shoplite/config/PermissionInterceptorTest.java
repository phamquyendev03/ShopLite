package com.quyen.shoplite.config;

import com.quyen.shoplite.domain.Permission;
import com.quyen.shoplite.domain.Role;
import com.quyen.shoplite.domain.User;
import com.quyen.shoplite.repository.UserRepository;
import com.quyen.shoplite.util.error.PermissionException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.HandlerMapping;

import java.util.List;
import java.util.Optional;

import com.quyen.shoplite.util.SecurityUtil;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit Test - PermissionInterceptor (RBAC core logic)
 */
@ExtendWith(MockitoExtension.class)
class PermissionInterceptorTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PermissionInterceptor permissionInterceptor;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private Role adminRole;
    private Role userRole;
    private User adminUser;
    private User regularUser;
    private Permission createProductPermission;

    @BeforeEach
    void setUp() {
        createProductPermission = Permission.builder()
                .id(1L)
                .name("Create Product")
                .apiPath("/api/v1/products")
                .method("POST")
                .module("Product")
                .build();

        Permission viewProductPermission = Permission.builder()
                .id(2L)
                .name("View Product")
                .apiPath("/api/v1/products/{id}")
                .method("GET")
                .module("Product")
                .build();

        adminRole = Role.builder()
                .id(1L).name("ADMIN")
                .permissions(List.of(createProductPermission, viewProductPermission))
                .build();

        userRole = Role.builder()
                .id(3L).name("USER")
                .permissions(List.of(viewProductPermission))   // chỉ có quyền xem
                .build();

        adminUser = User.builder()
                .id(1).username("admin").role(adminRole).isActive(true).build();

        regularUser = User.builder()
                .id(3).username("user1").role(userRole).isActive(true).build();
    }

    @Test
    @DisplayName("✅ ADMIN POST /api/v1/products → được phép")
    void preHandle_AdminCreateProduct_Allowed() {
        when(request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE))
                .thenReturn("/api/v1/products");
        when(request.getMethod()).thenReturn("POST");
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));

        try (MockedStatic<SecurityUtil> util = mockStatic(SecurityUtil.class)) {
            util.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin"));

            boolean result = permissionInterceptor.preHandle(request, response, new Object());

            assertThat(result).isTrue();
        }
    }

    @Test
    @DisplayName("❌ USER POST /api/v1/products → ném PermissionException")
    void preHandle_UserCreateProduct_ThrowsPermissionException() {
        when(request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE))
                .thenReturn("/api/v1/products");
        when(request.getMethod()).thenReturn("POST");
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(regularUser));

        try (MockedStatic<SecurityUtil> util = mockStatic(SecurityUtil.class)) {
            util.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("user1"));

            assertThatThrownBy(() ->
                    permissionInterceptor.preHandle(request, response, new Object()))
                    .isInstanceOf(PermissionException.class)
                    .hasMessageContaining("POST")
                    .hasMessageContaining("/api/v1/products");
        }
    }

    @Test
    @DisplayName("✅ USER GET /api/v1/products/{id} → được phép xem")
    void preHandle_UserViewProduct_Allowed() {
        when(request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE))
                .thenReturn("/api/v1/products/{id}");
        when(request.getMethod()).thenReturn("GET");
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(regularUser));

        try (MockedStatic<SecurityUtil> util = mockStatic(SecurityUtil.class)) {
            util.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("user1"));

            boolean result = permissionInterceptor.preHandle(request, response, new Object());

            assertThat(result).isTrue();
        }
    }

    @Test
    @DisplayName("✅ Chưa đăng nhập (username rỗng) → bỏ qua, trả về true")
    void preHandle_NotLoggedIn_SkipsCheck() {
        try (MockedStatic<SecurityUtil> util = mockStatic(SecurityUtil.class)) {
            util.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.empty());

            boolean result = permissionInterceptor.preHandle(request, response, new Object());

            assertThat(result).isTrue();
            verify(userRepository, never()).findByUsername(any());
        }
    }

    @Test
    @DisplayName("❌ User chưa được gán Role → ném PermissionException")
    void preHandle_UserWithNoRole_ThrowsPermissionException() {
        User noRoleUser = User.builder()
                .id(5).username("norole").role(null).isActive(true).build();

        when(request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE))
                .thenReturn("/api/v1/products");
        when(request.getMethod()).thenReturn("GET");
        when(userRepository.findByUsername("norole")).thenReturn(Optional.of(noRoleUser));

        try (MockedStatic<SecurityUtil> util = mockStatic(SecurityUtil.class)) {
            util.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("norole"));

            assertThatThrownBy(() ->
                    permissionInterceptor.preHandle(request, response, new Object()))
                    .isInstanceOf(PermissionException.class)
                    .hasMessageContaining("chưa được gán Role");
        }
    }

    @Test
    @DisplayName("✅ User không tồn tại trong DB → bỏ qua, trả về true")
    void preHandle_UserNotInDb_SkipsCheck() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        try (MockedStatic<SecurityUtil> util = mockStatic(SecurityUtil.class)) {
            util.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("ghost"));

            boolean result = permissionInterceptor.preHandle(request, response, new Object());

            assertThat(result).isTrue();
        }
    }
}
