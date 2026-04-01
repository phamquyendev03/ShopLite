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
        // 1. Xác thực credentials qua AuthenticationManager
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));

        // Lưu vào SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 2. Load user entity để lấy role và id
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IdInvalidException("Không tìm thấy user: " + username));

        // 3. Sinh tokens
        String accessToken  = securityUtil.generateAccessToken(user.getUsername(), user.getRole().name());
        String refreshToken = securityUtil.generateRefreshToken(user.getUsername());

        // 4. Build response
        ResLoginDTO.UserInfo userInfo = new ResLoginDTO.UserInfo(
                user.getId(), user.getUsername(), user.getRole());

        return new ResLoginDTO(accessToken, refreshToken, userInfo);
    }

    /**
     * Làm mới access token bằng refresh token hợp lệ.
     */
    public ResLoginDTO refresh(Jwt refreshJwt) {
        String username = refreshJwt.getSubject();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IdInvalidException("Không tìm thấy user: " + username));

        String newAccessToken  = securityUtil.generateAccessToken(user.getUsername(), user.getRole().name());
        String newRefreshToken = securityUtil.generateRefreshToken(user.getUsername());

        ResLoginDTO.UserInfo userInfo = new ResLoginDTO.UserInfo(
                user.getId(), user.getUsername(), user.getRole());

        return new ResLoginDTO(newAccessToken, newRefreshToken, userInfo);
    }
}
