package com.quyen.shoplite.domain.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqCheckInDTO {

    /** QR code của nhân viên — dùng để định danh */
    @NotNull(message = "qr không được để trống")
    private String qr;

    /** Vĩ độ GPS khi quét QR */
    @NotNull(message = "latitude không được để trống")
    private Double latitude;

    /** Kinh độ GPS khi quét QR */
    @NotNull(message = "longitude không được để trống")
    private Double longitude;
}
