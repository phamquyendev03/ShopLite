package com.quyen.shoplite.controller;

import com.quyen.shoplite.domain.request.ReqOrderDTO;
import com.quyen.shoplite.domain.response.ResOrderDTO;
import com.quyen.shoplite.service.OrderService;
import com.quyen.shoplite.util.annotation.ApiMessage;
import com.quyen.shoplite.util.constant.StatusEnum;
import jakarta.validation.Valid;
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
    private final com.quyen.shoplite.service.PaymentService paymentService;

    @PostMapping
    @ApiMessage("Create order success")
    public ResponseEntity<ResOrderDTO> create(@Valid @RequestBody ReqOrderDTO req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.create(req));
    }

    @GetMapping("/{id}")
    @ApiMessage("Get order success")
    public ResponseEntity<ResOrderDTO> findById(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(orderService.findById(id));
    }

    @GetMapping
    @ApiMessage("Get orders success")
    public ResponseEntity<List<ResOrderDTO>> findAll() {
        return ResponseEntity.ok(orderService.findAll());
    }

    @PatchMapping("/{id}/status")
    @ApiMessage("Update order status success")
    public ResponseEntity<ResOrderDTO> updateStatus(
            @PathVariable("id") Integer id,
            @RequestParam("status") StatusEnum status) {
        return ResponseEntity.ok(orderService.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    @ApiMessage("Cancel order success")
    public ResponseEntity<Void> cancel(@PathVariable("id") Integer id) {
        orderService.cancel(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/payments")
    @ApiMessage("Create payment for order success")
    public ResponseEntity<com.quyen.shoplite.domain.response.ResPaymentDTO> createPayment(
            @PathVariable("id") Integer id,
            @Valid @RequestBody com.quyen.shoplite.domain.request.ReqPaymentDTO req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.createPayment(id, req));
    }

    @GetMapping("/{id}/payments")
    @ApiMessage("Get payment for order success")
    public ResponseEntity<com.quyen.shoplite.domain.response.ResPaymentDTO> getPaymentByOrderId(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(paymentService.findByOrderId(id));
    }
}