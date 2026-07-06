package com.backmind.review;

import com.backmind.auth.JwtService;
import com.backmind.note.entity.Note;
import com.backmind.note.repository.NoteRepository;
import com.backmind.user.entity.User;
import com.backmind.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UsefulReviewIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private User user;
    private Note note;
    private String token;

    @BeforeEach
    void createAuthenticatedUserAndNote() {
        user = userRepository.saveAndFlush(new User(
                "learner@example.com",
                passwordEncoder.encode("correct-horse-battery-staple")
        ));
        note = noteRepository.saveAndFlush(
                new Note(user, "Diversification reduces single-stock risk.", "Investing")
        );
        token = jwtService.issueToken(user);
    }

    @Test
    void recordsUsefulFeedbackAndAdvancesTheReviewInterval() throws Exception {
        Instant previousNextReviewAt = note.getNextReviewAt();

        mockMvc.perform(post("/api/notes/{id}/review", note.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "feedbackType": "USEFUL",
                                  "userResponse": "Still useful for investing basics."
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(note.getId().toString()))
                .andExpect(jsonPath("$.currentIntervalDays").value(3))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        Note updatedNote = noteRepository.findByIdAndUserId(note.getId(), user.getId()).orElseThrow();
        assertThat(updatedNote.getCurrentIntervalDays()).isEqualTo(3);
        assertThat(updatedNote.getNextReviewAt()).isAfter(previousNextReviewAt);

        assertThat(jdbcTemplate.queryForObject(
                """
                select count(*) from note_reviews
                where note_id = ? and user_id = ? and feedback_type = 'USEFUL'
                """,
                Integer.class,
                note.getId(),
                user.getId()
        )).isEqualTo(1);
    }
}
