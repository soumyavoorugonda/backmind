package com.backmind.note.dto;

import com.backmind.note.entity.BeliefStatus;
import com.backmind.note.entity.Note;
import com.backmind.note.entity.NoteStatus;

import java.time.Instant;
import java.util.UUID;

public record NoteResponse(
        UUID id,
        String content,
        String category,
        Instant createdAt,
        Instant updatedAt,
        Instant lastSeenAt,
        Instant nextReviewAt,
        int currentIntervalDays,
        NoteStatus status,
        int usefulnessScore,
        BeliefStatus beliefStatus
) {

    public static NoteResponse from(Note note) {
        return new NoteResponse(
                note.getId(),
                note.getContent(),
                note.getCategory(),
                note.getCreatedAt(),
                note.getUpdatedAt(),
                note.getLastSeenAt(),
                note.getNextReviewAt(),
                note.getCurrentIntervalDays(),
                note.getStatus(),
                note.getUsefulnessScore(),
                note.getBeliefStatus()
        );
    }
}
