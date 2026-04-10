package com.quyen.shoplite.domain.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ResInventoryAdjustmentDTO {
    private Integer id;
    private String reason;
    private String note;
    private String createdBy;
    private LocalDateTime createdAt;

    /** Danh sách inventory log được tạo trong phiên kiểm kê này */
    private List<ResInventoryLogDTO> logs;
}
