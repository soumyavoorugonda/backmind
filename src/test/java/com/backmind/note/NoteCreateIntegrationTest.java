package com.backmind.note;

import com.backmind.auth.JwtService;
import com.backmind.note.entity.BeliefStatus;
import com.backmind.note.entity.NoteStatus;
import com.backmind.note.repository.NoteRepository;
import com.backmind.user.entity.User;
import com.backmind.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class NoteCreateIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private User user;
    private String token;

    @BeforeEach
    void createAuthenticatedUser() {
        user = userRepository.saveAndFlush(new User(
                "learner@example.com",
                passwordEncoder.encode("correct-horse-battery-staple")
        ));
        token = jwtService.issueToken(user);
    }

    @Test
    void createsANoteOwnedByTheAuthenticatedUser() throws Exception {
        mockMvc.perform(post("/api/notes")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "content": "Diversification reduces single-stock risk.",
                                  "category": "Investing"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.content").value("Diversification reduces single-stock risk."))
                .andExpect(jsonPath("$.category").value("Investing"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.currentIntervalDays").value(1));

        var savedNotes = noteRepository.findAllByUserId(user.getId());

        assertThat(savedNotes).singleElement().satisfies(note -> {
            assertThat(note.getUser().getId()).isEqualTo(user.getId());
            assertThat(note.getStatus()).isEqualTo(NoteStatus.ACTIVE);
            assertThat(note.getBeliefStatus()).isEqualTo(BeliefStatus.UNKNOWN);
            assertThat(note.getCurrentIntervalDays()).isEqualTo(1);
            assertThat(note.getNextReviewAt()).isAfter(note.getCreatedAt());
        });
    }

    @Test
    void rejectsCreateNoteWithoutAuthentication() throws Exception {
        mockMvc.perform(post("/api/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"content": "Private note"}
                                """))
                .andExpect(status().isUnauthorized());
    }
}
