package com.backmind.note.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateNoteRequest(
        @NotBlank(message = "Content is required")
        @Size(max = 300, message = "Content must not exceed 300 characters")
        @Pattern(
                regexp = "^\\S(?:[\\s\\S]*\\S)?$",
                message = "Content must not have leading or trailing whitespace"
        )
        String content,

        @Size(max = 30, message = "Category must not exceed 30 characters")
        @Pattern(regexp = "^[A-Za-z0-9]+$", message = "Category must be alphanumeric")
        String category
) {
}
