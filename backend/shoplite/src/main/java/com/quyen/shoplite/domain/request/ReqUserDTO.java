package com.quyen.shoplite.domain.request;

import com.quyen.shoplite.util.constant.RoleEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqUserDTO {
    private String username;
    private String password;
    private RoleEnum role;
    private boolean isActive;
}
