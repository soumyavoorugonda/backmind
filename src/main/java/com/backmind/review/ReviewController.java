package com.backmind.review;

import com.backmind.note.dto.NoteResponse;
import com.backmind.auth.AuthenticatedUser;
import com.backmind.review.dto.ReviewRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/notes/{id}/review")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public NoteResponse review(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable("id") UUID noteId,
            @Valid @RequestBody ReviewRequest request
    ) {
        return reviewService.review(user.id(), noteId, request);
    }
}
