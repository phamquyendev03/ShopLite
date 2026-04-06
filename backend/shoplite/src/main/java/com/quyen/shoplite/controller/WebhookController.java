package com.quyen.shoplite.controller;

import com.quyen.shoplite.service.SePayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/webhook")
@RequiredArgsConstructor
@Slf4j
public class WebhookController {

    private final SePayService sePayService;

    @PostMapping("/sepay")
    public ResponseEntity<String> handleSePayWebhook(@RequestBody Map<String, Object> payload) {
        log.info("Webhook endpoint hit with payload: {}", payload);
        try {
            sePayService.processWebhook(payload);
            return ResponseEntity.ok("success");
        } catch (IllegalArgumentException e) {
            log.error("Invalid webhook payload or amount mismatch: {}", e.getMessage());
            // Acknowledge gracefully so SePay doesn't indefinitely retry an invalid webhook
            return ResponseEntity.ok("acknowledged with errors: " + e.getMessage());
        } catch (Exception e) {
            log.error("Internal server error processing webhook", e);
            // Return 500 to allow caller to retry if it was a transient error
            return ResponseEntity.internalServerError().body("error");
        }
    }
}
