package com.projectmanagement.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test OpenAPI configuration
 */
@SpringBootTest
class OpenApiConfigTest {

    @Autowired
    private OpenAPI openAPI;

    @Test
    void testOpenApiConfiguration() {
        assertNotNull(openAPI);
        assertNotNull(openAPI.getInfo());
        assertEquals("Project Management Dashboard API", openAPI.getInfo().getTitle());
        assertEquals("1.0.0", openAPI.getInfo().getVersion());
        assertNotNull(openAPI.getInfo().getDescription());
        assertTrue(openAPI.getInfo().getDescription().contains("RESTful API"));
        
        // Test servers configuration
        assertNotNull(openAPI.getServers());
        assertEquals(2, openAPI.getServers().size());
        
        // Test security configuration
        assertNotNull(openAPI.getSecurity());
        assertEquals(1, openAPI.getSecurity().size());
        
        // Test components configuration
        assertNotNull(openAPI.getComponents());
        assertNotNull(openAPI.getComponents().getSecuritySchemes());
        assertTrue(openAPI.getComponents().getSecuritySchemes().containsKey("Bearer Authentication"));
    }
} 