package com.backmind.note.repository;

import com.backmind.note.entity.Note;
import com.backmind.note.entity.NoteStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NoteRepository extends JpaRepository<Note, UUID> {

    List<Note> findAllByUserId(UUID userId);

    Optional<Note> findByIdAndUserId(UUID id, UUID userId);

    List<Note> findAllByUserIdAndStatusAndNextReviewAtLessThanEqualOrderByNextReviewAtAsc(
            UUID userId,
            NoteStatus status,
            Instant dueAt,
            Pageable pageable
    );

    List<Note> findAllByUserIdAndStatusAndLastSeenAtLessThanEqualOrderByLastSeenAtAsc(
            UUID userId,
            NoteStatus status,
            Instant lastSeenCutoff
    );

    Optional<Note> findFirstByUserIdAndStatusAndLastSeenAtLessThanEqualOrderByLastSeenAtAsc(
            UUID userId,
            NoteStatus status,
            Instant lastSeenCutoff
    );

    Optional<Note> findFirstByUserIdAndStatusAndCreatedAtLessThanEqualAndLastSeenAtGreaterThanAndNextReviewAtGreaterThanOrderByCreatedAtAsc(
            UUID userId,
            NoteStatus status,
            Instant createdBefore,
            Instant lastSeenAfter,
            Instant nextReviewAfter
    );
}
