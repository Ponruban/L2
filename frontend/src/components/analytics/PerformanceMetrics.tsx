import React from 'react';
import {
  Box,
  Typography,
  Grid,
  Card,
  CardContent,
  LinearProgress,
  Chip,
} from '@mui/material';
import {
  TrendingUp as TrendingUpIcon,
  TrendingDown as TrendingDownIcon,
  Remove as RemoveIcon,
} from '@mui/icons-material';

interface PerformanceData {
  totalHours: number;
  completedTasks: number;
  averageCompletionTime: number;
  productivityScore: number;
}

interface PerformanceMetricsProps {
  data: PerformanceData;
}

const PerformanceMetrics: React.FC<PerformanceMetricsProps> = ({ data }) => {
  // Mock trend data - in real app, this would come from API
  const trends = {
    hours: { value: 12.5, trend: 'up' as const },
    tasks: { value: 8, trend: 'up' as const },
    time: { value: 0.3, trend: 'down' as const },
    productivity: { value: 5, trend: 'up' as const },
  };

  const getTrendIcon = (trend: 'up' | 'down' | 'stable') => {
    switch (trend) {
      case 'up':
        return <TrendingUpIcon color="success" fontSize="small" />;
      case 'down':
        return <TrendingDownIcon color="error" fontSize="small" />;
      default:
        return <RemoveIcon color="action" fontSize="small" />;
    }
  };

  const getTrendColor = (trend: 'up' | 'down' | 'stable') => {
    switch (trend) {
      case 'up':
        return 'success';
      case 'down':
        return 'error';
      default:
        return 'default';
    }
  };

  const metrics = [
    {
      title: 'Hours vs Last Period',
      value: `${trends.hours.value > 0 ? '+' : ''}${trends.hours.value}h`,
      trend: trends.hours.trend,
      description: 'Change in total hours logged',
    },
    {
      title: 'Tasks Completed',
      value: `${trends.tasks.value > 0 ? '+' : ''}${trends.tasks.value}`,
      trend: trends.tasks.trend,
      description: 'Change in completed tasks',
    },
    {
      title: 'Avg. Completion Time',
      value: `${trends.time.value > 0 ? '+' : ''}${trends.time.value}d`,
      trend: trends.time.trend,
      description: 'Change in average completion time',
    },
    {
      title: 'Productivity Score',
      value: `${trends.productivity.value > 0 ? '+' : ''}${trends.productivity.value}%`,
      trend: trends.productivity.trend,
      description: 'Change in productivity score',
    },
  ];

  return (
    <Box>
      {/* Summary Cards */}
      <Grid container spacing={2} sx={{ mb: 3 }}>
        {metrics.map((metric, index) => (
          <Grid item xs={12} sm={6} md={3} key={index}>
            <Card>
              <CardContent>
                <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                  <Typography variant="body2" color="text.secondary" sx={{ flexGrow: 1 }}>
                    {metric.title}
                  </Typography>
                  {getTrendIcon(metric.trend)}
                </Box>
                <Typography variant="h6" component="div" color={`${getTrendColor(metric.trend)}.main`}>
                  {metric.value}
                </Typography>
                <Typography variant="caption" color="text.secondary">
                  {metric.description}
                </Typography>
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>

      {/* Detailed Metrics */}
      <Grid container spacing={3}>
        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Productivity Breakdown
              </Typography>
              
              <Box sx={{ mb: 3 }}>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                  <Typography variant="body2">Overall Score</Typography>
                  <Typography variant="body2" fontWeight="bold">
                    {data.productivityScore}%
                  </Typography>
                </Box>
                <LinearProgress
                  variant="determinate"
                  value={data.productivityScore}
                  sx={{ height: 8, borderRadius: 4 }}
                  color={data.productivityScore >= 80 ? 'success' : data.productivityScore >= 60 ? 'warning' : 'error'}
                />
              </Box>

              <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1 }}>
                <Chip
                  label={`${data.completedTasks} Tasks`}
                  size="small"
                  color="success"
                  variant="outlined"
                />
                <Chip
                  label={`${data.totalHours}h Logged`}
                  size="small"
                  color="primary"
                  variant="outlined"
                />
                <Chip
                  label={`${data.averageCompletionTime}d Avg`}
                  size="small"
                  color="info"
                  variant="outlined"
                />
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Performance Insights
              </Typography>
              
              <Box sx={{ space: 2 }}>
                <Box sx={{ mb: 2 }}>
                  <Typography variant="body2" color="text.secondary" gutterBottom>
                    Task Completion Rate
                  </Typography>
                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                    <LinearProgress
                      variant="determinate"
                      value={75}
                      sx={{ flexGrow: 1, height: 6, borderRadius: 3 }}
                      color="success"
                    />
                    <Typography variant="body2" fontWeight="bold">
                      75%
                    </Typography>
                  </Box>
                </Box>

                <Box sx={{ mb: 2 }}>
                  <Typography variant="body2" color="text.secondary" gutterBottom>
                    Time Efficiency
                  </Typography>
                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                    <LinearProgress
                      variant="determinate"
                      value={data.productivityScore}
                      sx={{ flexGrow: 1, height: 6, borderRadius: 3 }}
                      color="primary"
                    />
                    <Typography variant="body2" fontWeight="bold">
                      {data.productivityScore}%
                    </Typography>
                  </Box>
                </Box>

                <Box sx={{ mb: 2 }}>
                  <Typography variant="body2" color="text.secondary" gutterBottom>
                    Team Collaboration
                  </Typography>
                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                    <LinearProgress
                      variant="determinate"
                      value={90}
                      sx={{ flexGrow: 1, height: 6, borderRadius: 3 }}
                      color="info"
                    />
                    <Typography variant="body2" fontWeight="bold">
                      90%
                    </Typography>
                  </Box>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Recommendations */}
      <Box sx={{ mt: 3 }}>
        <Card>
          <CardContent>
            <Typography variant="h6" gutterBottom>
              Recommendations
            </Typography>
            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1 }}>
              <Typography variant="body2" color="text.secondary">
                • Focus on completing high-priority tasks to improve productivity score
              </Typography>
              <Typography variant="body2" color="text.secondary">
                • Consider time management training to reduce average completion time
              </Typography>
              <Typography variant="body2" color="text.secondary">
                • Maintain consistent daily hours to improve overall performance
              </Typography>
            </Box>
          </CardContent>
        </Card>
      </Box>
    </Box>
  );
};

export default PerformanceMetrics; 
