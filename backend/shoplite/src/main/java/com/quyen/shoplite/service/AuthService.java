package com.quyen.shoplite.service;

import com.quyen.shoplite.domain.User;
import com.quyen.shoplite.domain.response.ResLoginDTO;
import com.quyen.shoplite.repository.UserRepository;
import com.quyen.shoplite.util.SecurityUtil;
import com.quyen.shoplite.util.error.IdInvalidException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final SecurityUtil securityUtil;
    private final UserRepository userRepository;

    /**
     * Xác thực username/password → sinh access token + refresh token.
     */
    public ResLoginDTO login(String username, String password) {
        // 1. Xác thực credentials
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));
        } catch (AuthenticationException ex) {
            throw new IdInvalidException("Tên đăng nhập hoặc mật khẩu không đúng");
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 2. Load user để lấy role
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IdInvalidException("Không tìm thấy user: " + username));

        String roleName = (user.getRole() != null) ? user.getRole().getName() : "USER";

        // 3. Sinh tokens
        String accessToken  = securityUtil.generateAccessToken(user.getUsername(), roleName);
        String refreshToken = securityUtil.generateRefreshToken(user.getUsername());

        // 4. Build response
        ResLoginDTO.UserInfo userInfo = new ResLoginDTO.UserInfo(
                user.getId(), user.getUsername(), roleName);

        return new ResLoginDTO(accessToken, refreshToken, userInfo);
    }

    /**
     * Làm mới access token bằng refresh token hợp lệ.
     */
    public ResLoginDTO refresh(Jwt refreshJwt) {
        String username = refreshJwt.getSubject();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IdInvalidException("Không tìm thấy user: " + username));

        String roleName = (user.getRole() != null) ? user.getRole().getName() : "USER";

        String newAccessToken  = securityUtil.generateAccessToken(user.getUsername(), roleName);
        String newRefreshToken = securityUtil.generateRefreshToken(user.getUsername());

        ResLoginDTO.UserInfo userInfo = new ResLoginDTO.UserInfo(
                user.getId(), user.getUsername(), roleName);

        return new ResLoginDTO(newAccessToken, newRefreshToken, userInfo);
    }
}
