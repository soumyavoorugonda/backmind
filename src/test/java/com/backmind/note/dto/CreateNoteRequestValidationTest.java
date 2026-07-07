package com.backmind.note.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CreateNoteRequestValidationTest {

    private static Validator validator;

    @BeforeAll
    static void createValidator() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void rejectsBlankContent() {
        var request = new CreateNoteRequest("", null);

        assertHasViolation(request, "content");
    }

    @Test
    void rejectsContentLongerThan300Characters() {
        var request = new CreateNoteRequest("a".repeat(301), null);

        assertHasViolation(request, "content");
    }

    @Test
    void rejectsContentWithLeadingOrTrailingWhitespace() {
        assertHasViolation(new CreateNoteRequest(" leading", null), "content");
        assertHasViolation(new CreateNoteRequest("trailing ", null), "content");
    }

    @Test
    void rejectsNonAlphanumericCategory() {
        var request = new CreateNoteRequest("A valid note", "software-design");

        assertHasViolation(request, "category");
    }

    @Test
    void rejectsCategoryLongerThan30Characters() {
        var request = new CreateNoteRequest("A valid note", "a".repeat(31));

        assertHasViolation(request, "category");
    }

    @Test
    void acceptsValidContentWithAnOptionalCategory() {
        assertTrue(validator.validate(new CreateNoteRequest("A valid note", "Software2")).isEmpty());
        assertTrue(validator.validate(new CreateNoteRequest("A valid note", "Physical activity")).isEmpty());
        assertTrue(validator.validate(new CreateNoteRequest("A valid note", null)).isEmpty());
    }

    @Test
    void rejectsMalformedCategorySpacing() {
        assertHasViolation(new CreateNoteRequest("A valid note", " Physical activity"), "category");
        assertHasViolation(new CreateNoteRequest("A valid note", "Physical  activity"), "category");
        assertHasViolation(new CreateNoteRequest("A valid note", "Physical activity "), "category");
    }

    private static void assertHasViolation(CreateNoteRequest request, String property) {
        Set<ConstraintViolation<CreateNoteRequest>> violations = validator.validate(request);

        assertTrue(
                violations.stream().anyMatch(violation -> property.equals(violation.getPropertyPath().toString())),
                () -> "Expected a validation violation for property: " + property
        );
    }
}
