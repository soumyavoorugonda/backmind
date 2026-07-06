package com.backmind.resurfacing;

import com.backmind.note.entity.Note;
import com.backmind.resurfacing.entity.ResurfacingReason;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

final class FeedSelectionPolicy {

    private static final int FEED_LIMIT = 5;
    private static final int DUE_ALLOCATION = 3;

    List<Selection> select(
            List<Note> dueNotes,
            Optional<Note> lostNote,
            Optional<Note> olderNote
    ) {
        var selections = new LinkedHashMap<Note, ResurfacingReason>();
        dueNotes.stream().limit(DUE_ALLOCATION)
                .forEach(note -> selections.put(note, ResurfacingReason.SPACED_REVIEW));
        lostNote.filter(note -> !selections.containsKey(note))
                .ifPresent(note -> selections.put(note, ResurfacingReason.LOST_KNOWLEDGE));
        olderNote.filter(note -> !selections.containsKey(note))
                .ifPresent(note -> selections.put(note, ResurfacingReason.RANDOM));
        dueNotes.stream()
                .skip(DUE_ALLOCATION)
                .filter(note -> !selections.containsKey(note))
                .limit(FEED_LIMIT - selections.size())
                .forEach(note -> selections.put(note, ResurfacingReason.SPACED_REVIEW));

        return selections.entrySet().stream()
                .map(entry -> new Selection(entry.getKey(), entry.getValue()))
                .toList();
    }

    record Selection(Note note, ResurfacingReason reason) {
    }
}
