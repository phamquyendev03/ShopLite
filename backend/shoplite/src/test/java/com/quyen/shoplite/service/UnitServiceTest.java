package com.quyen.shoplite.service;

import com.quyen.shoplite.domain.Unit;
import com.quyen.shoplite.domain.request.ReqUnitUpsertDTO;
import com.quyen.shoplite.domain.response.ResUnitDTO;
import com.quyen.shoplite.repository.UnitRepository;
import com.quyen.shoplite.util.error.BadRequestException;
import com.quyen.shoplite.util.error.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UnitServiceTest {

    @Mock
    private UnitRepository unitRepository;

    @InjectMocks
    private UnitService unitService;

    // --- Success cases ---

    @Test
    void create_ShouldReturnUnit_WhenValidRequest() {
        // Arrange
        ReqUnitUpsertDTO req = new ReqUnitUpsertDTO();
        req.setName(" Kilogram ");
        req.setDescription(" kg ");

        when(unitRepository.existsByName("Kilogram")).thenReturn(false);
        Unit savedUnit = Unit.builder().id(1).name("Kilogram").description("kg").build();
        when(unitRepository.save(any(Unit.class))).thenReturn(savedUnit);

        // Act
        ResUnitDTO result = unitService.create(req);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Kilogram", result.getName());
        assertEquals("kg", result.getDescription());
        verify(unitRepository).save(any(Unit.class));
    }

    @Test
    void update_ShouldReturnUnit_WhenValidRequest() {
        // Arrange
        Integer id = 1;
        ReqUnitUpsertDTO req = new ReqUnitUpsertDTO();
        req.setName(" Updated Kilogram ");
        req.setDescription(" updated kg ");

        Unit existingUnit = Unit.builder().id(id).name("Kilogram").description("kg").build();
        when(unitRepository.findById(id)).thenReturn(Optional.of(existingUnit));
        when(unitRepository.existsByNameAndIdNot("Updated Kilogram", id)).thenReturn(false);

        Unit savedUnit = Unit.builder().id(id).name("Updated Kilogram").description("updated kg").build();
        when(unitRepository.save(any(Unit.class))).thenReturn(savedUnit);

        // Act
        ResUnitDTO result = unitService.update(id, req);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Updated Kilogram", result.getName());
        assertEquals("updated kg", result.getDescription());
        verify(unitRepository).save(any(Unit.class));
    }

    @Test
    void delete_ShouldCallDelete_WhenUnitExists() {
        // Arrange
        Integer id = 1;
        Unit existingUnit = Unit.builder().id(id).name("Kilogram").build();
        when(unitRepository.findById(id)).thenReturn(Optional.of(existingUnit));

        // Act
        unitService.delete(id);

        // Assert
        verify(unitRepository).delete(existingUnit);
    }

    // --- Failure cases ---

    @Test
    void create_ShouldThrowBadRequest_WhenDuplicateName() {
        // Arrange
        ReqUnitUpsertDTO req = new ReqUnitUpsertDTO();
        req.setName(" Kilogram ");

        when(unitRepository.existsByName("Kilogram")).thenReturn(true);

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> unitService.create(req));
        assertEquals("Unit name already exists: Kilogram", exception.getMessage());
        verify(unitRepository, never()).save(any(Unit.class));
    }

    @Test
    void update_ShouldThrowNotFound_WhenUnitNotFound() {
        // Arrange
        Integer id = 99;
        ReqUnitUpsertDTO req = new ReqUnitUpsertDTO();
        req.setName("Kilogram");

        when(unitRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> unitService.update(id, req));
        assertEquals("Unit not found with id=" + id, exception.getMessage());
        verify(unitRepository, never()).save(any(Unit.class));
    }

    @Test
    void update_ShouldThrowBadRequest_WhenDuplicateName() {
        // Arrange
        Integer id = 1;
        ReqUnitUpsertDTO req = new ReqUnitUpsertDTO();
        req.setName(" Kilogram ");

        Unit existingUnit = Unit.builder().id(id).name("Old Name").build();
        when(unitRepository.findById(id)).thenReturn(Optional.of(existingUnit));
        when(unitRepository.existsByNameAndIdNot("Kilogram", id)).thenReturn(true);

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> unitService.update(id, req));
        assertEquals("Unit name already exists: Kilogram", exception.getMessage());
        verify(unitRepository, never()).save(any(Unit.class));
    }

    @Test
    void delete_ShouldThrowNotFound_WhenUnitNotFound() {
        // Arrange
        Integer id = 99;
        when(unitRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> unitService.delete(id));
        assertEquals("Unit not found with id=" + id, exception.getMessage());
        verify(unitRepository, never()).delete(any(Unit.class));
    }
}
