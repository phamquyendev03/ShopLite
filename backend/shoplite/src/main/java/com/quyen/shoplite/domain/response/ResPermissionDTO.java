package com.quyen.shoplite.domain.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ResPermissionDTO {
    private Long id;
    private String name;
    private String apiPath;
    private String method;
    private String module;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
