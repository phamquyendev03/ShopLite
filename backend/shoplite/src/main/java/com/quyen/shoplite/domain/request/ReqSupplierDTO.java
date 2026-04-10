package com.quyen.shoplite.domain.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqSupplierDTO {

    @NotBlank(message = "Tên nhà cung cấp không được để trống")
    private String name;

    private String phone;

    private String address;

    @Email(message = "Email không hợp lệ")
    private String email;
}
