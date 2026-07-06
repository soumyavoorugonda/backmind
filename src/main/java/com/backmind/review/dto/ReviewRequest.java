package com.backmind.review.dto;

import com.backmind.review.entity.FeedbackType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReviewRequest(
        @NotNull(message = "Feedback type is required")
        FeedbackType feedbackType,

        @Size(max = 500, message = "User response must not exceed 500 characters")
        String userResponse
) {
}
