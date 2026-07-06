package com.backmind.notification;

import com.backmind.notification.dto.NotificationPreferenceRequest;
import com.backmind.notification.dto.NotificationPreferenceResponse;
import com.backmind.auth.AuthenticatedUser;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notification-preferences")
public class NotificationPreferenceController {

    private final NotificationService notificationService;

    public NotificationPreferenceController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PutMapping
    public NotificationPreferenceResponse update(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody NotificationPreferenceRequest request
    ) {
        return notificationService.update(user.id(), request);
    }

    @GetMapping
    public NotificationPreferenceResponse find(@AuthenticationPrincipal AuthenticatedUser user) {
        return notificationService.find(user.id());
    }
}
