package com.backmind.resurfacing;

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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TodayFeedIntegrationTest {

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

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private User user;
    private Note dueNote;
    private Note otherUsersDueNote;
    private String token;

    @BeforeEach
    void createFeedCandidates() {
        user = userRepository.saveAndFlush(new User(
                "feed@example.com",
                passwordEncoder.encode("correct-horse-battery-staple")
        ));
        User otherUser = userRepository.saveAndFlush(new User(
                "other-feed@example.com",
                passwordEncoder.encode("another-password")
        ));

        dueNote = noteRepository.saveAndFlush(new Note(user, "Due private note", "Learning"));
        otherUsersDueNote = noteRepository.saveAndFlush(
                new Note(otherUser, "Another user's due note", "Learning")
        );

        Instant dueAt = Instant.now().minusSeconds(60);
        jdbcTemplate.update(
                "update notes set next_review_at = ? where id in (?, ?)",
                Timestamp.from(dueAt),
                dueNote.getId(),
                otherUsersDueNote.getId()
        );
        token = jwtService.issueToken(user);
    }

    @Test
    void returnsOnlyActiveDueNotesOwnedByTheAuthenticatedUser() throws Exception {
        mockMvc.perform(get("/api/feed/today")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(dueNote.getId().toString()))
                .andExpect(jsonPath("$[0].content").value("Due private note"))
                .andExpect(jsonPath("$[?(@.id == '%s')]".formatted(otherUsersDueNote.getId())).isEmpty());
    }

    @Test
    void capsTheDailyFeedAtFiveNotes() throws Exception {
        for (int index = 1; index <= 5; index++) {
            Note additionalNote = noteRepository.saveAndFlush(
                    new Note(user, "Additional due note " + index, "Learning")
            );
            jdbcTemplate.update(
                    "update notes set next_review_at = ? where id = ?",
                    Timestamp.from(Instant.now().minusSeconds(60)),
                    additionalNote.getId()
            );
        }

        mockMvc.perform(get("/api/feed/today")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)));
    }

    @Test
    void distributesFiveSlotsAcrossDueLostAndOlderNotes() throws Exception {
        Note secondDueNote = noteRepository.saveAndFlush(
                new Note(user, "Second due note", "Learning")
        );
        Note thirdDueNote = noteRepository.saveAndFlush(
                new Note(user, "Third due note", "Learning")
        );
        Note lostNote = noteRepository.saveAndFlush(
                new Note(user, "Lost knowledge note", "Learning")
        );
        Note olderNote = noteRepository.saveAndFlush(
                new Note(user, "Older active note", "Learning")
        );

        Timestamp dueAt = Timestamp.from(Instant.now().minusSeconds(60));
        jdbcTemplate.update(
                "update notes set next_review_at = ? where id in (?, ?)",
                dueAt,
                secondDueNote.getId(),
                thirdDueNote.getId()
        );
        jdbcTemplate.update(
                "update notes set last_seen_at = ?, next_review_at = ? where id = ?",
                Timestamp.from(Instant.now().minus(31, ChronoUnit.DAYS)),
                Timestamp.from(Instant.now().plus(1, ChronoUnit.DAYS)),
                lostNote.getId()
        );
        jdbcTemplate.update(
                "update notes set created_at = ?, next_review_at = ? where id = ?",
                Timestamp.from(Instant.now().minus(10, ChronoUnit.DAYS)),
                Timestamp.from(Instant.now().plus(1, ChronoUnit.DAYS)),
                olderNote.getId()
        );

        mockMvc.perform(get("/api/feed/today")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)))
                .andExpect(jsonPath("$[?(@.id == '%s')]".formatted(dueNote.getId()), hasSize(1)))
                .andExpect(jsonPath("$[?(@.id == '%s')]".formatted(secondDueNote.getId()), hasSize(1)))
                .andExpect(jsonPath("$[?(@.id == '%s')]".formatted(thirdDueNote.getId()), hasSize(1)))
                .andExpect(jsonPath("$[?(@.id == '%s')]".formatted(lostNote.getId()), hasSize(1)))
                .andExpect(jsonPath("$[?(@.id == '%s')]".formatted(olderNote.getId()), hasSize(1)));

        assertEquals(5, eventCount(null));
        assertEquals(3, eventCount("SPACED_REVIEW"));
        assertEquals(1, eventCount("LOST_KNOWLEDGE"));
        assertEquals(1, eventCount("RANDOM"));
    }

    private int eventCount(String reason) {
        String reasonClause = reason == null ? "" : " and reason = '" + reason + "'";
        return jdbcTemplate.queryForObject(
                "select count(*) from resurfacing_events where user_id = ?" + reasonClause,
                Integer.class,
                user.getId()
        );
    }
}
