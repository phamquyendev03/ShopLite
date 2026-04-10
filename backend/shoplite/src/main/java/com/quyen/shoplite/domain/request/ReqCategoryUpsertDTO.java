package com.quyen.shoplite.domain.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqCategoryUpsertDTO {

    @NotBlank(message = "name must not be blank")
    @Size(max = 150, message = "name must be less than or equal to 150 characters")
    private String name;
}
