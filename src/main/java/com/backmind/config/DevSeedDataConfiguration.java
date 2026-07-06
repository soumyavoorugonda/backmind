package com.backmind.config;

import com.backmind.note.entity.Note;
import com.backmind.note.repository.NoteRepository;
import com.backmind.user.entity.User;
import com.backmind.user.repository.UserRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("dev")
public class DevSeedDataConfiguration {

    @Bean
    ApplicationRunner seedDevelopmentData(
            UserRepository userRepository,
            NoteRepository noteRepository,
            PasswordEncoder passwordEncoder,
            @Value("${DEV_SECURITY_USER}") String devSecurityUser,
            @Value("${DEV_SECURITY_PASSWORD}") String devSecurityPassword
    ) {
        return arguments -> {
            var demoUser = userRepository.findByEmail(devSecurityUser)
                    .orElseGet(() -> userRepository.saveAndFlush(
                            new User(devSecurityUser, passwordEncoder.encode(devSecurityPassword))
                    ));

            if (noteRepository.findAllByUserId(demoUser.getId()).isEmpty()) {
                noteRepository.saveAndFlush(new Note(
                        demoUser,
                        "Spaced repetition works best when reviews happen before knowledge fades.",
                        "Learning"
                ));
            }
        };
    }
}
