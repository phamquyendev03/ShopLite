package com.quyen.shoplite.domain.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqCustomerDTO {

    @NotBlank(message = "Tên khách hàng không được để trống")
    private String name;

    private String phone;
}
