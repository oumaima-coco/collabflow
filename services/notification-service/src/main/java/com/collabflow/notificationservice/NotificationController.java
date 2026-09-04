package com.collabflow.notificationservice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {
    private final NotificationRepository notificationRepository;
    private final SseEmitterManager sseEmitterManager;

    public NotificationController(NotificationRepository notificationRepository, SseEmitterManager sseEmitterManager) {
        this.notificationRepository = notificationRepository;
        this.sseEmitterManager = sseEmitterManager;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getNotificationsForUser(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationRepository.findByUserIdOrderByCreatedAtDesc(userId));
    }

    @GetMapping("/stream/{userId}")
    public SseEmitter streamNotifications(@PathVariable Long userId) {
        return sseEmitterManager.subscribe(userId);
    }

    @PatchMapping("/user/{userId}/read-all")
    public ResponseEntity<Void> markAllAsRead(@PathVariable Long userId) {
        List<Notification> unread = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        for (Notification notification : unread) {
            if (!notification.isRead()) {
                notification.setRead(true);
            }
        }
        notificationRepository.saveAll(unread);
        return ResponseEntity.ok().build();
    }
}