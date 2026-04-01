package com.quyen.shoplite.config;

import com.quyen.shoplite.domain.User;
import com.quyen.shoplite.repository.UserRepository;
import com.quyen.shoplite.util.error.IdInvalidException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * Implement UserDetailsService để Spring Security load user từ database.
 * Được dùng trong JwtAuthenticationFilter để xác thực token.
 */
@Component
@RequiredArgsConstructor
public class UserDetailsCustom implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy user: " + username));

        if (!user.isActive()) {
            throw new IdInvalidException("Tài khoản '" + username + "' đã bị vô hiệu hóa");
        }

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(Collections.singletonList(
                        new SimpleGrantedAuthority("ROLE_" + user.getRole().name())))
                .build();
    }
}
