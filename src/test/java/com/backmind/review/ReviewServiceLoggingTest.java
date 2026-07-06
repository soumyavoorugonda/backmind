package com.backmind.review;

import com.backmind.note.entity.Note;
import com.backmind.note.repository.NoteRepository;
import com.backmind.review.dto.ReviewRequest;
import com.backmind.review.entity.FeedbackType;
import com.backmind.review.repository.NoteReviewRepository;
import com.backmind.user.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(OutputCaptureExtension.class)
class ReviewServiceLoggingTest {

    @Test
    void logsReviewMetadataWithoutSensitivePayloads(CapturedOutput output) {
        var noteRepository = mock(NoteRepository.class);
        var reviewRepository = mock(NoteReviewRepository.class);
        var service = new ReviewService(noteRepository, reviewRepository);
        var userId = UUID.randomUUID();
        var noteId = UUID.randomUUID();
        var note = new Note(
                new User("private@example.com", "secret-password-hash"),
                "Sensitive private note content",
                "Private"
        );
        when(noteRepository.findByIdAndUserId(noteId, userId)).thenReturn(Optional.of(note));

        service.review(
                userId,
                noteId,
                new ReviewRequest(FeedbackType.USEFUL, "Sensitive private response")
        );

        assertThat(output).contains(
                "Review completed",
                "userId=" + userId,
                "noteId=" + noteId,
                "feedbackType=USEFUL"
        );
        assertThat(output).doesNotContain(
                "Sensitive private note content",
                "Sensitive private response",
                "private@example.com",
                "secret-password-hash"
        );
    }
}
