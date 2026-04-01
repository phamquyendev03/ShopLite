package com.quyen.shoplite.config;

import com.quyen.shoplite.domain.User;
import com.quyen.shoplite.repository.UserRepository;
import com.quyen.shoplite.util.constant.RoleEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Tự động tạo tài khoản admin mặc định khi ứng dụng khởi chạy lần đầu.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .role(RoleEnum.ADMIN)
                    .isActive(true)
                    .createdAt(LocalDate.now())
                    .build();
            userRepository.save(admin);
            log.info("Tạo tài khoản mặc định: username=admin / password=admin123");
        }
    }
}
