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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class NoteListIntegrationTest {

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
                new Note(user, "My private note", "Learning")
        );
        otherUsersNote = noteRepository.saveAndFlush(
                new Note(otherUser, "Another user's private note", "Learning")
        );
        token = jwtService.issueToken(user);
    }

    @Test
    void listsOnlyNotesOwnedByTheAuthenticatedUser() throws Exception {
        mockMvc.perform(get("/api/notes")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(ownedNote.getId().toString()))
                .andExpect(jsonPath("$[0].content").value("My private note"))
                .andExpect(jsonPath("$[0].user").doesNotExist())
                .andExpect(jsonPath("$[?(@.id == '%s')]".formatted(otherUsersNote.getId())).isEmpty());
    }
}
