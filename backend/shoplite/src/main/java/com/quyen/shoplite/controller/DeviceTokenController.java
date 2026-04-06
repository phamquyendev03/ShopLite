package com.quyen.shoplite.controller;

import com.quyen.shoplite.service.DeviceTokenService;
import com.quyen.shoplite.service.FcmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/device-tokens")
@RequiredArgsConstructor
@Slf4j
public class DeviceTokenController {

    private final DeviceTokenService deviceTokenService;
    private final FcmService fcmService;

    /**
     * POST /api/v1/device-tokens/register
     * Body: { "userId": 1, "token": "fcm_token_here", "deviceType": "ANDROID" }
     * 
     * Gọi sau khi user login để lưu FCM token từ thiết bị.
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerToken(@RequestBody Map<String, Object> request) {
        Integer userId = Integer.valueOf(request.get("userId").toString());
        String token = request.get("token").toString();
        String deviceType = request.getOrDefault("deviceType", "ANDROID").toString();

        deviceTokenService.registerToken(userId, token, deviceType);
        log.info("[DeviceToken] Registered token for userId={}", userId);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "FCM token đã được đăng ký thành công"
        ));
    }

    /**
     * DELETE /api/v1/device-tokens
     * Body: { "token": "fcm_token_here" }
     * 
     * Gọi khi logout để xóa token.
     */
    @DeleteMapping
    public ResponseEntity<Map<String, Object>> deleteToken(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        deviceTokenService.deleteToken(token);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "FCM token đã được xóa"
        ));
    }

    /**
     * POST /api/v1/device-tokens/test-notification
     * Body: { "token": "fcm_token", "title": "Test", "body": "Hello!" }
     * 
     * Test gửi notification tới một device cụ thể (dành cho debug).
     */
    @PostMapping("/test-notification")
    public ResponseEntity<Map<String, Object>> testNotification(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String title = request.getOrDefault("title", "🔔 Test Notification");
        String body = request.getOrDefault("body", "Đây là thông báo thử nghiệm từ ShopLite!");

        try {
            fcmService.sendTestNotification(token, title, body);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Notification đã được gửi thành công!"
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "Gửi notification thất bại: " + e.getMessage()
            ));
        }
    }
}
