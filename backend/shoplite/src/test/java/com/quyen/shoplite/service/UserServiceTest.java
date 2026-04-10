package com.quyen.shoplite.service;

import com.quyen.shoplite.domain.Role;
import com.quyen.shoplite.domain.User;
import com.quyen.shoplite.domain.request.ReqUserDTO;
import com.quyen.shoplite.domain.response.ResUserDTO;
import com.quyen.shoplite.repository.RoleRepository;
import com.quyen.shoplite.repository.UserRepository;
import com.quyen.shoplite.util.error.BadRequestException;
import com.quyen.shoplite.util.error.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void createUser_success() {
        // Arrange
        ReqUserDTO req = new ReqUserDTO();
        req.setUsername("johndoe");
        req.setPassword("Password123!");
        req.setRoleId(1L);

        Role userRole = new Role();
        userRole.setId(1L);
        userRole.setName("USER");

        User savedUser = User.builder()
                .id(100)
                .username("johndoe")
                .password("encoded_password")
                .role(userRole)
                .isActive(true)
                .build();

        when(userRepository.existsByUsername("johndoe")).thenReturn(false);
        when(roleRepository.findById(1L)).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode("Password123!")).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        ResUserDTO res = userService.create(req);

        // Assert
        assertNotNull(res);
        assertEquals(100, res.getId());
        assertEquals("johndoe", res.getUsername());
        verify(passwordEncoder, times(1)).encode("Password123!");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createUser_duplicateUsername_throwsException() {
        // Arrange
        ReqUserDTO req = new ReqUserDTO();
        req.setUsername("johndoe");

        when(userRepository.existsByUsername("johndoe")).thenReturn(true);

        // Act & Assert
        BadRequestException ex = assertThrows(BadRequestException.class, () -> userService.create(req));
        assertTrue(ex.getMessage().contains("đã tồn tại"));
        verify(userRepository, times(1)).existsByUsername("johndoe");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUserById_success() {
        // Arrange
        User user = new User();
        user.setId(100);
        user.setUsername("johndoe");

        when(userRepository.findById(100)).thenReturn(Optional.of(user));

        // Act
        ResUserDTO res = userService.findById(100);

        // Assert
        assertNotNull(res);
        assertEquals(100, res.getId());
        assertEquals("johndoe", res.getUsername());
    }

    @Test
    void getUserById_userNotFound_throwsException() {
        // Arrange
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> userService.findById(99));
        assertTrue(ex.getMessage().contains("Không tìm thấy User"));
    }

    @Test
    void updateUser_success() {
        // Arrange
        ReqUserDTO req = new ReqUserDTO();
        req.setRoleId(2L);
        req.setPassword("NewPassword!");
        req.setActive(false);

        User existingUser = new User();
        existingUser.setId(1);
        existingUser.setUsername("johndoe");

        Role newRole = new Role();
        newRole.setId(2L);
        newRole.setName("ADMIN");

        when(userRepository.findById(1)).thenReturn(Optional.of(existingUser));
        when(roleRepository.findById(2L)).thenReturn(Optional.of(newRole));
        when(passwordEncoder.encode("NewPassword!")).thenReturn("encoded_new_pass");
        
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ResUserDTO res = userService.update(1, req);

        // Assert
        assertNotNull(res);
        verify(passwordEncoder, times(1)).encode("NewPassword!");
        verify(userRepository, times(1)).save(existingUser);
        assertFalse(existingUser.isActive());
        assertEquals(newRole, existingUser.getRole());
        assertEquals("encoded_new_pass", existingUser.getPassword());
    }
}
