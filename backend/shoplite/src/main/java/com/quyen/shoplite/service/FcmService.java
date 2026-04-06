package com.quyen.shoplite.service;

import com.google.firebase.messaging.*;
import com.quyen.shoplite.domain.DeviceToken;
import com.quyen.shoplite.domain.Order;
import com.quyen.shoplite.domain.User;
import com.quyen.shoplite.repository.DeviceTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FcmService {

    private final DeviceTokenRepository deviceTokenRepository;

    /**
     * Gửi push notification "Thanh toán thành công" tới tất cả device của user sở hữu order.
     */
    public void sendPaymentSuccessNotification(Order order) {
        User user = order.getUser();
        List<DeviceToken> deviceTokens = deviceTokenRepository.findAllByUser(user);

        if (deviceTokens.isEmpty()) {
            log.warn("[FCM] No device tokens found for user id={}. Notification skipped.", user.getId());
            return;
        }

        String title = "✅ Thanh toán thành công!";
        String body = String.format("Đơn hàng %s đã được thanh toán. Số tiền: %,.0f VNĐ.",
                order.getCode(), order.getTotalAmount());

        for (DeviceToken deviceToken : deviceTokens) {
            sendToToken(deviceToken.getToken(), title, body, order);
        }
    }

    /**
     * Gửi notification tới một device token cụ thể.
     */
    public void sendToToken(String token, String title, String body, Order order) {
        try {
            Message message = Message.builder()
                    .setToken(token)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .putData("order_code", order != null ? order.getCode() : "")
                    .putData("order_id", order != null ? String.valueOf(order.getId()) : "")
                    .putData("amount", order != null ? String.valueOf(order.getTotalAmount()) : "")
                    .putData("type", "PAYMENT_SUCCESS")
                    .setAndroidConfig(AndroidConfig.builder()
                            .setPriority(AndroidConfig.Priority.HIGH)
                            .setNotification(AndroidNotification.builder()
                                    .setChannelId("payment_channel")
                                    .setIcon("ic_notification")
                                    .setColor("#4CAF50")
                                    .setSound("default")
                                    .build())
                            .build())
                    .setApnsConfig(ApnsConfig.builder()
                            .setAps(Aps.builder()
                                    .setSound("default")
                                    .setBadge(1)
                                    .build())
                            .build())
                    .build();

            String messageId = FirebaseMessaging.getInstance().send(message);
            log.info("[FCM] Notification sent successfully. MessageId={}, Token={}...", messageId, safeSubstring(token));

        } catch (FirebaseMessagingException e) {
            log.error("[FCM] Failed to send notification to token {}...: {}", safeSubstring(token), e.getMessage());

            // Nếu token không hợp lệ (unregistered), có thể xóa khỏi DB
            if (MessagingErrorCode.UNREGISTERED.equals(e.getMessagingErrorCode())
                    || MessagingErrorCode.INVALID_ARGUMENT.equals(e.getMessagingErrorCode())) {
                log.warn("[FCM] Token is invalid/unregistered. Removing from DB: {}...", safeSubstring(token));
                deviceTokenRepository.deleteByToken(token);
            }
        }
    }

    /**
     * Gửi notification thử nghiệm (không cần order).
     */
    public void sendTestNotification(String token, String title, String body) {
        try {
            Message message = Message.builder()
                    .setToken(token)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .putData("type", "TEST")
                    .setAndroidConfig(AndroidConfig.builder()
                            .setPriority(AndroidConfig.Priority.HIGH)
                            .build())
                    .build();

            String messageId = FirebaseMessaging.getInstance().send(message);
            log.info("[FCM] Test notification sent. MessageId={}", messageId);

        } catch (FirebaseMessagingException e) {
            log.error("[FCM] Failed to send test notification: {}", e.getMessage());
            throw new RuntimeException("FCM send failed: " + e.getMessage(), e);
        }
    }

    private String safeSubstring(String s) {
        if (s == null) return "null";
        return s.length() > 20 ? s.substring(0, 20) : s;
    }
}
