package com.backmind.notification.dto;

import com.backmind.notification.entity.NotificationFrequency;
import com.backmind.notification.entity.NotificationPreference;

public record NotificationPreferenceResponse(
        boolean enabled,
        String preferredTime,
        NotificationFrequency frequency
) {
    public static NotificationPreferenceResponse from(NotificationPreference preference) {
        return new NotificationPreferenceResponse(
                preference.isEnabled(),
                preference.getPreferredTime(),
                preference.getFrequency()
        );
    }
}
