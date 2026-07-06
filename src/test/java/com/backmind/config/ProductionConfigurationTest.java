package com.backmind.config;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductionConfigurationTest {

    @Test
    void externalizesDatabaseCredentialsAndJwtSecret() throws IOException {
        var configuration = Path.of("src/main/resources/application.properties");
        assertTrue(Files.exists(configuration), "Production application.properties must exist");

        var properties = new Properties();
        try (var input = Files.newInputStream(configuration)) {
            properties.load(input);
        }

        assertEquals("${DB_URL}", properties.getProperty("spring.datasource.url"));
        assertEquals("${DB_USERNAME}", properties.getProperty("spring.datasource.username"));
        assertEquals("${DB_PASSWORD}", properties.getProperty("spring.datasource.password"));
        assertEquals("${JWT_SECRET}", properties.getProperty("backmind.jwt.secret"));
    }
}
