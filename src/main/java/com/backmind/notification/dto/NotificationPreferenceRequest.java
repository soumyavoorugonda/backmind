package com.backmind.notification.dto;

import com.backmind.notification.entity.NotificationFrequency;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record NotificationPreferenceRequest(
        @NotNull Boolean enabled,
        @NotNull
        @Pattern(regexp = "(?:[01]\\d|2[0-3]):[0-5]\\d")
        String preferredTime,
        @NotNull NotificationFrequency frequency
) {
}
