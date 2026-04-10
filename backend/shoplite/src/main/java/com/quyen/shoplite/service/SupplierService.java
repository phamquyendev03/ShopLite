package com.quyen.shoplite.service;

import com.quyen.shoplite.domain.Supplier;
import com.quyen.shoplite.domain.request.ReqSupplierDTO;
import com.quyen.shoplite.domain.response.ResSupplierDTO;
import com.quyen.shoplite.repository.SupplierRepository;
import com.quyen.shoplite.util.DTOMapper;
import com.quyen.shoplite.util.error.IdInvalidException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository supplierRepository;

    public ResSupplierDTO create(ReqSupplierDTO req) {
        validateDuplicateName(req.getName(), null);

        Supplier supplier = Supplier.builder()
                .name(req.getName().trim())
                .phone(normalize(req.getPhone()))
                .address(normalize(req.getAddress()))
                .email(normalize(req.getEmail()))
                .createdAt(LocalDateTime.now())
                .build();
        return DTOMapper.toResSupplierDTO(supplierRepository.save(supplier));
    }

    public ResSupplierDTO findById(Integer id) {
        return DTOMapper.toResSupplierDTO(findEntityById(id));
    }

    public List<ResSupplierDTO> findAll() {
        return supplierRepository.findAll().stream()
                .map(DTOMapper::toResSupplierDTO)
                .toList();
    }

    public ResSupplierDTO update(Integer id, ReqSupplierDTO req) {
        Supplier supplier = findEntityById(id);
        validateDuplicateName(req.getName(), id);

        supplier.setName(req.getName().trim());
        supplier.setPhone(normalize(req.getPhone()));
        supplier.setAddress(normalize(req.getAddress()));
        supplier.setEmail(normalize(req.getEmail()));
        return DTOMapper.toResSupplierDTO(supplierRepository.save(supplier));
    }

    public void delete(Integer id) {
        if (!supplierRepository.existsById(id)) {
            throw new IdInvalidException("Supplier id=" + id + " not found");
        }
        supplierRepository.deleteById(id);
    }

    private Supplier findEntityById(Integer id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Supplier id=" + id + " not found"));
    }

    private void validateDuplicateName(String name, Integer supplierId) {
        String normalizedName = name.trim();
        boolean duplicate = supplierRepository.findAll().stream()
                .anyMatch(item -> item.getName() != null
                        && item.getName().equalsIgnoreCase(normalizedName)
                        && !item.getId().equals(supplierId));
        if (duplicate) {
            throw new IdInvalidException("Supplier '" + normalizedName + "' already exists");
        }
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}