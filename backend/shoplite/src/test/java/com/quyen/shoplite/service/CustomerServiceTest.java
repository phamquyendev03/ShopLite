package com.quyen.shoplite.service;

import com.quyen.shoplite.domain.Customer;
import com.quyen.shoplite.domain.request.ReqCustomerUpsertDTO;
import com.quyen.shoplite.domain.response.ResCustomerDTO;
import com.quyen.shoplite.repository.CustomerRepository;
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
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    // --- Success cases ---

    @Test
    void create_ShouldReturnCustomer_WhenValidRequest() {
        // Arrange
        ReqCustomerUpsertDTO req = new ReqCustomerUpsertDTO();
        req.setName(" John Doe ");
        req.setPhone(" 0987654321 ");

        when(customerRepository.existsByPhone("0987654321")).thenReturn(false);
        Customer savedCustomer = Customer.builder().id(1).name("John Doe").phone("0987654321").points(0).build();
        when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);

        // Act
        ResCustomerDTO result = customerService.create(req);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("John Doe", result.getName());
        assertEquals("0987654321", result.getPhone());
        assertEquals(0, result.getPoints());
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void update_ShouldReturnCustomer_WhenValidRequest() {
        // Arrange
        Integer id = 1;
        ReqCustomerUpsertDTO req = new ReqCustomerUpsertDTO();
        req.setName(" Jane Doe ");
        req.setPhone(" 0123456789 ");

        Customer existingCustomer = Customer.builder().id(id).name("John Doe").phone("0987654321").points(10).build();
        when(customerRepository.findById(id)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.existsByPhoneAndIdNot("0123456789", id)).thenReturn(false);

        Customer savedCustomer = Customer.builder().id(id).name("Jane Doe").phone("0123456789").points(10).build();
        when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);

        // Act
        ResCustomerDTO result = customerService.update(id, req);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Jane Doe", result.getName());
        assertEquals("0123456789", result.getPhone());
        assertEquals(10, result.getPoints());
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void delete_ShouldCallDelete_WhenCustomerExists() {
        // Arrange
        Integer id = 1;
        Customer existingCustomer = Customer.builder().id(id).name("John Doe").build();
        when(customerRepository.findById(id)).thenReturn(Optional.of(existingCustomer));

        // Act
        customerService.delete(id);

        // Assert
        verify(customerRepository).delete(existingCustomer);
    }

    // --- Failure cases ---

    @Test
    void create_ShouldThrowBadRequest_WhenDuplicatePhone() {
        // Arrange
        ReqCustomerUpsertDTO req = new ReqCustomerUpsertDTO();
        req.setPhone(" 0987654321 ");
        when(customerRepository.existsByPhone("0987654321")).thenReturn(true);

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> customerService.create(req));
        assertEquals("Phone already exists: 0987654321", exception.getMessage());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void update_ShouldThrowNotFound_WhenCustomerNotFound() {
        // Arrange
        Integer id = 99;
        ReqCustomerUpsertDTO req = new ReqCustomerUpsertDTO();
        when(customerRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> customerService.update(id, req));
        assertEquals("Customer not found with id=" + id, exception.getMessage());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void update_ShouldThrowBadRequest_WhenDuplicatePhone() {
        // Arrange
        Integer id = 1;
        ReqCustomerUpsertDTO req = new ReqCustomerUpsertDTO();
        req.setPhone(" 0123456789 ");

        Customer existingCustomer = Customer.builder().id(id).name("Old Name").build();
        when(customerRepository.findById(id)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.existsByPhoneAndIdNot("0123456789", id)).thenReturn(true);

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> customerService.update(id, req));
        assertEquals("Phone already exists: 0123456789", exception.getMessage());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void delete_ShouldThrowNotFound_WhenCustomerNotFound() {
        // Arrange
        Integer id = 99;
        when(customerRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> customerService.delete(id));
        assertEquals("Customer not found with id=" + id, exception.getMessage());
        verify(customerRepository, never()).delete(any(Customer.class));
    }
}
