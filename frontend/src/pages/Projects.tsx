import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Box,
  Typography,
  Grid,
  Card,
  CardContent,
  Button,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  IconButton,
  Tooltip,
  Chip,
  LinearProgress,
  Alert,
} from '@mui/material';
import {
  Add as AddIcon,
  Visibility as ViewIcon,
  Edit as EditIcon,
  Delete as DeleteIcon,
} from '@mui/icons-material';
import { useProjects, useCreateProject, useUpdateProject, useDeleteProject, useProject } from '@/hooks/useProjects';
import ProjectForm from '@/components/projects/ProjectForm';
import type { Project, ProjectStatus, CreateProjectRequest, UpdateProjectRequest } from '@/types';

interface ProjectWithStats extends Project {
  progress: number;
  memberCount: number;
  taskCount: number;
}

const Projects: React.FC = () => {
  const navigate = useNavigate();
  const { projects, isLoading } = useProjects();
  const { createProject, isCreating } = useCreateProject();
  const { updateProject, isUpdating } = useUpdateProject();
  const { deleteProject, isDeleting } = useDeleteProject();
  const [openDialog, setOpenDialog] = useState(false);
  const [editingProjectId, setEditingProjectId] = useState<number | null>(null);
  
  // Fetch full project details when editing
  const { project: editingProjectDetails, isLoading: isLoadingProjectDetails } = useProject({
    projectId: editingProjectId || 0
  });

  const handleOpenDialog = (project?: ProjectWithStats) => {
    if (project) {
      setEditingProjectId(project.id);
    } else {
      setEditingProjectId(null);
    }
    setOpenDialog(true);
  };

  const handleCloseDialog = () => {
    setOpenDialog(false);
    setEditingProjectId(null);
  };

  const handleSubmit = async (data: CreateProjectRequest | UpdateProjectRequest) => {
    try {
      if (editingProjectId) {
        await updateProject({ projectId: editingProjectId, data: data as UpdateProjectRequest });
      } else {
        await createProject(data as CreateProjectRequest);
      }
      handleCloseDialog();
    } catch (error) {
      console.error('Error saving project:', error);
      
      // Show user-friendly error message
      const errorMessage = error instanceof Error ? error.message : 'An error occurred while saving the project';
      
      // Check if it's an authentication error
      if (errorMessage.includes('401') || errorMessage.includes('Unauthorized') || errorMessage.includes('token')) {
        alert('Your session has expired. Please log in again.');
        // Don't redirect automatically - let the user handle it
      } else {
        alert(`Error: ${errorMessage}`);
      }
    }
  };

  const handleDelete = async (projectId: number) => {
    if (window.confirm('Are you sure you want to delete this project?')) {
      try {
        await deleteProject(projectId);
      } catch (error) {
        console.error('Error deleting project:', error);
      }
    }
  };

  const getStatusColor = (status: ProjectStatus) => {
    switch (status) {
      case 'ACTIVE':
        return 'success';
      case 'ON_HOLD':
        return 'warning';
      case 'COMPLETED':
        return 'info';
      case 'CANCELLED':
        return 'error';
      default:
        return 'default';
    }
  };

  const getStatusLabel = (status: ProjectStatus) => {
    switch (status) {
      case 'ACTIVE':
        return 'Active';
      case 'ON_HOLD':
        return 'On Hold';
      case 'COMPLETED':
        return 'Completed';
      case 'CANCELLED':
        return 'Cancelled';
      default:
        return status;
    }
  };

  if (isLoading) {
    return (
      <Box sx={{ p: 3 }}>
        <LinearProgress />
      </Box>
    );
  }

  return (
    <Box sx={{ p: 3 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4">Projects</Typography>
        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={() => handleOpenDialog()}
        >
          Create Project
        </Button>
      </Box>

      {projects.length === 0 ? (
        <Alert severity="info">
          No projects found. Create your first project to get started.
        </Alert>
      ) : (
        <Grid container spacing={3}>
          {projects.map((project) => {
            const projectWithStats: ProjectWithStats = {
              ...project,
              progress: 0, // This would be calculated from tasks
              memberCount: 0, // This would come from project members
              taskCount: 0, // This would come from project tasks
            };

            return (
              <Grid item xs={12} sm={6} md={4} key={project.id}>
                <Card>
                  <CardContent>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', mb: 2 }}>
                      <Typography variant="h6" component="div" sx={{ flex: 1, mr: 1 }}>
                        {project.name}
                      </Typography>
                      <Chip
                        label={getStatusLabel(project.status)}
                        color={getStatusColor(project.status) as any}
                        size="small"
                      />
                    </Box>
                    
                    {project.description && (
                      <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
                        {project.description}
                      </Typography>
                    )}

                    <Box sx={{ mb: 2 }}>
                      <Typography variant="body2" color="text.secondary">
                        Progress: {projectWithStats.progress}%
                      </Typography>
                      <LinearProgress 
                        variant="determinate" 
                        value={projectWithStats.progress} 
                        sx={{ mt: 1 }}
                      />
                    </Box>

                    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                      <Typography variant="body2" color="text.secondary">
                        {projectWithStats.memberCount} members • {projectWithStats.taskCount} tasks
                      </Typography>
                    </Box>

                    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                      <Button
                        size="small"
                        onClick={() => navigate(`/projects/${project.id}`)}
                      >
                        View Details
                      </Button>
                      
                      <Box>
                        <Tooltip title="View Project">
                          <IconButton
                            size="small"
                            onClick={() => navigate(`/projects/${project.id}`)}
                          >
                            <ViewIcon />
                          </IconButton>
                        </Tooltip>
                        <Tooltip title="Edit Project">
                          <IconButton
                            size="small"
                            onClick={() => handleOpenDialog(projectWithStats)}
                          >
                            <EditIcon />
                          </IconButton>
                        </Tooltip>
                        <Tooltip title="Delete Project">
                          <IconButton
                            size="small"
                            onClick={() => handleDelete(project.id)}
                            color="error"
                          >
                            <DeleteIcon />
                          </IconButton>
                        </Tooltip>
                      </Box>
                    </Box>
                  </CardContent>
                </Card>
              </Grid>
            );
          })}
        </Grid>
      )}

      {/* Create/Edit Project Dialog */}
      <Dialog open={openDialog} onClose={handleCloseDialog} maxWidth="md" fullWidth>
        <DialogContent sx={{ p: 0 }}>
          {editingProjectId && isLoadingProjectDetails ? (
            <Box sx={{ p: 3, textAlign: 'center' }}>
              <Typography>Loading project details...</Typography>
            </Box>
          ) : (
            <ProjectForm
              mode={editingProjectId ? 'edit' : 'create'}
              onSubmit={handleSubmit}
              onCancel={handleCloseDialog}
              initialData={editingProjectDetails}
            />
          )}
        </DialogContent>
      </Dialog>
    </Box>
  );
};

export default Projects; 