package com.quyen.shoplite.domain.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqUnitDTO {

    @NotBlank(message = "Tên đơn vị không được để trống")
    private String name;

    private String description;
}
