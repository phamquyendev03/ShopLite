package com.quyen.shoplite.controller;

import com.quyen.shoplite.service.SePayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final SePayService sePayService;

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createPaymentSession(@RequestBody Map<String, Integer> request) {
        Integer orderId = request.get("orderId");
        if (orderId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Missing orderId"));
        }
        
        try {
            Map<String, Object> session = sePayService.createPaymentSession(orderId);
            return ResponseEntity.ok(session);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}
