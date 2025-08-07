package com.projectmanagement.security;

import com.projectmanagement.ProjectManagementApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for Role-Based Access Control (RBAC)
 */
@SpringBootTest(classes = ProjectManagementApplication.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
class RbacIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
    void shouldAllowAdminToAccessAllEndpoints() throws Exception {
        // Test public endpoint
        mockMvc.perform(get("/api/v1/test/public"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.user").value("admin@test.com"));

        // Test team endpoint
        mockMvc.perform(get("/api/v1/test/team"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.canEditTasks").value(true));

        // Test manager endpoint
        mockMvc.perform(get("/api/v1/test/manager"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.canEditProjects").value(true));

        // Test admin endpoint
        mockMvc.perform(get("/api/v1/test/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.canManageUsers").value(true));

        // Test project edit
        mockMvc.perform(put("/api/v1/test/projects/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // Test task edit
        mockMvc.perform(put("/api/v1/test/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // Test task assignment
        mockMvc.perform(post("/api/v1/test/tasks/1/assign")
                .param("assigneeId", "user123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // Test user management
        mockMvc.perform(get("/api/v1/test/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(username = "manager@test.com", roles = {"PROJECT_MANAGER"})
    void shouldAllowProjectManagerToAccessManagerEndpoints() throws Exception {
        // Test public endpoint
        mockMvc.perform(get("/api/v1/test/public"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // Test team endpoint
        mockMvc.perform(get("/api/v1/test/team"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.canEditTasks").value(true));

        // Test manager endpoint
        mockMvc.perform(get("/api/v1/test/manager"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.canEditProjects").value(true));

        // Test admin endpoint - should be forbidden
        mockMvc.perform(get("/api/v1/test/admin"))
                .andExpect(status().isForbidden());

        // Test project edit
        mockMvc.perform(put("/api/v1/test/projects/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // Test task edit
        mockMvc.perform(put("/api/v1/test/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // Test task assignment
        mockMvc.perform(post("/api/v1/test/tasks/1/assign")
                .param("assigneeId", "user123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // Test user management - should be forbidden
        mockMvc.perform(get("/api/v1/test/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "member@test.com", roles = {"TEAM_MEMBER"})
    void shouldAllowTeamMemberToAccessTeamEndpoints() throws Exception {
        // Test public endpoint
        mockMvc.perform(get("/api/v1/test/public"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // Test team endpoint
        mockMvc.perform(get("/api/v1/test/team"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.canEditTasks").value(true));

        // Test manager endpoint - should be forbidden
        mockMvc.perform(get("/api/v1/test/manager"))
                .andExpect(status().isForbidden());

        // Test admin endpoint - should be forbidden
        mockMvc.perform(get("/api/v1/test/admin"))
                .andExpect(status().isForbidden());

        // Test project edit - should be forbidden
        mockMvc.perform(put("/api/v1/test/projects/1"))
                .andExpect(status().isForbidden());

        // Test task edit
        mockMvc.perform(put("/api/v1/test/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // Test task assignment - should be forbidden
        mockMvc.perform(post("/api/v1/test/tasks/1/assign")
                .param("assigneeId", "user123"))
                .andExpect(status().isForbidden());

        // Test user management - should be forbidden
        mockMvc.perform(get("/api/v1/test/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user@test.com", roles = {"USER"})
    void shouldDenyUserWithoutProperRoles() throws Exception {
        // Test public endpoint
        mockMvc.perform(get("/api/v1/test/public"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // Test team endpoint - should be forbidden
        mockMvc.perform(get("/api/v1/test/team"))
                .andExpect(status().isForbidden());

        // Test manager endpoint - should be forbidden
        mockMvc.perform(get("/api/v1/test/manager"))
                .andExpect(status().isForbidden());

        // Test admin endpoint - should be forbidden
        mockMvc.perform(get("/api/v1/test/admin"))
                .andExpect(status().isForbidden());

        // Test project edit - should be forbidden
        mockMvc.perform(put("/api/v1/test/projects/1"))
                .andExpect(status().isForbidden());

        // Test task edit - should be forbidden
        mockMvc.perform(put("/api/v1/test/tasks/1"))
                .andExpect(status().isForbidden());

        // Test task assignment - should be forbidden
        mockMvc.perform(post("/api/v1/test/tasks/1/assign")
                .param("assigneeId", "user123"))
                .andExpect(status().isForbidden());

        // Test user management - should be forbidden
        mockMvc.perform(get("/api/v1/test/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldDenyUnauthenticatedAccess() throws Exception {
        // Test public endpoint - should be forbidden
        mockMvc.perform(get("/api/v1/test/public"))
                .andExpect(status().isForbidden());

        // Test team endpoint - should be forbidden
        mockMvc.perform(get("/api/v1/test/team"))
                .andExpect(status().isForbidden());

        // Test manager endpoint - should be forbidden
        mockMvc.perform(get("/api/v1/test/manager"))
                .andExpect(status().isForbidden());

        // Test admin endpoint - should be forbidden
        mockMvc.perform(get("/api/v1/test/admin"))
                .andExpect(status().isForbidden());

        // Test project edit - should be forbidden
        mockMvc.perform(put("/api/v1/test/projects/1"))
                .andExpect(status().isForbidden());

        // Test task edit - should be forbidden
        mockMvc.perform(put("/api/v1/test/tasks/1"))
                .andExpect(status().isForbidden());

        // Test task assignment - should be forbidden
        mockMvc.perform(post("/api/v1/test/tasks/1/assign")
                .param("assigneeId", "user123"))
                .andExpect(status().isForbidden());

        // Test user management - should be forbidden
        mockMvc.perform(get("/api/v1/test/users"))
                .andExpect(status().isForbidden());
    }
} 