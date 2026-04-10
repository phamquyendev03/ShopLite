    package com.quyen.shoplite.unit;

import com.quyen.shoplite.domain.Unit;
import com.quyen.shoplite.domain.request.ReqUnitUpsertDTO;
import com.quyen.shoplite.domain.response.ResUnitDTO;
import com.quyen.shoplite.repository.UnitRepository;
import com.quyen.shoplite.service.UnitService;
import com.quyen.shoplite.util.error.BadRequestException;
import com.quyen.shoplite.util.error.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnitServiceTest {

    @Mock
    private UnitRepository unitRepository;

    @InjectMocks
    private UnitService unitService;

    @Test
    void create_Success() {
        ReqUnitUpsertDTO req = new ReqUnitUpsertDTO();
        req.setName("Box");
        req.setDescription("Unit test");

        Unit saved = Unit.builder().id(1).name("Box").description("Unit test").build();

        when(unitRepository.existsByName("Box")).thenReturn(false);
        when(unitRepository.save(any(Unit.class))).thenReturn(saved);

        ResUnitDTO result = unitService.create(req);

        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getName()).isEqualTo("Box");
    }

    @Test
    void create_DuplicateName_ThrowsBadRequest() {
        ReqUnitUpsertDTO req = new ReqUnitUpsertDTO();
        req.setName("Box");

        when(unitRepository.existsByName("Box")).thenReturn(true);

        assertThatThrownBy(() -> unitService.create(req))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Box");

        verify(unitRepository, never()).save(any(Unit.class));
    }

    @Test
    void findById_NotFound_ThrowsNotFound() {
        when(unitRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> unitService.findById(99))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
