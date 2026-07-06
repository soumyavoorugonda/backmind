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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class LostFeedIntegrationTest {

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

    private Note lostNote;
    private Note recentNote;
    private Note otherUsersLostNote;
    private String token;

    @BeforeEach
    void createLostKnowledgeCandidates() {
        User user = userRepository.saveAndFlush(new User(
                "lost@example.com",
                passwordEncoder.encode("correct-horse-battery-staple")
        ));
        User otherUser = userRepository.saveAndFlush(new User(
                "other-lost@example.com",
                passwordEncoder.encode("another-password")
        ));

        lostNote = noteRepository.saveAndFlush(new Note(user, "Forgotten private note", "Learning"));
        recentNote = noteRepository.saveAndFlush(new Note(user, "Recently seen note", "Learning"));
        otherUsersLostNote = noteRepository.saveAndFlush(
                new Note(otherUser, "Another user's forgotten note", "Learning")
        );

        Timestamp lostAt = Timestamp.from(Instant.now().minus(31, ChronoUnit.DAYS));
        jdbcTemplate.update(
                "update notes set last_seen_at = ? where id in (?, ?)",
                lostAt,
                lostNote.getId(),
                otherUsersLostNote.getId()
        );
        token = jwtService.issueToken(user);
    }

    @Test
    void returnsOnlyAuthenticatedUsersActiveNotesUnseenForAtLeastThirtyDays() throws Exception {
        mockMvc.perform(get("/api/feed/lost")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(lostNote.getId().toString()))
                .andExpect(jsonPath("$[0].content").value("Forgotten private note"))
                .andExpect(jsonPath("$[?(@.id == '%s')]".formatted(recentNote.getId())).isEmpty())
                .andExpect(jsonPath("$[?(@.id == '%s')]".formatted(otherUsersLostNote.getId())).isEmpty());
    }
}
