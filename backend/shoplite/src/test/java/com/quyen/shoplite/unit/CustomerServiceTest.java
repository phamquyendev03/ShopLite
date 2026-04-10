package com.quyen.shoplite.unit;

import com.quyen.shoplite.domain.Customer;
import com.quyen.shoplite.domain.request.ReqCustomerUpsertDTO;
import com.quyen.shoplite.domain.response.ResCustomerDTO;
import com.quyen.shoplite.repository.CustomerRepository;
import com.quyen.shoplite.service.CustomerService;
import com.quyen.shoplite.util.error.BadRequestException;
import com.quyen.shoplite.util.error.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
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
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    @Test
    @DisplayName("create customer success")
    void create_Success() {
        ReqCustomerUpsertDTO req = new ReqCustomerUpsertDTO();
        req.setName("Khach A");
        req.setPhone("0909000001");

        Customer customer = Customer.builder()
                .id(1)
                .name("Khach A")
                .phone("0909000001")
                .points(0)
                .build();

        when(customerRepository.existsByPhone("0909000001")).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        ResCustomerDTO result = customerService.create(req);

        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getName()).isEqualTo("Khach A");
    }

    @Test
    @DisplayName("create customer duplicate phone")
    void create_DuplicatePhone_ThrowsException() {
        ReqCustomerUpsertDTO req = new ReqCustomerUpsertDTO();
        req.setName("Khach A");
        req.setPhone("0909000001");

        when(customerRepository.existsByPhone("0909000001")).thenReturn(true);

        assertThatThrownBy(() -> customerService.create(req))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("0909000001");

        verify(customerRepository, never()).save(any());
    }

    @Test
    @DisplayName("update customer not found")
    void update_NotFound_ThrowsException() {
        ReqCustomerUpsertDTO req = new ReqCustomerUpsertDTO();
        req.setName("Khach B");

        when(customerRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.update(99, req))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }
}
