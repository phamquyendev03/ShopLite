package com.quyen.shoplite.controller;

import com.quyen.shoplite.domain.request.ReqOrderDTO;
import com.quyen.shoplite.domain.response.ResOrderDTO;
import com.quyen.shoplite.service.OrderService;
import com.quyen.shoplite.util.annotation.ApiMessage;
import com.quyen.shoplite.util.constant.StatusEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ApiMessage("Tạo đơn hàng thành công")
    public ResponseEntity<ResOrderDTO> create(@RequestBody ReqOrderDTO req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.create(req));
    }

    @GetMapping("/{id}")
    @ApiMessage("Lấy thông tin đơn hàng")
    public ResponseEntity<ResOrderDTO> findById(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(orderService.findById(id));
    }

    @GetMapping
    @ApiMessage("Danh sách đơn hàng")
    public ResponseEntity<List<ResOrderDTO>> findAll() {
        return ResponseEntity.ok(orderService.findAll());
    }

    @PatchMapping("/{id}/status")
    @ApiMessage("Cập nhật trạng thái đơn hàng")
    public ResponseEntity<ResOrderDTO> updateStatus(
            @PathVariable("id") Integer id,
            @RequestParam("status") StatusEnum status) {
        return ResponseEntity.ok(orderService.updateStatus(id, status));
    }
}
