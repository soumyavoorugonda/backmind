package com.backmind.review;

import com.backmind.note.NoteNotFoundException;
import com.backmind.note.dto.NoteResponse;
import com.backmind.note.repository.NoteRepository;
import com.backmind.review.dto.ReviewRequest;
import com.backmind.review.entity.FeedbackType;
import com.backmind.review.entity.NoteReview;
import com.backmind.review.repository.NoteReviewRepository;
import com.backmind.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ReviewService {

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
    public NoteResponse review(User user, UUID noteId, ReviewRequest request) {
        var note = noteRepository.findByIdAndUserId(noteId, user.getId())
                .orElseThrow(NoteNotFoundException::new);

        switch (request.feedbackType()) {
            case USEFUL -> note.recordSuccessfulReview();
            case FORGOT_THIS -> note.recordForgottenReview();
            case STILL_BELIEVE -> note.recordStillBelieveReview();
            case NOT_USEFUL -> note.recordNotUsefulReview();
            case NO_LONGER_BELIEVE -> note.recordNoLongerBelieveReview();
            case SKIPPED -> note.recordSkippedReview();
        }
        noteReviewRepository.saveAndFlush(
                new NoteReview(note, user, request.feedbackType(), request.userResponse())
        );

        return NoteResponse.from(note);
    }
}
