package com.quyen.shoplite.controller;

import com.quyen.shoplite.domain.request.ReqInventoryLogDTO;
import com.quyen.shoplite.domain.response.ResInventoryLogDTO;
import com.quyen.shoplite.service.InventoryLogService;
import com.quyen.shoplite.util.annotation.ApiMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventory-logs")
@RequiredArgsConstructor
public class InventoryLogController {

    private final InventoryLogService inventoryLogService;

    @PostMapping
    @ApiMessage("Ghi log kho thành công")
    public ResponseEntity<ResInventoryLogDTO> create(@RequestBody ReqInventoryLogDTO req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventoryLogService.create(req));
    }

    @GetMapping
    @ApiMessage("Danh sách log kho")
    public ResponseEntity<List<ResInventoryLogDTO>> findAll() {
        return ResponseEntity.ok(inventoryLogService.findAll());
    }

    @GetMapping("/product/{productId}")
    @ApiMessage("Log kho theo sản phẩm")
    public ResponseEntity<List<ResInventoryLogDTO>> findByProductId(@PathVariable Integer productId) {
        return ResponseEntity.ok(inventoryLogService.findByProductId(productId));
    }
}
