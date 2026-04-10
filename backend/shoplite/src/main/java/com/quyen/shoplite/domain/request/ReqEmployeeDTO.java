package com.quyen.shoplite.domain.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqEmployeeDTO {

    @NotNull(message = "userId không được để trống")
    private Integer userId;

    @NotNull(message = "salaryRate không được để trống")
    @Positive(message = "salaryRate phải lớn hơn 0")
    private Double salaryRate;

    /** ID văn phòng (nullable) */
    private Integer officeId;

    /** QR code cho check-in (tuỳ chọn, nếu null sẽ sinh tự động) */
    private String qr;
}
