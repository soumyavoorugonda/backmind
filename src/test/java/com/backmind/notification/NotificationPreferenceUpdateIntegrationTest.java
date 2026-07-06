package com.backmind.notification;

import com.backmind.auth.JwtService;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class NotificationPreferenceUpdateIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private User user;
    private String token;

    @BeforeEach
    void createAuthenticatedUser() {
        user = userRepository.saveAndFlush(new User(
                "notifications@example.com",
                passwordEncoder.encode("correct-horse-battery-staple")
        ));
        token = jwtService.issueToken(user);
    }

    @Test
    void createsPreferencesForTheAuthenticatedUser() throws Exception {
        mockMvc.perform(put("/api/notification-preferences")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "enabled": false,
                                  "preferredTime": "21:45",
                                  "frequency": "DAILY"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enabled").value(false))
                .andExpect(jsonPath("$.preferredTime").value("21:45"))
                .andExpect(jsonPath("$.frequency").value("DAILY"))
                .andExpect(jsonPath("$.userId").doesNotExist());

        Integer ownedPreferenceCount = jdbcTemplate.queryForObject(
                "select count(*) from notification_preferences where user_id = ?",
                Integer.class,
                user.getId()
        );
        assertEquals(1, ownedPreferenceCount);
    }

    @Test
    void retrievesPreferencesForTheAuthenticatedUser() throws Exception {
        mockMvc.perform(put("/api/notification-preferences")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "enabled": true,
                                  "preferredTime": "08:30",
                                  "frequency": "DAILY"
                                }
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/notification-preferences")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enabled").value(true))
                .andExpect(jsonPath("$.preferredTime").value("08:30"))
                .andExpect(jsonPath("$.frequency").value("DAILY"))
                .andExpect(jsonPath("$.userId").doesNotExist());
    }

    @Test
    void createsAndReturnsDefaultsWhenPreferencesDoNotExist() throws Exception {
        mockMvc.perform(get("/api/notification-preferences")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enabled").value(true))
                .andExpect(jsonPath("$.preferredTime").value("09:00"))
                .andExpect(jsonPath("$.frequency").value("DAILY"))
                .andExpect(jsonPath("$.userId").doesNotExist());

        Integer ownedPreferenceCount = jdbcTemplate.queryForObject(
                "select count(*) from notification_preferences where user_id = ?",
                Integer.class,
                user.getId()
        );
        assertEquals(1, ownedPreferenceCount);
    }
}
