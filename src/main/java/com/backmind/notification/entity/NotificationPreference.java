package com.backmind.notification.entity;

import com.backmind.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notification_preferences")
public class NotificationPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private boolean enabled;

    @Column(name = "preferred_time", nullable = false, length = 5)
    private String preferredTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NotificationFrequency frequency;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected NotificationPreference() {
    }

    public NotificationPreference(
            User user,
            boolean enabled,
            String preferredTime,
            NotificationFrequency frequency
    ) {
        this(user, enabled, preferredTime, frequency, Instant.now());
    }

    public NotificationPreference(
            User user,
            boolean enabled,
            String preferredTime,
            NotificationFrequency frequency,
            Instant now
    ) {
        this.user = user;
        this.enabled = enabled;
        this.preferredTime = preferredTime;
        this.frequency = frequency;
        this.createdAt = now;
        this.updatedAt = now;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getPreferredTime() {
        return preferredTime;
    }

    public NotificationFrequency getFrequency() {
        return frequency;
    }

    public void update(
            boolean enabled,
            String preferredTime,
            NotificationFrequency frequency
    ) {
        update(enabled, preferredTime, frequency, Instant.now());
    }

    public void update(
            boolean enabled,
            String preferredTime,
            NotificationFrequency frequency,
            Instant now
    ) {
        this.enabled = enabled;
        this.preferredTime = preferredTime;
        this.frequency = frequency;
        this.updatedAt = now;
    }
}
