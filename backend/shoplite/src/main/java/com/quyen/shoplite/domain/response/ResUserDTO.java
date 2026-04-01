package com.quyen.shoplite.domain.response;

import com.quyen.shoplite.util.constant.RoleEnum;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ResUserDTO {
    private Integer id;
    private String username;
    private RoleEnum role;
    private boolean isActive;
    private LocalDate createdAt;
}
