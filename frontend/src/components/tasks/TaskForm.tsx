// TaskForm Component
import React, { useState, useEffect } from "react";
import { Box, Typography, Grid } from "@mui/material";
import { Input, Select, Button, Card, DatePicker } from "@/components/ui";
import { useAssignableUsers } from "@/hooks/useUsers";
import { useProjects } from "@/hooks/useProjects";

import type { Task, CreateTaskRequest, UpdateTaskRequest, TaskPriority, TaskStatus } from "@/types/task";

interface TaskFormProps {
  mode: "create" | "edit";
  projectId?: number;
  initialData?: Partial<CreateTaskRequest | UpdateTaskRequest>;
  onSubmit: (data: CreateTaskRequest | UpdateTaskRequest) => void;
  onCancel?: () => void;
  isLoading?: boolean;
}

const TaskForm: React.FC<TaskFormProps> = ({
  mode,
  projectId,
  initialData,
  onSubmit,
  onCancel,
  isLoading = false,
}) => {
  // Fetch assignable users and projects
  const { users, isLoading: usersLoading } = useAssignableUsers();
  const { projects, isLoading: projectsLoading } = useProjects();

  // Form state - use CreateTaskRequest as base since it has all required fields
  const [formData, setFormData] = useState<CreateTaskRequest>({
    title: initialData?.title || "",
    description: initialData?.description || "",
    priority: (initialData?.priority as TaskPriority) || "MEDIUM",
    status: (initialData?.status as TaskStatus) || "TODO",
    deadline: initialData?.deadline || "",
    assigneeId: initialData?.assigneeId,
    milestoneId: initialData?.milestoneId,
  });

  const [errors, setErrors] = useState<Record<string, string>>({});

  // Prepare assignee options
  const assigneeOptions = users?.map(user => ({
    value: user.id,
    label: user.firstName && user.lastName 
      ? `${user.firstName} ${user.lastName}` 
      : user.email || `User ${user.id}`,
  })) || [];



  // Validation
  const validateForm = (): boolean => {
    const newErrors: Record<string, string> = {};

    if (!formData.title.trim()) {
      newErrors.title = "Task title is required";
    } else if (formData.title.length < 3) {
      newErrors.title = "Title must be at least 3 characters";
    }

    if (!formData.priority) {
      newErrors.priority = "Priority is required";
    }

    if (!formData.status) {
      newErrors.status = "Status is required";
    }

    // Validate deadline if provided
    if (formData.deadline) {
      const deadlineDate = new Date(formData.deadline);
      const today = new Date();
      today.setHours(0, 0, 0, 0); // Reset time to start of day
      
      if (deadlineDate < today) {
        newErrors.deadline = "Deadline cannot be in the past";
      }
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    
    if (validateForm()) {
      if (mode === "edit") {
        // For edit mode, convert to UpdateTaskRequest (all fields optional except those that are set)
        const updateData: UpdateTaskRequest = {};
        if (formData.title) updateData.title = formData.title;
        if (formData.description !== undefined) updateData.description = formData.description;
        if (formData.priority) updateData.priority = formData.priority;
        if (formData.status) updateData.status = formData.status;
        if (formData.deadline !== undefined) updateData.deadline = formData.deadline;
        if (formData.assigneeId !== undefined) updateData.assigneeId = formData.assigneeId;
        if (formData.milestoneId !== undefined) updateData.milestoneId = formData.milestoneId;
        
        onSubmit(updateData);
      } else {
        // For create mode, use CreateTaskRequest as is
        onSubmit(formData);
      }
    }
  };

  const handleInputChange = (field: keyof CreateTaskRequest, value: any) => {
    setFormData(prev => ({
      ...prev,
      [field]: value,
    }));
    
    // Clear error when user starts typing
    if (errors[field]) {
      setErrors(prev => ({
        ...prev,
        [field]: "",
      }));
    }
  };

  const handleCancel = () => {
    setFormData({
      title: "",
      description: "",
      priority: "MEDIUM",
      status: "TODO",
      deadline: "",
      assigneeId: undefined,
      milestoneId: undefined,
    });
    setErrors({});
    onCancel?.();
  };

  // Get current project name
  const currentProject = projects?.find(p => p.id === projectId);

  return (
    <Card sx={{ p: 3 }}>
      <Typography variant="h5" sx={{ mb: 3 }}>
        {mode === "create" ? "Create Task" : "Edit Task"}
      </Typography>
      
      {/* Project Information */}
      {currentProject && (
        <Box sx={{ mb: 3, p: 2, bgcolor: 'grey.50', borderRadius: 1 }}>
          <Typography variant="body2" color="text.secondary">
            Project: <strong>{currentProject.name}</strong>
          </Typography>
        </Box>
      )}
      
      <form onSubmit={handleSubmit}>
        <Box sx={{ display: "flex", flexDirection: "column", gap: 3 }}>
          {/* Task Title */}
          <Input
            label="Task Title"
            value={formData.title}
            onChange={(value) => handleInputChange("title", value)}
            error={!!errors.title}
            helperText={errors.title}
            required
            fullWidth
          />

          {/* Description */}
          <Input
            label="Description"
            value={formData.description || ""}
            onChange={(value) => handleInputChange("description", value)}
            multiline
            rows={4}
            fullWidth
          />
          
          {/* Priority and Status */}
          <Grid container spacing={2}>
            <Grid item xs={12} md={6}>
              <Select
                label="Priority"
                value={formData.priority}
                onChange={(value) => handleInputChange("priority", value)}
                options={[
                  { value: "LOW", label: "Low" },
                  { value: "MEDIUM", label: "Medium" },
                  { value: "HIGH", label: "High" },
                  { value: "URGENT", label: "Urgent" },
                ]}
                error={!!errors.priority}
                helperText={errors.priority}
                required
                fullWidth
              />
            </Grid>
            <Grid item xs={12} md={6}>
              <Select
                label="Status"
                value={formData.status}
                onChange={(value) => handleInputChange("status", value)}
                options={[
                  { value: "TODO", label: "To Do" },
                  { value: "IN_PROGRESS", label: "In Progress" },
                  { value: "REVIEW", label: "Review" },
                  { value: "DONE", label: "Done" },
                  { value: "CANCELLED", label: "Cancelled" },
                ]}
                error={!!errors.status}
                helperText={errors.status}
                required
                fullWidth
              />
            </Grid>
          </Grid>
          
          {/* Deadline and Assignee */}
          <Grid container spacing={2}>
            <Grid item xs={12} md={6}>
              <DatePicker
                label="Deadline"
                value={formData.deadline || null}
                onChange={(value) => handleInputChange("deadline", value)}
                placeholder="Select deadline"
                error={!!errors.deadline}
                helperText={errors.deadline}
                minDate={new Date()}
                fullWidth
              />
            </Grid>
            <Grid item xs={12} md={6}>
              <Select
                label="Assignee"
                value={formData.assigneeId}
                onChange={(value) => {
                  handleInputChange("assigneeId", value);
                }}
                options={assigneeOptions}
                placeholder={usersLoading ? "Loading users..." : "Select assignee"}
                disabled={usersLoading}
                fullWidth
              />
            </Grid>
          </Grid>
          
          {/* Form Actions */}
          <Box sx={{ display: "flex", gap: 2, justifyContent: "flex-end", mt: 2 }}>
            {onCancel && (
              <Button
                variant="outlined"
                onClick={handleCancel}
                disabled={isLoading}
              >
                Cancel
              </Button>
            )}
            <Button
              type="submit"
              variant="contained"
              disabled={isLoading}
              loading={isLoading}
            >
              {mode === "create" ? "Create Task" : "Update Task"}
            </Button>
          </Box>
        </Box>
      </form>
    </Card>
  );
};

export default TaskForm;