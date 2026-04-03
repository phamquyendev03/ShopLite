package com.quyen.shoplite.service;

import com.quyen.shoplite.domain.Permission;
import com.quyen.shoplite.domain.request.ReqPermissionDTO;
import com.quyen.shoplite.domain.response.ResPermissionDTO;
import com.quyen.shoplite.repository.PermissionRepository;
import com.quyen.shoplite.util.error.IdInvalidException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepository;

    // ─── Create ────────────────────────────────────────────────────────────────
    public ResPermissionDTO create(ReqPermissionDTO req) {
        if (permissionRepository.existsByModuleAndApiPathAndMethod(
                req.getModule(), req.getApiPath(), req.getMethod())) {
            throw new IdInvalidException(
                    "Permission [" + req.getMethod() + " " + req.getApiPath() + "] đã tồn tại trong module " + req.getModule());
        }
        Permission p = Permission.builder()
                .name(req.getName())
                .apiPath(req.getApiPath())
                .method(req.getMethod().toUpperCase())
                .module(req.getModule().toUpperCase())
                .createdAt(LocalDateTime.now())
                .build();
        return toDTO(permissionRepository.save(p));
    }

    // ─── Get by ID ─────────────────────────────────────────────────────────────
    public ResPermissionDTO findById(Long id) {
        Permission p = permissionRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Không tìm thấy Permission id=" + id));
        return toDTO(p);
    }

    // ─── Update ────────────────────────────────────────────────────────────────
    public ResPermissionDTO update(Long id, ReqPermissionDTO req) {
        Permission p = permissionRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Không tìm thấy Permission id=" + id));
        p.setName(req.getName());
        p.setApiPath(req.getApiPath());
        p.setMethod(req.getMethod().toUpperCase());
        p.setModule(req.getModule().toUpperCase());
        p.setUpdatedAt(LocalDateTime.now());
        return toDTO(permissionRepository.save(p));
    }

    // ─── Delete ────────────────────────────────────────────────────────────────
    public void delete(Long id) {
        Permission p = permissionRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Không tìm thấy Permission id=" + id));
        // Xóa relationship với roles trước
        p.getRoles().forEach(role -> role.getPermissions().remove(p));
        permissionRepository.delete(p);
    }

    // ─── Get All (paginated) ───────────────────────────────────────────────────
    public Map<String, Object> getAll(Pageable pageable) {
        Page<Permission> page = permissionRepository.findAll(pageable);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalElements", page.getTotalElements());
        result.put("totalPages", page.getTotalPages());
        result.put("page", pageable.getPageNumber());
        result.put("size", pageable.getPageSize());
        result.put("data", page.getContent().stream().map(this::toDTO).toList());
        return result;
    }

    // ─── Internal: find entities by IDs (used by RoleService) ─────────────────
    public List<Permission> findAllByIds(List<Long> ids) {
        return permissionRepository.findAllById(ids);
    }

    // ─── Mapper ────────────────────────────────────────────────────────────────
    public ResPermissionDTO toDTO(Permission p) {
        ResPermissionDTO dto = new ResPermissionDTO();
        dto.setId(p.getId());
        dto.setName(p.getName());
        dto.setApiPath(p.getApiPath());
        dto.setMethod(p.getMethod());
        dto.setModule(p.getModule());
        dto.setCreatedAt(p.getCreatedAt());
        dto.setUpdatedAt(p.getUpdatedAt());
        dto.setCreatedBy(p.getCreatedBy());
        dto.setUpdatedBy(p.getUpdatedBy());
        return dto;
    }
}
