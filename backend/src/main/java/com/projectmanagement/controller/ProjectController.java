package com.projectmanagement.controller;

import com.projectmanagement.dto.ApiResponse;
import com.projectmanagement.dto.project.*;
import com.projectmanagement.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/projects")
@Tag(name = "Projects", description = "Project management endpoints")
public class ProjectController {
    
    private final ProjectService projectService;
    
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }
    
    @PostMapping
    @PreAuthorize("hasRole('PROJECT_MANAGER') or hasRole('TEAM_LEAD')")
    @Operation(summary = "Create Project", description = "Create a new project")
    public ResponseEntity<ApiResponse<ProjectResponse>> createProject(@Valid @RequestBody ProjectCreateRequest request) {
        ProjectResponse project = projectService.createProject(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, project, "Project created successfully"));
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProjectListResponse>>> getAllProjects(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long userId) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ProjectListResponse> projects = projectService.getAllProjects(pageable, status, search, userId);
        
        return ResponseEntity.ok(new ApiResponse<>(true, projects, "Projects retrieved successfully"));
    }
    
    @GetMapping("/my-projects")
    public ResponseEntity<ApiResponse<Page<ProjectListResponse>>> getMyProjects(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ProjectListResponse> projects = projectService.getProjectsForCurrentUser(pageable, status, search);
        
        return ResponseEntity.ok(new ApiResponse<>(true, projects, "Your projects retrieved successfully"));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectResponse>> getProjectById(@PathVariable Long id) {
        ProjectResponse project = projectService.getProjectById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, project, "Project retrieved successfully"));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PROJECT_MANAGER') or hasRole('TEAM_LEAD')")
    public ResponseEntity<ApiResponse<ProjectResponse>> updateProject(
            @PathVariable Long id, 
            @Valid @RequestBody ProjectUpdateRequest request) {
        ProjectResponse project = projectService.updateProject(id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, project, "Project updated successfully"));
    }
    
    @PatchMapping("/{id}/archive")
    @PreAuthorize("hasRole('PROJECT_MANAGER') or hasRole('TEAM_LEAD')")
    public ResponseEntity<ApiResponse<Void>> archiveProject(@PathVariable Long id) {
        projectService.archiveProject(id);
        return ResponseEntity.ok(new ApiResponse<>(true, null, "Project archived successfully"));
    }
    
    @PostMapping("/{id}/members")
    @PreAuthorize("hasRole('PROJECT_MANAGER') or hasRole('TEAM_LEAD')")
    public ResponseEntity<ApiResponse<ProjectMemberResponse>> addProjectMember(
            @PathVariable Long id, 
            @Valid @RequestBody ProjectMemberRequest request) {
        ProjectMemberResponse member = projectService.addProjectMember(id, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, member, "Project member added successfully"));
    }
    
    @DeleteMapping("/{projectId}/members/{userId}")
    @PreAuthorize("hasRole('PROJECT_MANAGER') or hasRole('TEAM_LEAD')")
    public ResponseEntity<ApiResponse<Void>> removeProjectMember(
            @PathVariable Long projectId, 
            @PathVariable Long userId) {
        projectService.removeProjectMember(projectId, userId);
        return ResponseEntity.ok(new ApiResponse<>(true, null, "Project member removed successfully"));
    }
} 