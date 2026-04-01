package com.quyen.shoplite.controller;

import com.quyen.shoplite.domain.request.ReqTransactionDTO;
import com.quyen.shoplite.domain.response.ResTransactionDTO;
import com.quyen.shoplite.service.TransactionService;
import com.quyen.shoplite.util.annotation.ApiMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    @ApiMessage("Tạo giao dịch thành công")
    public ResponseEntity<ResTransactionDTO> create(@RequestBody ReqTransactionDTO req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.create(req));
    }

    @GetMapping("/{id}")
    @ApiMessage("Lấy thông tin giao dịch")
    public ResponseEntity<ResTransactionDTO> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(transactionService.findById(id));
    }

    @GetMapping
    @ApiMessage("Danh sách giao dịch")
    public ResponseEntity<List<ResTransactionDTO>> findAll() {
        return ResponseEntity.ok(transactionService.findAll());
    }

    @GetMapping("/order/{orderId}")
    @ApiMessage("Giao dịch theo đơn hàng")
    public ResponseEntity<List<ResTransactionDTO>> findByOrderId(@PathVariable Integer orderId) {
        return ResponseEntity.ok(transactionService.findByOrderId(orderId));
    }
}
