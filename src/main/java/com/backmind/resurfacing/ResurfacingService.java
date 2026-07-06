package com.backmind.resurfacing;

import com.backmind.note.dto.NoteResponse;
import com.backmind.note.entity.NoteStatus;
import com.backmind.note.repository.NoteRepository;
import com.backmind.resurfacing.entity.ResurfacingEvent;
import com.backmind.resurfacing.repository.ResurfacingEventRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
public class ResurfacingService {

    private static final int LOST_KNOWLEDGE_DAYS_THRESHOLD = 30;

    private final FeedSelectionPolicy selectionPolicy = new FeedSelectionPolicy();
    private final NoteRepository noteRepository;
    private final ResurfacingEventRepository resurfacingEventRepository;

    public ResurfacingService(
            NoteRepository noteRepository,
            ResurfacingEventRepository resurfacingEventRepository
    ) {
        this.noteRepository = noteRepository;
        this.resurfacingEventRepository = resurfacingEventRepository;
    }

    @Transactional
    public List<NoteResponse> today(UUID userId) {
        var now = Instant.now();
        var lostCutoff = now.minus(LOST_KNOWLEDGE_DAYS_THRESHOLD, ChronoUnit.DAYS);
        var firstResult = PageRequest.of(0, 1);
        var dueNotes = noteRepository.findDueNotes(
                userId,
                NoteStatus.ACTIVE,
                now,
                PageRequest.of(0, 5)
        );

        var lostNote = noteRepository.findFirstLostNote(
                userId, NoteStatus.ACTIVE, lostCutoff, firstResult
        ).stream().findFirst();
        var olderNote = noteRepository.findOlderNoteForFeed(
                userId,
                NoteStatus.ACTIVE,
                now.minus(1, ChronoUnit.DAYS),
                lostCutoff,
                now,
                firstResult
        ).stream().findFirst();
        var selections = selectionPolicy.select(dueNotes, lostNote, olderNote);

        resurfacingEventRepository.saveAllAndFlush(
                selections.stream()
                        .map(selection -> new ResurfacingEvent(
                                selection.note(),
                                selection.note().getUser(),
                                selection.reason(),
                                now
                        ))
                        .toList()
        );

        return selections.stream()
                .map(FeedSelectionPolicy.Selection::note)
                .map(NoteResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<NoteResponse> lost(UUID userId) {
        var now = Instant.now();
        return noteRepository.findLostNotes(
                userId,
                NoteStatus.ACTIVE,
                now.minus(LOST_KNOWLEDGE_DAYS_THRESHOLD, ChronoUnit.DAYS)
        )
                .stream()
                .map(NoteResponse::from)
                .toList();
    }
}
