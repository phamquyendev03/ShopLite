package com.quyen.shoplite.domain.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqUnitUpsertDTO {

    @NotBlank(message = "name must not be blank")
    @Size(max = 100, message = "name must be less than or equal to 100 characters")
    private String name;

    @Size(max = 300, message = "description must be less than or equal to 300 characters")
    private String description;
}
