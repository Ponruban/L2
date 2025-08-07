package com.projectmanagement.controller;

import com.projectmanagement.dto.ApiResponse;
import com.projectmanagement.dto.milestone.MilestoneCreateRequest;
import com.projectmanagement.dto.milestone.MilestoneListResponse;
import com.projectmanagement.dto.milestone.MilestoneResponse;
import com.projectmanagement.dto.milestone.MilestoneUpdateRequest;
import com.projectmanagement.service.MilestoneService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects/{projectId}/milestones")
public class MilestoneController {
    
    private static final Logger logger = LoggerFactory.getLogger(MilestoneController.class);
    
    private final MilestoneService milestoneService;
    
    public MilestoneController(MilestoneService milestoneService) {
        this.milestoneService = milestoneService;
    }
    
    /**
     * Create a new milestone for a project
     * POST /projects/{projectId}/milestones
     */
    @PostMapping
    @PreAuthorize("hasRole('PROJECT_MANAGER') or hasRole('TEAM_LEAD')")
    public ResponseEntity<ApiResponse<MilestoneResponse>> createMilestone(
            @PathVariable Long projectId,
            @Valid @RequestBody MilestoneCreateRequest request) {
        
        logger.info("Creating milestone for project ID: {}", projectId);
        
        MilestoneResponse milestone = milestoneService.createMilestone(projectId, request);
        
        ApiResponse<MilestoneResponse> response = new ApiResponse<>(
                true,
                milestone,
                "Milestone created successfully"
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Get all milestones for a project
     * GET /projects/{projectId}/milestones
     */
    @GetMapping
    @PreAuthorize("hasRole('PROJECT_MANAGER') or hasRole('TEAM_LEAD') or hasRole('DEVELOPER') or hasRole('QA')")
    public ResponseEntity<ApiResponse<MilestoneListResponse>> getProjectMilestones(
            @PathVariable Long projectId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        logger.info("Getting milestones for project ID: {} with status: {}, page: {}, size: {}", 
                   projectId, status, page, size);
        
        MilestoneListResponse milestones = milestoneService.getProjectMilestones(projectId, status, page, size);
        
        ApiResponse<MilestoneListResponse> response = new ApiResponse<>(
                true,
                milestones,
                "Milestones retrieved successfully"
        );
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get milestone by ID
     * GET /milestones/{id}
     */
    @GetMapping("/{milestoneId}")
    @PreAuthorize("hasRole('PROJECT_MANAGER') or hasRole('TEAM_LEAD') or hasRole('DEVELOPER') or hasRole('QA')")
    public ResponseEntity<ApiResponse<MilestoneResponse>> getMilestoneById(
            @PathVariable Long projectId,
            @PathVariable Long milestoneId) {
        
        logger.info("Getting milestone with ID: {} for project ID: {}", milestoneId, projectId);
        
        MilestoneResponse milestone = milestoneService.getMilestoneById(milestoneId);
        
        ApiResponse<MilestoneResponse> response = new ApiResponse<>(
                true,
                milestone,
                "Milestone retrieved successfully"
        );
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Update milestone
     * PUT /milestones/{id}
     */
    @PutMapping("/{milestoneId}")
    @PreAuthorize("hasRole('PROJECT_MANAGER') or hasRole('TEAM_LEAD')")
    public ResponseEntity<ApiResponse<MilestoneResponse>> updateMilestone(
            @PathVariable Long projectId,
            @PathVariable Long milestoneId,
            @Valid @RequestBody MilestoneUpdateRequest request) {
        
        logger.info("Updating milestone with ID: {} for project ID: {}", milestoneId, projectId);
        
        MilestoneResponse milestone = milestoneService.updateMilestone(milestoneId, request);
        
        ApiResponse<MilestoneResponse> response = new ApiResponse<>(
                true,
                milestone,
                "Milestone updated successfully"
        );
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Delete milestone
     * DELETE /milestones/{id}
     */
    @DeleteMapping("/{milestoneId}")
    @PreAuthorize("hasRole('PROJECT_MANAGER')")
    public ResponseEntity<ApiResponse<Void>> deleteMilestone(
            @PathVariable Long projectId,
            @PathVariable Long milestoneId) {
        
        logger.info("Deleting milestone with ID: {} for project ID: {}", milestoneId, projectId);
        
        milestoneService.deleteMilestone(milestoneId);
        
        ApiResponse<Void> response = new ApiResponse<>(
                true,
                null,
                "Milestone deleted successfully"
        );
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get overdue milestones for a project
     * GET /projects/{projectId}/milestones/overdue
     */
    @GetMapping("/overdue")
    @PreAuthorize("hasRole('PROJECT_MANAGER') or hasRole('TEAM_LEAD') or hasRole('DEVELOPER') or hasRole('QA')")
    public ResponseEntity<ApiResponse<List<MilestoneResponse>>> getOverdueMilestones(
            @PathVariable Long projectId) {
        
        logger.info("Getting overdue milestones for project ID: {}", projectId);
        
        List<MilestoneResponse> overdueMilestones = milestoneService.getOverdueMilestones(projectId);
        
        ApiResponse<List<MilestoneResponse>> response = new ApiResponse<>(
                true,
                overdueMilestones,
                "Overdue milestones retrieved successfully"
        );
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get upcoming milestones for a project
     * GET /projects/{projectId}/milestones/upcoming
     */
    @GetMapping("/upcoming")
    @PreAuthorize("hasRole('PROJECT_MANAGER') or hasRole('TEAM_LEAD') or hasRole('DEVELOPER') or hasRole('QA')")
    public ResponseEntity<ApiResponse<List<MilestoneResponse>>> getUpcomingMilestones(
            @PathVariable Long projectId,
            @RequestParam(defaultValue = "7") int days) {
        
        logger.info("Getting upcoming milestones for project ID: {} within {} days", projectId, days);
        
        List<MilestoneResponse> upcomingMilestones = milestoneService.getUpcomingMilestones(projectId, days);
        
        ApiResponse<List<MilestoneResponse>> response = new ApiResponse<>(
                true,
                upcomingMilestones,
                "Upcoming milestones retrieved successfully"
        );
        
        return ResponseEntity.ok(response);
    }
} 