package com.backmind.note.repository;

import com.backmind.note.entity.Note;
import com.backmind.note.entity.NoteStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NoteRepository extends JpaRepository<Note, UUID> {

    List<Note> findAllByUserId(UUID userId);

    Optional<Note> findByIdAndUserId(UUID id, UUID userId);

    @Query("""
            select note
            from Note note
            where note.user.id = :userId
              and note.status = :status
              and note.nextReviewAt <= :dueAt
            order by note.nextReviewAt asc
            """)
    List<Note> findDueNotes(
            @Param("userId") UUID userId,
            @Param("status") NoteStatus status,
            @Param("dueAt") Instant dueAt,
            Pageable pageable
    );

    @Query("""
            select note
            from Note note
            where note.user.id = :userId
              and note.status = :status
              and note.lastSeenAt <= :cutoff
            order by note.lastSeenAt asc
            """)
    List<Note> findLostNotes(
            @Param("userId") UUID userId,
            @Param("status") NoteStatus status,
            @Param("cutoff") Instant cutoff
    );

    @Query("""
            select note
            from Note note
            where note.user.id = :userId
              and note.status = :status
              and note.lastSeenAt <= :cutoff
            order by note.lastSeenAt asc
            """)
    List<Note> findFirstLostNote(
            @Param("userId") UUID userId,
            @Param("status") NoteStatus status,
            @Param("cutoff") Instant cutoff,
            Pageable pageable
    );

    @Query("""
            select note
            from Note note
            where note.user.id = :userId
              and note.status = :status
              and note.createdAt <= :createdBefore
              and note.lastSeenAt > :lastSeenAfter
              and note.nextReviewAt > :nextReviewAfter
            order by note.createdAt asc
            """)
    List<Note> findOlderNoteForFeed(
            @Param("userId") UUID userId,
            @Param("status") NoteStatus status,
            @Param("createdBefore") Instant createdBefore,
            @Param("lastSeenAfter") Instant lastSeenAfter,
            @Param("nextReviewAfter") Instant nextReviewAfter,
            Pageable pageable
    );
}
