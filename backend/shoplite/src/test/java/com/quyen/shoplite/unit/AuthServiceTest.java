package com.quyen.shoplite.unit;

import com.quyen.shoplite.domain.User;
import com.quyen.shoplite.domain.Role;
import com.quyen.shoplite.domain.response.ResLoginDTO;
import com.quyen.shoplite.repository.UserRepository;
import com.quyen.shoplite.service.AuthService;
import com.quyen.shoplite.util.SecurityUtil;
import com.quyen.shoplite.util.error.IdInvalidException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private SecurityUtil securityUtil;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthService authService;

    private User adminUser;
    private User staffUser;

    @BeforeEach
    void setUp() {
        Role adminRole = Role.builder().id(1L).name("ADMIN").build();
        Role staffRole = Role.builder().id(2L).name("STAFF").build();

        adminUser = User.builder()
                .id(1).username("admin").password("hashed").role(adminRole).isActive(true).build();
        staffUser = User.builder()
                .id(2).username("staff").password("hashed").role(staffRole).isActive(true).build();
    }

    // ─── LOGIN ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("✅ ADMIN đăng nhập thành công → trả về token + role ADMIN")
    void login_Admin_Success() {
        Authentication auth = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));
        when(securityUtil.generateAccessToken("admin", "ADMIN")).thenReturn("access.token.admin");
        when(securityUtil.generateRefreshToken("admin")).thenReturn("refresh.token.admin");

        ResLoginDTO result = authService.login("admin", "password");

        assertThat(result.getAccessToken()).isEqualTo("access.token.admin");
        assertThat(result.getUser().getRoleName()).isEqualTo("ADMIN");
    }

    @Test
    @DisplayName("✅ STAFF đăng nhập → role đúng là STAFF")
    void login_Staff_CorrectRole() {
        Authentication auth = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(userRepository.findByUsername("staff")).thenReturn(Optional.of(staffUser));
        when(securityUtil.generateAccessToken("staff", "STAFF")).thenReturn("token");
        when(securityUtil.generateRefreshToken("staff")).thenReturn("refresh");

        ResLoginDTO result = authService.login("staff", "password");

        assertThat(result.getUser().getRoleName()).isEqualTo("STAFF");
    }

    @Test
    @DisplayName("✅ USER không có role → mặc định là USER")
    void login_UserWithNullRole_DefaultsToUser() {
        User noRoleUser = User.builder()
                .id(4).username("norole").password("hashed").role(null).isActive(true).build();
        Authentication auth = mock(Authentication.class);

        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(userRepository.findByUsername("norole")).thenReturn(Optional.of(noRoleUser));
        when(securityUtil.generateAccessToken("norole", "USER")).thenReturn("token");
        when(securityUtil.generateRefreshToken("norole")).thenReturn("refresh");

        ResLoginDTO result = authService.login("norole", "password");

        assertThat(result.getUser().getRoleName()).isEqualTo("USER");
    }

    @Test
    @DisplayName("❌ Sai mật khẩu → AuthService ném IdInvalidException")
    void login_WrongPassword_ThrowsIdInvalidException() {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Sai thông tin đăng nhập"));

        assertThatThrownBy(() -> authService.login("admin", "wrongpass"))
                .isInstanceOf(IdInvalidException.class)
                .hasMessageContaining("Tên đăng nhập hoặc mật khẩu không đúng");

        verify(userRepository, never()).findByUsername(any());
    }

    @Test
    @DisplayName("✅ Login response chứa đủ accessToken, refreshToken, userInfo")
    void login_ResponseStructure_Complete() {
        Authentication auth = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));
        when(securityUtil.generateAccessToken(any(), any())).thenReturn("at");
        when(securityUtil.generateRefreshToken(any())).thenReturn("rt");

        ResLoginDTO result = authService.login("admin", "password");

        assertThat(result.getAccessToken()).isNotNull();
        assertThat(result.getRefreshToken()).isNotNull();
        assertThat(result.getUser()).isNotNull();
        assertThat(result.getUser().getUsername()).isEqualTo("admin");
    }

    @Test
    @DisplayName("❌ User không tồn tại trong DB sau khi auth → ném IdInvalidException")
    void login_UserNotInDb_ThrowsException() {
        Authentication auth = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login("ghost", "pass"))
                .isInstanceOf(IdInvalidException.class)
                .hasMessageContaining("ghost");
    }
}
