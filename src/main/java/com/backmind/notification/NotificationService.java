package com.backmind.notification;

import com.backmind.notification.dto.NotificationPreferenceRequest;
import com.backmind.notification.dto.NotificationPreferenceResponse;
import com.backmind.notification.entity.NotificationFrequency;
import com.backmind.notification.entity.NotificationPreference;
import com.backmind.notification.repository.NotificationPreferenceRepository;
import com.backmind.user.entity.User;
import com.backmind.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.time.Instant;

@Service
public class NotificationService {

    private static final String DEFAULT_PREFERRED_TIME = "09:00";

    private final NotificationPreferenceRepository preferenceRepository;
    private final UserRepository userRepository;

    public NotificationService(
            NotificationPreferenceRepository preferenceRepository,
            UserRepository userRepository
    ) {
        this.preferenceRepository = preferenceRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public NotificationPreferenceResponse update(
            UUID userId,
            NotificationPreferenceRequest request
    ) {
        var now = Instant.now();
        var preference = preferenceRepository.findByUserId(userId)
                .map(existingPreference -> {
                    existingPreference.update(
                            request.enabled(),
                            request.preferredTime(),
                            request.frequency(),
                            now
                    );
                    return existingPreference;
                })
                .orElseGet(() -> new NotificationPreference(
                        findUser(userId),
                        request.enabled(),
                        request.preferredTime(),
                        request.frequency(),
                        now
                ));

        return NotificationPreferenceResponse.from(
                preferenceRepository.saveAndFlush(preference)
        );
    }

    @Transactional
    public NotificationPreferenceResponse find(UUID userId) {
        var now = Instant.now();
        var preference = preferenceRepository.findByUserId(userId)
                .orElseGet(() -> preferenceRepository.saveAndFlush(
                        new NotificationPreference(
                                findUser(userId),
                                true,
                                DEFAULT_PREFERRED_TIME,
                                NotificationFrequency.DAILY,
                                now
                        )
                ));
        return NotificationPreferenceResponse.from(preference);
    }

    private User findUser(UUID userId) {
        return userRepository.findById(userId).orElseThrow();
    }
}
