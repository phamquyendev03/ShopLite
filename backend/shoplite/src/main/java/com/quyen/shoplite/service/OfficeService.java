package com.quyen.shoplite.service;

import com.quyen.shoplite.domain.Office;
import com.quyen.shoplite.domain.request.ReqOfficeDTO;
import com.quyen.shoplite.domain.response.ResOfficeDTO;
import com.quyen.shoplite.repository.OfficeRepository;
import com.quyen.shoplite.util.DTOMapper;
import com.quyen.shoplite.util.error.IdInvalidException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OfficeService {

    private final OfficeRepository officeRepository;

    public ResOfficeDTO create(ReqOfficeDTO req) {
        validateDuplicateName(req.getName(), null);

        Office office = Office.builder()
                .name(req.getName().trim())
                .officeLat(req.getOfficeLat())
                .officeLng(req.getOfficeLng())
                .radius(req.getRadius())
                .build();
        return DTOMapper.toResOfficeDTO(officeRepository.save(office));
    }

    public ResOfficeDTO findById(Integer id) {
        return DTOMapper.toResOfficeDTO(findEntityById(id));
    }

    public List<ResOfficeDTO> findAll() {
        return officeRepository.findAll().stream()
                .map(DTOMapper::toResOfficeDTO)
                .toList();
    }

    public ResOfficeDTO update(Integer id, ReqOfficeDTO req) {
        Office office = findEntityById(id);
        validateDuplicateName(req.getName(), id);

        office.setName(req.getName().trim());
        office.setOfficeLat(req.getOfficeLat());
        office.setOfficeLng(req.getOfficeLng());
        office.setRadius(req.getRadius());
        return DTOMapper.toResOfficeDTO(officeRepository.save(office));
    }

    public void delete(Integer id) {
        if (!officeRepository.existsById(id)) {
            throw new IdInvalidException("Office id=" + id + " not found");
        }
        officeRepository.deleteById(id);
    }

    private Office findEntityById(Integer id) {
        return officeRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Office id=" + id + " not found"));
    }

    private void validateDuplicateName(String name, Integer officeId) {
        String normalizedName = name.trim();
        boolean duplicate = officeRepository.findAll().stream()
                .anyMatch(item -> item.getName() != null
                        && item.getName().equalsIgnoreCase(normalizedName)
                        && !item.getId().equals(officeId));
        if (duplicate) {
            throw new IdInvalidException("Office '" + normalizedName + "' already exists");
        }
    }
}