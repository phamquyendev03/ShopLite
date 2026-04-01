package com.quyen.shoplite.controller;

import com.quyen.shoplite.domain.request.ReqLoginDTO;
import com.quyen.shoplite.domain.response.ResLoginDTO;
import com.quyen.shoplite.service.AuthService;
import com.quyen.shoplite.util.annotation.ApiMessage;
import com.quyen.shoplite.util.error.IdInvalidException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * POST /api/v1/auth/login
     * Body: { "username": "...", "password": "..." }
     * Response: { "statusCode": 200, "message": "...", "data": { "accessToken", "refreshToken", "user" } }
     */
    @PostMapping("/login")
    @ApiMessage("Đăng nhập thành công")
    public ResponseEntity<ResLoginDTO> login(@RequestBody ReqLoginDTO req) {
        ResLoginDTO result = authService.login(req.getUsername(), req.getPassword());
        return ResponseEntity.ok(result);
    }

    /**
     * POST /api/v1/auth/refresh
     * Header: Authorization: Bearer <refresh_token>
     * Spring OAuth2 Resource Server sẽ xác thực refresh token trước khi vào đây.
     */
    @PostMapping("/refresh")
    @ApiMessage("Làm mới token thành công")
    public ResponseEntity<ResLoginDTO> refresh(@AuthenticationPrincipal Jwt refreshJwt) {
        if (refreshJwt == null) {
            throw new IdInvalidException("Refresh token không hợp lệ");
        }
        return ResponseEntity.ok(authService.refresh(refreshJwt));
    }

    /**
     * GET /api/v1/auth/me - lấy thông tin user hiện tại
     */
    @GetMapping("/me")
    @ApiMessage("Lấy thông tin người dùng hiện tại")
    public ResponseEntity<String> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        if (jwt == null) {
            throw new IdInvalidException("Chưa đăng nhập");
        }
        return ResponseEntity.ok(jwt.getSubject());
    }
}
