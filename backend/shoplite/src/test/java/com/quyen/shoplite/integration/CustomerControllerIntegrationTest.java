package com.quyen.shoplite.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quyen.shoplite.domain.Customer;
import com.quyen.shoplite.domain.request.ReqCustomerUpsertDTO;
import com.quyen.shoplite.repository.CustomerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.security.test.context.support.WithMockUser;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@WithMockUser
class CustomerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    @DisplayName("create customer success")
    void createCustomer_Success() throws Exception {
        ReqCustomerUpsertDTO req = new ReqCustomerUpsertDTO();
        req.setName("Customer IT");
        req.setPhone("0981234567");

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("Customer IT"))
                .andExpect(jsonPath("$.data.phone").value("0981234567"));

        assertThat(customerRepository.existsByPhone("0981234567")).isTrue();
    }

    @Test
    @DisplayName("create customer validation failure - invalid phone")
    void createCustomer_InvalidPhoneFailure() throws Exception {
        ReqCustomerUpsertDTO req = new ReqCustomerUpsertDTO();
        req.setName("Customer IT");
        req.setPhone("123");

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    @DisplayName("create customer duplicate phone failure")
    void createCustomer_DuplicatePhoneFailure() throws Exception {
        customerRepository.save(Customer.builder().name("Exst").phone("0981234567").points(0).build());
        
        ReqCustomerUpsertDTO req = new ReqCustomerUpsertDTO();
        req.setName("Customer IT 2");
        req.setPhone("0981234567");

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.message").value("Phone already exists: 0981234567"));
    }

    @Test
    @DisplayName("get customer by id success")
    void getCustomer_Success() throws Exception {
        Customer customer = customerRepository.save(Customer.builder().name("TestCust").phone("0981112222").points(0).build());

        mockMvc.perform(get("/api/v1/customers/" + customer.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(customer.getId()))
                .andExpect(jsonPath("$.data.name").value("TestCust"));
    }

    @Test
    @DisplayName("update customer success")
    void updateCustomer_Success() throws Exception {
        Customer customer = customerRepository.save(Customer.builder().name("OldCust").phone("0981112222").points(0).build());

        ReqCustomerUpsertDTO req = new ReqCustomerUpsertDTO();
        req.setName("UpdatedCust");
        req.setPhone("0983334444");

        mockMvc.perform(put("/api/v1/customers/" + customer.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("UpdatedCust"))
                .andExpect(jsonPath("$.data.phone").value("0983334444"));

        Customer updated = customerRepository.findById(customer.getId()).orElseThrow();
        assertThat(updated.getName()).isEqualTo("UpdatedCust");
        assertThat(updated.getPhone()).isEqualTo("0983334444");
    }

    @Test
    @DisplayName("delete customer success")
    void deleteCustomer_Success() throws Exception {
        Customer customer = customerRepository.save(Customer.builder().name("ToDelCust").phone("0981112222").points(0).build());

        mockMvc.perform(delete("/api/v1/customers/" + customer.getId()))
                .andExpect(status().isNoContent());

        assertThat(customerRepository.existsById(customer.getId())).isFalse();
    }
}
