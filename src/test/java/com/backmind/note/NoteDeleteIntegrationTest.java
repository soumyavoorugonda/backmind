package com.backmind.note;

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
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class NoteDeleteIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private User user;
    private Note ownedNote;
    private Note otherUsersNote;
    private String token;

    @BeforeEach
    void createNotesForTwoUsers() {
        user = userRepository.saveAndFlush(new User(
                "learner@example.com",
                passwordEncoder.encode("correct-horse-battery-staple")
        ));
        User otherUser = userRepository.saveAndFlush(new User(
                "other@example.com",
                passwordEncoder.encode("another-password")
        ));

        ownedNote = noteRepository.saveAndFlush(
                new Note(user, "My private note", "Learning")
        );
        otherUsersNote = noteRepository.saveAndFlush(
                new Note(otherUser, "Another user's note", "Private")
        );
        token = jwtService.issueToken(user);
    }

    @Test
    void softDeletesANoteOwnedByTheAuthenticatedUser() throws Exception {
        mockMvc.perform(delete("/api/notes/{id}", ownedNote.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        entityManager.clear();
        assertThat(noteRepository.findById(ownedNote.getId())).isEmpty();
        assertThat(jdbcTemplate.queryForObject(
                "select status from notes where id = ?",
                String.class,
                ownedNote.getId()
        )).isEqualTo("DELETED");
    }

    @Test
    void hidesAnotherUsersNoteWhenDeleting() throws Exception {
        mockMvc.perform(delete("/api/notes/{id}", otherUsersNote.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());

        assertThat(noteRepository.findById(otherUsersNote.getId())).isPresent();
    }
}
