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
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class NoteUpdateIntegrationTest {

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
                new Note(user, "Original content", "Learning")
        );
        otherUsersNote = noteRepository.saveAndFlush(
                new Note(otherUser, "Another user's note", "Private")
        );
        token = jwtService.issueToken(user);
    }

    @Test
    void updatesANoteOwnedByTheAuthenticatedUser() throws Exception {
        mockMvc.perform(put("/api/notes/{id}", ownedNote.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "content": "Updated content",
                                  "category": "Knowledge"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ownedNote.getId().toString()))
                .andExpect(jsonPath("$.content").value("Updated content"))
                .andExpect(jsonPath("$.category").value("Knowledge"));

        var updatedNote = noteRepository.findByIdAndUserId(ownedNote.getId(), user.getId()).orElseThrow();
        assertThat(updatedNote.getContent()).isEqualTo("Updated content");
        assertThat(updatedNote.getCategory()).isEqualTo("Knowledge");
        assertThat(updatedNote.getUpdatedAt()).isAfterOrEqualTo(updatedNote.getCreatedAt());
    }

    @Test
    void hidesAnotherUsersNoteWhenUpdating() throws Exception {
        mockMvc.perform(put("/api/notes/{id}", otherUsersNote.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"content": "Attempted overwrite"}
                                """))
                .andExpect(status().isNotFound());

        assertThat(noteRepository.findById(otherUsersNote.getId()).orElseThrow().getContent())
                .isEqualTo("Another user's note");
    }
}
