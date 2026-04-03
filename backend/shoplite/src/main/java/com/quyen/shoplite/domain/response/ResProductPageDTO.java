package com.quyen.shoplite.domain.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Wrapper response cho danh sách sản phẩm có phân trang.
 */
@Getter
@Setter
public class ResProductPageDTO {

    private long totalElements;
    private int totalPages;
    private int page;
    private int size;
    private List<ResProductDTO> data;
}
