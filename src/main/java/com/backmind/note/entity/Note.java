package com.backmind.note.entity;

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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.SQLRestriction;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Entity
@Table(name = "notes")
@SQLRestriction("status <> 'DELETED'")
public class Note {

    private static final int[] REVIEW_INTERVALS = {1, 3, 10, 30, 60};

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 300)
    private String content;

    @Column(length = 50)
    private String category;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "last_seen_at", nullable = false)
    private Instant lastSeenAt;

    @Column(name = "next_review_at", nullable = false)
    private Instant nextReviewAt;

    @Column(name = "current_interval_days", nullable = false)
    private int currentIntervalDays;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NoteStatus status;

    @Column(name = "usefulness_score", nullable = false)
    private int usefulnessScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "belief_status", nullable = false, length = 30)
    private BeliefStatus beliefStatus;

    protected Note() {
    }

    public Note(User user, String content, String category) {
        var now = Instant.now();
        this.user = user;
        this.content = content;
        this.category = category;
        this.createdAt = now;
        this.updatedAt = now;
        this.lastSeenAt = now;
        this.nextReviewAt = now.plus(1, ChronoUnit.DAYS);
        this.currentIntervalDays = 1;
        this.status = NoteStatus.ACTIVE;
        this.usefulnessScore = 0;
        this.beliefStatus = BeliefStatus.UNKNOWN;
    }

    public UUID getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getContent() {
        return content;
    }

    public String getCategory() {
        return category;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Instant getLastSeenAt() {
        return lastSeenAt;
    }

    public Instant getNextReviewAt() {
        return nextReviewAt;
    }

    public int getCurrentIntervalDays() {
        return currentIntervalDays;
    }

    public NoteStatus getStatus() {
        return status;
    }

    public int getUsefulnessScore() {
        return usefulnessScore;
    }

    public BeliefStatus getBeliefStatus() {
        return beliefStatus;
    }

    public void update(String content, String category) {
        this.content = content;
        this.category = category;
        this.updatedAt = Instant.now();
    }

    public void softDelete() {
        this.status = NoteStatus.DELETED;
        this.updatedAt = Instant.now();
    }

    public void recordSuccessfulReview() {
        var now = Instant.now();
        this.currentIntervalDays = nextReviewInterval(currentIntervalDays);
        this.lastSeenAt = now;
        this.nextReviewAt = now.plus(currentIntervalDays, ChronoUnit.DAYS);
        this.updatedAt = now;
    }

    public void recordForgottenReview() {
        var now = Instant.now();
        this.currentIntervalDays = 1;
        this.lastSeenAt = now;
        this.nextReviewAt = now.plus(1, ChronoUnit.DAYS);
        this.updatedAt = now;
    }

    public void recordStillBelieveReview() {
        recordSuccessfulReview();
        this.beliefStatus = BeliefStatus.STILL_BELIEVE;
    }

    public void recordNotUsefulReview() {
        var now = Instant.now();
        this.status = NoteStatus.ARCHIVED;
        this.lastSeenAt = now;
        this.updatedAt = now;
    }

    public void recordNoLongerBelieveReview() {
        recordNotUsefulReview();
        this.beliefStatus = BeliefStatus.NO_LONGER_BELIEVE;
    }

    public void recordSkippedReview() {
        var now = Instant.now();
        this.lastSeenAt = now;
        this.updatedAt = now;
    }

    private static int nextReviewInterval(int currentInterval) {
        for (int interval : REVIEW_INTERVALS) {
            if (interval > currentInterval) {
                return interval;
            }
        }
        return REVIEW_INTERVALS[REVIEW_INTERVALS.length - 1];
    }
}
