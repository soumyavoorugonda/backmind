package com.backmind.resurfacing.entity;

import com.backmind.note.entity.Note;
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

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "resurfacing_events")
public class ResurfacingEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "note_id", nullable = false)
    private Note note;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "shown_at", nullable = false)
    private Instant shownAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ResurfacingReason reason;

    protected ResurfacingEvent() {
    }

    public ResurfacingEvent(Note note, User user, ResurfacingReason reason) {
        this.note = note;
        this.user = user;
        this.reason = reason;
        this.shownAt = Instant.now();
    }
}
