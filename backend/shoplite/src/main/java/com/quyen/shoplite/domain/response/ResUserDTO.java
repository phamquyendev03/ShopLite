package com.quyen.shoplite.domain.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ResUserDTO {
    private Integer id;
    private String username;
    private String roleName;    // Tên role (thay cho RoleEnum)
    private Long roleId;
    private boolean isActive;
    private LocalDate createdAt;
}
