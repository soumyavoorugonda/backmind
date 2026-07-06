package com.backmind.config;

import com.backmind.note.repository.NoteRepository;
import com.backmind.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest(properties = {
        "DEV_SECURITY_USER=demo@backmind.app",
        "DEV_SECURITY_PASSWORD=test-only-demo-password"
})
@ActiveProfiles("dev")
class SeedDataIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NoteRepository noteRepository;

    @Test
    void seedsDemoUserWithOwnedNotesInDevelopmentProfile() {
        var demoUser = userRepository.findByEmail("demo@backmind.app").orElseThrow();

        assertFalse(noteRepository.findAllByUserId(demoUser.getId()).isEmpty());
    }
}
