import React, { useState, useEffect } from 'react';
import {
  Box,
  Typography,
  Grid,
  Card,
  CardContent,
  Button,
  Chip,
  IconButton,
  Tooltip,
  Alert,
  Snackbar,
} from '@mui/material';
import {
  Add as AddIcon,
  Edit as EditIcon,
  Delete as DeleteIcon,
  Visibility as ViewIcon,
  Flag as PriorityIcon,
} from '@mui/icons-material';
import { useTasks, useCreateTask, useUpdateTask, useDeleteTask } from '@/hooks/useTasks';
import { useProjects } from '@/hooks/useProjects';
import { LoadingSpinner, SkeletonCard, Modal } from '@/components/ui';
import TaskForm from '@/components/tasks/TaskForm';
import { useNavigate } from 'react-router-dom';

import type { Task, TaskStatus, TaskPriority, CreateTaskRequest, UpdateTaskRequest } from '@/types/task';

interface TaskWithDetails extends Task {
  assignee?: {
    id: number;
    firstName: string;
    lastName: string;
    email: string;
  };
  project?: {
    id: number;
    name: string;
  };
  estimatedHours?: number;
  actualHours?: number;
}

const Tasks: React.FC = () => {
  const navigate = useNavigate();
  const { tasks, isLoading } = useTasks();
  const { projects, isLoading: projectsLoading } = useProjects();
  
  // State for modal and form
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [selectedProjectId, setSelectedProjectId] = useState<number | null>(null);
  const [editingTask, setEditingTask] = useState<Task | null>(null);
  const [snackbar, setSnackbar] = useState<{
    open: boolean;
    message: string;
    severity: 'success' | 'error' | 'info';
  }>({
    open: false,
    message: '',
    severity: 'info',
  });
  

  
  // Mutation hooks
  const createTaskMutation = useCreateTask({
    onSuccess: (data) => {
      setSnackbar({
        open: true,
        message: 'Task created successfully!',
        severity: 'success',
      });
      setIsCreateModalOpen(false);
      setSelectedProjectId(null);
    },
    onError: (error) => {
      setSnackbar({
        open: true,
        message: `Failed to create task: ${error.message}`,
        severity: 'error',
      });
    },
  });

  const updateTaskMutation = useUpdateTask({
    onSuccess: (data) => {
      setSnackbar({
        open: true,
        message: 'Task updated successfully!',
        severity: 'success',
      });
      setIsEditModalOpen(false);
      setEditingTask(null);
    },
    onError: (error) => {
      setSnackbar({
        open: true,
        message: `Failed to update task: ${error.message}`,
        severity: 'error',
      });
    },
  });

  const deleteTaskMutation = useDeleteTask({
    onSuccess: () => {
      setSnackbar({
        open: true,
        message: 'Task deleted successfully!',
        severity: 'success',
      });
    },
    onError: (error) => {
      setSnackbar({
        open: true,
        message: `Failed to delete task: ${error.message}`,
        severity: 'error',
      });
    },
  });

  const handleCreateTask = () => {
    if (!projects || projects.length === 0) {
      setSnackbar({
        open: true,
        message: 'No projects available. Please create a project first.',
        severity: 'error',
      });
      return;
    }
    
    // If only one project, auto-select it
    if (projects.length === 1) {
      setSelectedProjectId(projects[0].id);
    }
    
    setIsCreateModalOpen(true);
  };

  const handleEditTask = (task: Task) => {
    setEditingTask(task);
    setIsEditModalOpen(true);
  };

  const handleProjectSelect = (projectId: number) => {
    setSelectedProjectId(projectId);
  };

  const handleTaskSubmit = async (formData: CreateTaskRequest) => {
    if (!selectedProjectId) {
      setSnackbar({
        open: true,
        message: 'Please select a project first.',
        severity: 'error',
      });
      return;
    }

    try {
      await createTaskMutation.mutateAsync({
        projectId: selectedProjectId,
        data: formData,
      });
    } catch (error) {
      // Error is handled by the mutation's onError callback
      console.error('Error creating task:', error);
    }
  };

  const handleTaskUpdate = async (formData: UpdateTaskRequest) => {
    if (!editingTask) {
      setSnackbar({
        open: true,
        message: 'No task selected for editing.',
        severity: 'error',
      });
      return;
    }

    try {
      await updateTaskMutation.mutateAsync({
        taskId: editingTask.id,
        data: formData,
      });
    } catch (error) {
      // Error is handled by the mutation's onError callback
      console.error('Error updating task:', error);
    }
  };

  const handleDelete = async (taskId: number) => {
    if (window.confirm('Are you sure you want to delete this task?')) {
      try {
        await deleteTaskMutation.mutateAsync(taskId);
      } catch (error) {
        console.error('Error deleting task:', error);
      }
    }
  };

  const handleCloseSnackbar = () => {
    setSnackbar(prev => ({ ...prev, open: false }));
  };

  const handleCloseCreateModal = () => {
    setIsCreateModalOpen(false);
    setSelectedProjectId(null);
  };

  const handleCloseEditModal = () => {
    setIsEditModalOpen(false);
    setEditingTask(null);
  };

  if (isLoading || projectsLoading) {
    return (
      <Box sx={{ p: 3 }}>
        <Typography variant="h4" sx={{ mb: 3 }}>
          Tasks
        </Typography>
        <Grid container spacing={3}>
          {Array.from({ length: 6 }).map((_, index) => (
            <Grid item xs={12} md={6} lg={4} key={index}>
              <SkeletonCard title={true} content={3} actions={true} height={200} />
            </Grid>
          ))}
        </Grid>
      </Box>
    );
  }

  return (
    <Box sx={{ p: 3 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4">Tasks</Typography>
        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={handleCreateTask}
          disabled={createTaskMutation.isPending}
        >
          Create Task
        </Button>
      </Box>

      {/* No Projects Alert */}
      {(!projects || projects.length === 0) && (
        <Alert severity="warning" sx={{ mb: 2 }}>
          No projects available. Please create a project first before creating tasks.
        </Alert>
      )}

      {/* No Tasks Alert */}
      {projects && projects.length > 0 && (!tasks || tasks.length === 0) && (
        <Alert severity="info" sx={{ mb: 2 }}>
          No tasks found. Create your first task to get started!
        </Alert>
      )}

      {/* Debug Info */}
      {/* {import.meta.env.DEV && (
        <Alert severity="info" sx={{ mb: 2 }}>
          Debug: {tasks?.length || 0} tasks, {projects?.length || 0} projects
        </Alert>
      )} */}

      <Grid container spacing={3}>
        {tasks?.map((task: Task) => (
          <Grid item xs={12} md={6} lg={4} key={task.id}>
            <Card sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
              <CardContent sx={{ flexGrow: 1 }}>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', mb: 2 }}>
                  <Typography variant="h6" component="h2" sx={{ flexGrow: 1, mr: 1 }}>
                    {task.title}
                  </Typography>
                  <Box sx={{ display: 'flex', gap: 1 }}>
                    <Chip
                      label={task.status}
                      color="primary"
                      size="small"
                    />
                    <Chip
                      label={task.priority}
                      color="secondary"
                      size="small"
                      icon={<PriorityIcon />}
                    />
                  </Box>
                </Box>

                <Typography variant="body2" color="text.secondary" sx={{ mb: 2, minHeight: 40 }}>
                  {task.description || 'No description available'}
                </Typography>

                <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1, mb: 2 }}>
                  <Typography variant="body2" color="text.secondary">
                    Project: {task.projectName || `ID: ${task.projectId}`}
                  </Typography>
                  {task.assignee && (
                    <Typography variant="body2" color="text.secondary">
                      Assignee: {task.assignee.firstName} {task.assignee.lastName}
                    </Typography>
                  )}
                  {task.deadline && (
                    <Typography variant="body2" color="text.secondary">
                      Due: {new Date(task.deadline).toLocaleDateString()}
                    </Typography>
                  )}
                </Box>

                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <Typography variant="body2" color="text.secondary">
                    Created: {new Date(task.createdAt).toLocaleDateString()}
                  </Typography>
                  <Box>
                    <Tooltip title="View Details">
                      <IconButton size="small">
                        <ViewIcon />
                      </IconButton>
                    </Tooltip>
                    <Tooltip title="Edit Task">
                      <IconButton
                        size="small"
                        onClick={() => handleEditTask(task)}
                        disabled={updateTaskMutation.isPending}
                      >
                        <EditIcon />
                      </IconButton>
                    </Tooltip>
                    <Tooltip title="Delete Task">
                      <IconButton
                        size="small"
                        onClick={() => handleDelete(task.id)}
                        color="error"
                        disabled={deleteTaskMutation.isPending}
                      >
                        <DeleteIcon />
                      </IconButton>
                    </Tooltip>
                  </Box>
                </Box>
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>

      {/* Create Task Modal */}
      <Modal
        open={isCreateModalOpen}
        onClose={handleCloseCreateModal}
        title="Create New Task"
        maxWidth="md"
        fullWidth
      >
        {selectedProjectId ? (
          <TaskForm
            mode="create"
            projectId={selectedProjectId}
            onSubmit={handleTaskSubmit}
            onCancel={handleCloseCreateModal}
            isLoading={createTaskMutation.isPending}
          />
        ) : projects && projects.length === 1 ? (
          <Box sx={{ textAlign: 'center', py: 4 }}>
            <Typography variant="h6" sx={{ mb: 2 }}>
              Create Task for Project
            </Typography>
            <Typography variant="body1" sx={{ mb: 3 }}>
              You have one project: <strong>{projects[0].name}</strong>
            </Typography>
            <Button
              variant="contained"
              onClick={() => setSelectedProjectId(projects[0].id)}
              disabled={createTaskMutation.isPending}
              size="large"
            >
              Continue to Create Task
            </Button>
          </Box>
        ) : (
          <Box sx={{ textAlign: 'center', py: 4 }}>
            <Typography variant="h6" sx={{ mb: 2 }}>
              Select Project for New Task
            </Typography>
            <Typography variant="body1" sx={{ mb: 3 }}>
              Choose which project this task belongs to:
            </Typography>
            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, alignItems: 'center', maxWidth: 400, mx: 'auto' }}>
              {projects?.map((project) => (
                <Button
                  key={project.id}
                  variant="outlined"
                  onClick={() => handleProjectSelect(project.id)}
                  sx={{ 
                    minWidth: 300, 
                    py: 2,
                    justifyContent: 'flex-start',
                    textAlign: 'left'
                  }}
                  startIcon={<div style={{ width: 12, height: 12, borderRadius: '50%', backgroundColor: '#1976d2' }} />}
                >
                  <Box sx={{ textAlign: 'left' }}>
                    <Typography variant="body1" sx={{ fontWeight: 'bold' }}>
                      {project.name}
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      Project ID: {project.id}
                    </Typography>
                  </Box>
                </Button>
              ))}
            </Box>
            <Button
              variant="text"
              onClick={handleCloseCreateModal}
              sx={{ mt: 3 }}
            >
              Cancel
            </Button>
          </Box>
        )}
      </Modal>

      {/* Edit Task Modal */}
      <Modal
        open={isEditModalOpen}
        onClose={handleCloseEditModal}
        title="Edit Task"
        maxWidth="md"
        fullWidth
      >
        {editingTask && (
          <TaskForm
            mode="edit"
            projectId={editingTask.projectId}
            initialData={{
              title: editingTask.title,
              description: editingTask.description,
              priority: editingTask.priority,
              status: editingTask.status,
              deadline: editingTask.deadline,
              assigneeId: editingTask.assignee?.id || editingTask.assigneeId,
              milestoneId: editingTask.milestoneId,
            }}
            onSubmit={handleTaskUpdate}
            onCancel={handleCloseEditModal}
            isLoading={updateTaskMutation.isPending}
          />
        )}
      </Modal>

      {/* Snackbar for notifications */}
      <Snackbar
        open={snackbar.open}
        autoHideDuration={6000}
        onClose={handleCloseSnackbar}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
      >
        <Alert
          onClose={handleCloseSnackbar}
          severity={snackbar.severity}
          sx={{ width: '100%' }}
        >
          {snackbar.message}
        </Alert>
      </Snackbar>
    </Box>
  );
};

export default Tasks; 