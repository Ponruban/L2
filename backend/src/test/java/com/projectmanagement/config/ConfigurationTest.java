package com.projectmanagement.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test configuration loading and environment-specific properties
 */
@SpringBootTest
@ActiveProfiles("dev")
class ConfigurationTest {

    @Autowired
    private Environment environment;

    @Test
    void testDatabaseConfiguration() {
        assertNotNull(environment.getProperty("spring.datasource.url"));
        assertNotNull(environment.getProperty("spring.datasource.username"));
        assertNotNull(environment.getProperty("spring.datasource.password"));
        assertNotNull(environment.getProperty("spring.datasource.driver-class-name"));
    }

    @Test
    void testJwtConfiguration() {
        assertNotNull(environment.getProperty("jwt.secret.key"));
        assertNotNull(environment.getProperty("jwt.expiration.time"));
        assertNotNull(environment.getProperty("jwt.refresh.expiration.time"));
    }

    @Test
    void testServerConfiguration() {
        assertNotNull(environment.getProperty("server.port"));
        assertNotNull(environment.getProperty("server.servlet.context-path"));
    }

    @Test
    void testSecurityConfiguration() {
        assertNotNull(environment.getProperty("app.security.bcrypt.strength"));
        assertNotNull(environment.getProperty("app.security.cors.allowed-origins"));
    }

    @Test
    void testFileUploadConfiguration() {
        assertNotNull(environment.getProperty("spring.servlet.multipart.max-file-size"));
        assertNotNull(environment.getProperty("app.upload.allowed-file-types"));
        assertNotNull(environment.getProperty("app.upload.directory"));
    }

    @Test
    void testLoggingConfiguration() {
        assertNotNull(environment.getProperty("logging.level.root"));
        assertNotNull(environment.getProperty("logging.level.com.projectmanagement"));
    }

    @Test
    void testEmailConfiguration() {
        assertNotNull(environment.getProperty("spring.mail.host"));
        assertNotNull(environment.getProperty("spring.mail.port"));
    }

    @Test
    void testActuatorConfiguration() {
        assertNotNull(environment.getProperty("management.endpoints.web.exposure.include"));
        assertNotNull(environment.getProperty("management.endpoint.health.show-details"));
    }

    @Test
    void testDevelopmentProfileSpecificConfiguration() {
        // These should be specific to dev profile
        assertEquals("true", environment.getProperty("spring.jpa.show-sql"));
        assertEquals("DEBUG", environment.getProperty("logging.level.org.hibernate.SQL"));
        assertTrue(environment.getProperty("app.security.cors.allowed-origins")
                .contains("http://localhost:3000"));
    }
} 