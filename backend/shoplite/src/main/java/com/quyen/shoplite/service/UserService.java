package com.quyen.shoplite.service;

import com.quyen.shoplite.domain.Role;
import com.quyen.shoplite.domain.User;
import com.quyen.shoplite.domain.request.ReqUserDTO;
import com.quyen.shoplite.domain.response.ResUserDTO;
import com.quyen.shoplite.repository.RoleRepository;
import com.quyen.shoplite.repository.UserRepository;
import com.quyen.shoplite.util.DTOMapper;
import com.quyen.shoplite.util.error.IdInvalidException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public ResUserDTO create(ReqUserDTO req) {
        if (userRepository.existsByUsername(req.getUsername())) {
            throw new IdInvalidException("Username '" + req.getUsername() + "' đã tồn tại");
        }
        Role role = resolveRole(req.getRoleId());
        User user = User.builder()
                .username(req.getUsername())
                .password(passwordEncoder.encode(req.getPassword()))
                .role(role)
                .isActive(req.isActive())
                .createdAt(LocalDateTime.now())
                .build();
        return DTOMapper.toResUserDTO(userRepository.save(user));
    }

    public ResUserDTO findById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Không tìm thấy User với id=" + id));
        return DTOMapper.toResUserDTO(user);
    }

    public List<ResUserDTO> findAll() {
        return userRepository.findAll().stream()
                .map(DTOMapper::toResUserDTO)
                .toList();
    }

    public ResUserDTO update(Integer id, ReqUserDTO req) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Không tìm thấy User với id=" + id));
        if (req.getRoleId() != null) {
            user.setRole(resolveRole(req.getRoleId()));
        }
        user.setActive(req.isActive());
        if (req.getPassword() != null && !req.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(req.getPassword()));
        }
        return DTOMapper.toResUserDTO(userRepository.save(user));
    }

    public void delete(Integer id) {
        if (!userRepository.existsById(id)) {
            throw new IdInvalidException("Không tìm thấy User với id=" + id);
        }
        userRepository.deleteById(id);
    }

    public User findEntityByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IdInvalidException("Không tìm thấy User: " + username));
    }

    private Role resolveRole(Long roleId) {
        if (roleId == null) return null;
        return roleRepository.findById(roleId)
                .orElseThrow(() -> new IdInvalidException("Không tìm thấy Role id=" + roleId));
    }
}
