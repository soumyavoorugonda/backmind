package com.backmind.notification;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class NotificationScheduler {

    @Scheduled(cron = "${backmind.notifications.daily-cron:0 0 9 * * *}")
    public void sendDailyReminders() {
        // Delivery integration is intentionally deferred; Phase 6 requires only the scheduler stub.
    }
}
