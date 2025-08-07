import React from 'react';
import {
  Box,
  Typography,
  Grid,
  Card,
  CardContent,
  Button,
  Alert,
} from '@mui/material';
import {
  Add as AddIcon,
  TrendingUp as TrendingUpIcon,
  Assignment as ProjectIcon,
  Task as TaskIcon,
  Person as ProfileIcon,
  Analytics as AnalyticsIcon,
} from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import { useAuthContext } from '@/contexts/AuthContext';
import { useProjects } from '@/hooks/useProjects';
import { LoadingSpinner } from '@/components/ui';

const Dashboard: React.FC = () => {
  const navigate = useNavigate();
  const { user, isAuthenticated, isLoading } = useAuthContext();
  const { projects, totalProjects, isLoading: isLoadingProjects } = useProjects();

  if (isLoading) {
    return (
      <Box sx={{ p: 3 }}>
        <Typography variant="h4" sx={{ mb: 3 }}>
          Dashboard
        </Typography>
        <LoadingSpinner />
      </Box>
    );
  }

  if (!isAuthenticated || !user) {
    return (
      <Box sx={{ p: 3 }}>
        <Typography variant="h4" sx={{ mb: 3 }}>
          Dashboard
        </Typography>
        <Alert severity="error">
          You are not authenticated. Please log in to access the dashboard.
        </Alert>
      </Box>
    );
  }

  return (
    <Box sx={{ p: 3 }}>
      {/* Header */}
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4">
          Welcome back, {user.firstName}!
        </Typography>
        <Box sx={{ display: 'flex', gap: 2 }}>
          <Button
            variant="contained"
            startIcon={<AddIcon />}
            onClick={() => navigate('/projects')}
            sx={{ minWidth: 120 }}
          >
            New Project
          </Button>
          <Button
            variant="outlined"
            startIcon={<AddIcon />}
            onClick={() => navigate('/tasks')}
            sx={{ minWidth: 120 }}
          >
            New Task
          </Button>
        </Box>
      </Box>

      {/* Debug Information */}
      {/* <Alert severity="info" sx={{ mb: 3 }}>
        <Typography variant="body2">
          <strong>Debug Info:</strong> User ID: {user.id}, Role: {user.role}, Email: {user.email}
        </Typography>
      </Alert> */}

      {/* Statistics Cards */}
      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                <Box>
                  <Typography color="textSecondary" gutterBottom variant="body2">
                    Total Projects
                  </Typography>
                  <Typography variant="h4" component="div">
                    {isLoadingProjects ? '...' : totalProjects}
                  </Typography>
                </Box>
                <ProjectIcon color="primary" sx={{ fontSize: 40 }} />
              </Box>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                <Box>
                  <Typography color="textSecondary" gutterBottom variant="body2">
                    Total Tasks
                  </Typography>
                  <Typography variant="h4" component="div">
                    {isLoadingProjects ? '...' : '0'}
                  </Typography>
                </Box>
                <TaskIcon color="secondary" sx={{ fontSize: 40 }} />
              </Box>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                <Box>
                  <Typography color="textSecondary" gutterBottom variant="body2">
                    Team Members
                  </Typography>
                  <Typography variant="h4" component="div">
                    {isLoadingProjects ? '...' : '0'}
                  </Typography>
                </Box>
                <ProfileIcon color="success" sx={{ fontSize: 40 }} />
              </Box>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                <Box>
                  <Typography color="textSecondary" gutterBottom variant="body2">
                    Active Projects
                  </Typography>
                  <Typography variant="h4" component="div">
                    {isLoadingProjects ? '...' : projects.filter(p => p.status === 'ACTIVE').length}
                  </Typography>
                </Box>
                <TrendingUpIcon color="info" sx={{ fontSize: 40 }} />
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Quick Actions Section */}
      <Box sx={{ mt: 4 }}>
        <Typography variant="h5" sx={{ mb: 3 }}>
          Quick Actions
        </Typography>
        <Grid container spacing={2}>
          <Grid item xs={12} sm={6} md={3}>
            <Card
              sx={{
                cursor: 'pointer',
                '&:hover': {
                  boxShadow: 4,
                  transform: 'translateY(-2px)',
                  transition: 'all 0.2s ease-in-out'
                }
              }}
              onClick={() => navigate('/projects')}
            >
              <CardContent sx={{ textAlign: 'center', py: 3 }}>
                <ProjectIcon color="primary" sx={{ fontSize: 48, mb: 2 }} />
                <Typography variant="h6" gutterBottom>
                  Manage Projects
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  Create, edit, and track your projects
                </Typography>
              </CardContent>
            </Card>
          </Grid>
          <Grid item xs={12} sm={6} md={3}>
            <Card
              sx={{
                cursor: 'pointer',
                '&:hover': {
                  boxShadow: 4,
                  transform: 'translateY(-2px)',
                  transition: 'all 0.2s ease-in-out'
                }
              }}
              onClick={() => navigate('/tasks')}
            >
              <CardContent sx={{ textAlign: 'center', py: 3 }}>
                <TaskIcon color="secondary" sx={{ fontSize: 48, mb: 2 }} />
                <Typography variant="h6" gutterBottom>
                  Manage Tasks
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  Create, assign, and track tasks
                </Typography>
              </CardContent>
            </Card>
          </Grid>
          <Grid item xs={12} sm={6} md={3}>
            <Card
              sx={{
                cursor: 'pointer',
                '&:hover': {
                  boxShadow: 4,
                  transform: 'translateY(-2px)',
                  transition: 'all 0.2s ease-in-out'
                }
              }}
              onClick={() => navigate('/analytics')}
            >
              <CardContent sx={{ textAlign: 'center', py: 3 }}>
                <AnalyticsIcon color="success" sx={{ fontSize: 48, mb: 2 }} />
                <Typography variant="h6" gutterBottom>
                  View Analytics
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  Track performance and metrics
                </Typography>
              </CardContent>
            </Card>
          </Grid>
          <Grid item xs={12} sm={6} md={3}>
            <Card
              sx={{
                cursor: 'pointer',
                '&:hover': {
                  boxShadow: 4,
                  transform: 'translateY(-2px)',
                  transition: 'all 0.2s ease-in-out'
                }
              }}
              onClick={() => navigate('/profile')}
            >
              <CardContent sx={{ textAlign: 'center', py: 3 }}>
                <ProfileIcon color="info" sx={{ fontSize: 48, mb: 2 }} />
                <Typography variant="h6" gutterBottom>
                  My Profile
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  Manage your account settings
                </Typography>
              </CardContent>
            </Card>
          </Grid>
        </Grid>
      </Box>

      {/* Recent Activity */}
      <Box sx={{ mt: 4 }}>
        <Typography variant="h5" sx={{ mb: 3 }}>
          Recent Activity
        </Typography>
        <Card>
          <CardContent>
            <Typography variant="body2" color="text.secondary">
              No recent activity to display.
            </Typography>
          </CardContent>
        </Card>
      </Box>
    </Box>
  );
};

export default Dashboard; 
