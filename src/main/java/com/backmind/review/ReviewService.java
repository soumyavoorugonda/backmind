package com.backmind.review;

import com.backmind.note.NoteNotFoundException;
import com.backmind.note.dto.NoteResponse;
import com.backmind.note.repository.NoteRepository;
import com.backmind.review.dto.ReviewRequest;
import com.backmind.review.entity.FeedbackType;
import com.backmind.review.entity.NoteReview;
import com.backmind.review.repository.NoteReviewRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.time.Instant;

@Service
public class ReviewService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReviewService.class);

    private final NoteRepository noteRepository;
    private final NoteReviewRepository noteReviewRepository;

    public ReviewService(
            NoteRepository noteRepository,
            NoteReviewRepository noteReviewRepository
    ) {
        this.noteRepository = noteRepository;
        this.noteReviewRepository = noteReviewRepository;
    }

    @Transactional
    public NoteResponse review(UUID userId, UUID noteId, ReviewRequest request) {
        var now = Instant.now();
        var note = noteRepository.findByIdAndUserId(noteId, userId)
                .orElseThrow(NoteNotFoundException::new);

        switch (request.feedbackType()) {
            case USEFUL -> note.recordSuccessfulReview(now);
            case FORGOT_THIS -> note.recordForgottenReview(now);
            case STILL_BELIEVE -> note.recordStillBelieveReview(now);
            case NOT_USEFUL -> note.recordNotUsefulReview(now);
            case NO_LONGER_BELIEVE -> note.recordNoLongerBelieveReview(now);
            case SKIPPED -> note.recordSkippedReview(now);
        }
        noteReviewRepository.saveAndFlush(
                new NoteReview(
                        note, note.getUser(), request.feedbackType(), request.userResponse(), now
                )
        );
        LOGGER.info(
                "Review completed userId={} noteId={} feedbackType={}",
                userId,
                noteId,
                request.feedbackType()
        );

        return NoteResponse.from(note);
    }
}
