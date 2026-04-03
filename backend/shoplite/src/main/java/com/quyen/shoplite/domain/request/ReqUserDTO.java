package com.quyen.shoplite.domain.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqUserDTO {
    @NotBlank(message = "username không được để trống")
    private String username;

    private String password;

    /** ID của Role cần gán (null = không đổi khi update) */
    private Long roleId;

    private boolean isActive = true;
}
