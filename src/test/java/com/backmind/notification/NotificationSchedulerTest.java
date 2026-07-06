package com.backmind.notification;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class NotificationSchedulerTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void registersDailyReminderSchedulerStub() throws Exception {
        Object scheduler = applicationContext.getBean("notificationScheduler");
        var scheduledMethod = scheduler.getClass().getMethod("sendDailyReminders");

        assertNotNull(scheduledMethod.getAnnotation(Scheduled.class));
    }
}
