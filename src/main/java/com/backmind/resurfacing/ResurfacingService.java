package com.backmind.resurfacing;

import com.backmind.note.dto.NoteResponse;
import com.backmind.note.entity.Note;
import com.backmind.note.entity.NoteStatus;
import com.backmind.note.repository.NoteRepository;
import com.backmind.resurfacing.entity.ResurfacingEvent;
import com.backmind.resurfacing.entity.ResurfacingReason;
import com.backmind.resurfacing.repository.ResurfacingEventRepository;
import com.backmind.user.entity.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;

@Service
public class ResurfacingService {

    private final NoteRepository noteRepository;
    private final ResurfacingEventRepository resurfacingEventRepository;

    public ResurfacingService(
            NoteRepository noteRepository,
            ResurfacingEventRepository resurfacingEventRepository
    ) {
        this.noteRepository = noteRepository;
        this.resurfacingEventRepository = resurfacingEventRepository;
    }

    @Transactional(readOnly = true)
    public List<NoteResponse> today(User user) {
        Instant now = Instant.now();
        Instant lostCutoff = now.minus(30, ChronoUnit.DAYS);
        var dueNotes = noteRepository
                .findAllByUserIdAndStatusAndNextReviewAtLessThanEqualOrderByNextReviewAtAsc(
                        user.getId(),
                        NoteStatus.ACTIVE,
                        now,
                        PageRequest.of(0, 5)
                );

        var selections = new LinkedHashMap<Note, ResurfacingReason>();
        dueNotes.stream().limit(3)
                .forEach(note -> selections.put(note, ResurfacingReason.SPACED_REVIEW));
        noteRepository
                .findFirstByUserIdAndStatusAndLastSeenAtLessThanEqualOrderByLastSeenAtAsc(
                        user.getId(), NoteStatus.ACTIVE, lostCutoff
                )
                .filter(note -> !selections.containsKey(note))
                .ifPresent(note -> selections.put(note, ResurfacingReason.LOST_KNOWLEDGE));
        noteRepository
                .findFirstByUserIdAndStatusAndCreatedAtLessThanEqualAndLastSeenAtGreaterThanAndNextReviewAtGreaterThanOrderByCreatedAtAsc(
                        user.getId(),
                        NoteStatus.ACTIVE,
                        now.minus(1, ChronoUnit.DAYS),
                        lostCutoff,
                        now
                )
                .filter(note -> !selections.containsKey(note))
                .ifPresent(note -> selections.put(note, ResurfacingReason.RANDOM));

        dueNotes.stream()
                .skip(3)
                .filter(note -> !selections.containsKey(note))
                .limit(5 - selections.size())
                .forEach(note -> selections.put(note, ResurfacingReason.SPACED_REVIEW));

        resurfacingEventRepository.saveAllAndFlush(
                selections.entrySet().stream()
                        .map(selection -> new ResurfacingEvent(
                                selection.getKey(), user, selection.getValue()
                        ))
                        .toList()
        );

        return selections.keySet().stream()
                .map(NoteResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<NoteResponse> lost(User user) {
        return noteRepository
                .findAllByUserIdAndStatusAndLastSeenAtLessThanEqualOrderByLastSeenAtAsc(
                        user.getId(),
                        NoteStatus.ACTIVE,
                        Instant.now().minus(30, ChronoUnit.DAYS)
                )
                .stream()
                .map(NoteResponse::from)
                .toList();
    }
}
