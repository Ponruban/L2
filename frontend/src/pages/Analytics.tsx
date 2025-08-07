import React, { useEffect, useState } from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  Grid,
  Paper,
  CircularProgress,
  Alert,
} from '@mui/material';
import {
  TrendingUp,
  TrendingDown,
  People,
  Assignment,
  Schedule,
  CheckCircle,
} from '@mui/icons-material';
import { useAuthContext } from '@/contexts/AuthContext';
import { useProjects } from '@/hooks/useProjects';
import { useTasks } from '@/hooks/useTasks';
import { useUsers } from '@/hooks/useUsers';
import { analyticsService } from '@/services/analytics';
import type { AnalyticsData } from '@/services/analytics';

const Analytics: React.FC = () => {
  const { user } = useAuthContext();
  const { projects, totalProjects, isLoading: isLoadingProjects } = useProjects();
  const { tasks, totalTasks, isLoading: isLoadingTasks } = useTasks();
  const { users, totalUsers, isLoading: isLoadingUsers } = useUsers();
  
  const [analyticsData, setAnalyticsData] = useState<AnalyticsData | null>(null);
  const [isLoadingAnalytics, setIsLoadingAnalytics] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Calculate real data from projects and tasks
  const realData = {
    totalProjects: totalProjects || 0,
    activeProjects: projects?.filter(p => p.status === 'ACTIVE').length || 0,
    completedProjects: projects?.filter(p => p.status === 'COMPLETED').length || 0,
    totalTasks: totalTasks || 0,
    completedTasks: tasks?.filter(t => t.status === 'COMPLETED').length || 0,
    pendingTasks: tasks?.filter(t => t.status === 'IN_PROGRESS' || t.status === 'TODO').length || 0,
    teamMembers: totalUsers || 0,
    averageCompletionTime: analyticsData?.performance?.averageCompletionTime || 0,
    productivityScore: analyticsData?.performance?.productivityScore || 0,
  };

  // Fetch analytics data
  useEffect(() => {
    const fetchAnalytics = async () => {
      if (!user) return;
      
      try {
        setIsLoadingAnalytics(true);
        setError(null);
        
        // Get current date and 30 days ago for date range
        const endDate = new Date().toISOString().split('T')[0];
        const startDate = new Date(Date.now() - 30 * 24 * 60 * 60 * 1000).toISOString().split('T')[0];
        
        const [performanceMetrics] = await Promise.all([
          analyticsService.getPerformanceMetrics(startDate, endDate),
        ]);
        
        setAnalyticsData({
          timeTracking: { labels: [], datasets: [] },
          taskCompletion: { labels: [], datasets: [] },
          performance: performanceMetrics.data || {
            totalHours: 0,
            completedTasks: 0,
            averageCompletionTime: 0,
            productivityScore: 0,
          },
        });
      } catch (err) {
        console.error('Error fetching analytics:', err);
        setError('Failed to load analytics data');
      } finally {
        setIsLoadingAnalytics(false);
      }
    };

    fetchAnalytics();
  }, [user]);

  if (!user) {
    return (
      <Box sx={{ p: 3 }}>
        <Typography variant="h4" sx={{ mb: 3 }}>
          Analytics
        </Typography>
        <Alert severity="error">User not found. Please log in again.</Alert>
      </Box>
    );
  }

  if (error) {
    return (
      <Box sx={{ p: 3 }}>
        <Typography variant="h4" sx={{ mb: 3 }}>
          Analytics Dashboard
        </Typography>
        <Alert severity="error" sx={{ mb: 3 }}>
          {error}
        </Alert>
      </Box>
    );
  }

  const isLoading = isLoadingProjects || isLoadingTasks || isLoadingUsers || isLoadingAnalytics;

  const StatCard = ({ title, value, icon, color, trend }: {
    title: string;
    value: string | number;
    icon: React.ReactNode;
    color: string;
    trend?: 'up' | 'down';
  }) => (
    <Card>
      <CardContent>
        <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
          <Box>
            <Typography color="textSecondary" gutterBottom variant="body2">
              {title}
            </Typography>
            <Typography variant="h4" component="div" sx={{ color }}>
              {isLoading ? <CircularProgress size={24} /> : value}
            </Typography>
          </Box>
          <Box sx={{ color }}>
            {icon}
          </Box>
        </Box>
        {trend && !isLoading && (
          <Box sx={{ display: 'flex', alignItems: 'center', mt: 1 }}>
            {trend === 'up' ? (
              <TrendingUp sx={{ color: 'success.main', fontSize: 16, mr: 0.5 }} />
            ) : (
              <TrendingDown sx={{ color: 'error.main', fontSize: 16, mr: 0.5 }} />
            )}
            <Typography variant="body2" color="textSecondary">
              {trend === 'up' ? '+12%' : '-5%'} from last month
            </Typography>
          </Box>
        )}
      </CardContent>
    </Card>
  );

  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h4" sx={{ mb: 3 }}>
        Analytics Dashboard
      </Typography>
      <Typography variant="body1" color="text.secondary" sx={{ mb: 3 }}>
        Track your team's performance and productivity metrics
      </Typography>

      {/* Key Metrics */}
      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid item xs={12} sm={6} md={3}>
          <StatCard
            title="Total Projects"
            value={realData.totalProjects}
            icon={<Assignment sx={{ fontSize: 40 }} />}
            color="primary.main"
            trend="up"
          />
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <StatCard
            title="Active Tasks"
            value={realData.pendingTasks}
            icon={<Schedule sx={{ fontSize: 40 }} />}
            color="warning.main"
            trend="down"
          />
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <StatCard
            title="Completed Tasks"
            value={realData.completedTasks}
            icon={<CheckCircle sx={{ fontSize: 40 }} />}
            color="success.main"
            trend="up"
          />
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <StatCard
            title="Team Members"
            value={realData.teamMembers}
            icon={<People sx={{ fontSize: 40 }} />}
            color="info.main"
          />
        </Grid>
      </Grid>

      {/* Project Status */}
      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Project Status Overview
              </Typography>
              <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <Typography>Active Projects</Typography>
                  <Typography variant="h6" color="primary.main">
                    {isLoading ? <CircularProgress size={20} /> : realData.activeProjects}
                  </Typography>
                </Box>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <Typography>Completed Projects</Typography>
                  <Typography variant="h6" color="success.main">
                    {isLoading ? <CircularProgress size={20} /> : realData.completedProjects}
                  </Typography>
                </Box>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <Typography>Total Projects</Typography>
                  <Typography variant="h6">
                    {isLoading ? <CircularProgress size={20} /> : realData.totalProjects}
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Performance Metrics
              </Typography>
              <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <Typography>Productivity Score</Typography>
                  <Typography variant="h6" color="success.main">
                    {isLoading ? <CircularProgress size={20} /> : `${realData.productivityScore}%`}
                  </Typography>
                </Box>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <Typography>Avg. Completion Time</Typography>
                  <Typography variant="h6" color="info.main">
                    {isLoading ? <CircularProgress size={20} /> : `${realData.averageCompletionTime} days`}
                  </Typography>
                </Box>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <Typography>Task Completion Rate</Typography>
                  <Typography variant="h6" color="primary.main">
                    {isLoading ? <CircularProgress size={20} /> : 
                      realData.totalTasks > 0 ? `${Math.round((realData.completedTasks / realData.totalTasks) * 100)}%` : '0%'}
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Recent Activity */}
      <Card>
        <CardContent>
          <Typography variant="h6" gutterBottom>
            Recent Activity
          </Typography>
          <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
            {isLoading ? (
              <Box sx={{ display: 'flex', justifyContent: 'center', p: 2 }}>
                <CircularProgress />
              </Box>
            ) : (
              <>
                {projects?.slice(0, 3).map((project) => (
                  <Box key={project.id} sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                    <Assignment color="primary" />
                    <Typography variant="body2">
                      Project "{project.name}" - {project.status}
                    </Typography>
                  </Box>
                ))}
                {tasks?.slice(0, 2).map((task) => (
                  <Box key={task.id} sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                    <CheckCircle color="success" />
                    <Typography variant="body2">
                      Task "{task.title}" - {task.status}
                    </Typography>
                  </Box>
                ))}
              </>
            )}
          </Box>
        </CardContent>
      </Card>
    </Box>
  );
};

export default Analytics; 