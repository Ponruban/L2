import React from 'react';
import { Box, Card, CardContent, Grid } from '@mui/material';
import { Skeleton, SkeletonCard } from '@/components/ui/Skeleton';

interface ProjectSkeletonProps {
  count?: number;
  variant?: 'card' | 'list' | 'grid';
  columns?: number;
}

export const ProjectSkeleton: React.FC<ProjectSkeletonProps> = ({
  count = 3,
  variant = 'card',
  columns = 3,
}) => {
  if (variant === 'list') {
    return (
      <Box>
        {Array.from({ length: count }).map((_, index) => (
          <Card key={index} sx={{ mb: 2 }}>
            <CardContent>
              <Box sx={{ display: 'flex', gap: 2, alignItems: 'flex-start' }}>
                {/* Project Icon/Avatar */}
                <Skeleton variant="circular" width={48} height={48} />
                
                <Box sx={{ flex: 1 }}>
                  {/* Project Title */}
                  <Skeleton variant="text" width="60%" height={24} sx={{ mb: 1 }} />
                  
                  {/* Project Description */}
                  <Skeleton variant="text" width="80%" height={16} sx={{ mb: 1 }} />
                  <Skeleton variant="text" width="50%" height={16} sx={{ mb: 2 }} />
                  
                  {/* Project Stats */}
                  <Box sx={{ display: 'flex', gap: 3, mb: 2 }}>
                    <Box>
                      <Skeleton variant="text" width={40} height={20} />
                      <Skeleton variant="text" width={60} height={14} />
                    </Box>
                    <Box>
                      <Skeleton variant="text" width={40} height={20} />
                      <Skeleton variant="text" width={60} height={14} />
                    </Box>
                    <Box>
                      <Skeleton variant="text" width={40} height={20} />
                      <Skeleton variant="text" width={60} height={14} />
                    </Box>
                  </Box>
                  
                  {/* Progress Bar */}
                  <Box sx={{ mb: 2 }}>
                    <Skeleton variant="text" width="30%" height={16} sx={{ mb: 1 }} />
                    <Skeleton variant="rectangular" width="100%" height={8} />
                  </Box>
                  
                  {/* Actions */}
                  <Box sx={{ display: 'flex', gap: 1 }}>
                    <Skeleton variant="rectangular" width={80} height={32} />
                    <Skeleton variant="rectangular" width={80} height={32} />
                  </Box>
                </Box>
              </Box>
            </CardContent>
          </Card>
        ))}
      </Box>
    );
  }

  if (variant === 'grid') {
    return (
      <Grid container spacing={3}>
        {Array.from({ length: count }).map((_, index) => (
          <Grid item xs={12} sm={6} md={12 / columns} key={index}>
            <SkeletonCard
              title={true}
              content={3}
              actions={true}
              height={280}
            />
          </Grid>
        ))}
      </Grid>
    );
  }

  // Default card variant
  return (
    <Box>
      {Array.from({ length: count }).map((_, index) => (
        <Card key={index} sx={{ mb: 2, height: 200 }}>
          <CardContent>
            {/* Project Header */}
            <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
              <Skeleton variant="text" width="40%" height={28} />
              <Skeleton variant="circular" width={24} height={24} />
            </Box>
            
            {/* Project Description */}
            <Skeleton variant="text" width="100%" height={16} sx={{ mb: 1 }} />
            <Skeleton variant="text" width="80%" height={16} sx={{ mb: 2 }} />
            
            {/* Project Stats */}
            <Box sx={{ display: 'flex', gap: 2, mb: 2 }}>
              <Box sx={{ flex: 1 }}>
                <Skeleton variant="text" width="100%" height={20} />
                <Skeleton variant="text" width="60%" height={14} />
              </Box>
              <Box sx={{ flex: 1 }}>
                <Skeleton variant="text" width="100%" height={20} />
                <Skeleton variant="text" width="60%" height={14} />
              </Box>
            </Box>
            
            {/* Progress */}
            <Box sx={{ mb: 2 }}>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                <Skeleton variant="text" width="20%" height={14} />
                <Skeleton variant="text" width="10%" height={14} />
              </Box>
              <Skeleton variant="rectangular" width="100%" height={6} />
            </Box>
            
            {/* Actions */}
            <Box sx={{ display: 'flex', gap: 1 }}>
              <Skeleton variant="rectangular" width={60} height={28} />
              <Skeleton variant="rectangular" width={60} height={28} />
            </Box>
          </CardContent>
        </Card>
      ))}
    </Box>
  );
};

export const ProjectDetailSkeleton: React.FC = () => {
  return (
    <Box>
      {/* Project Header */}
      <Box sx={{ mb: 3 }}>
        <Skeleton variant="text" width="50%" height={32} sx={{ mb: 1 }} />
        <Skeleton variant="text" width="80%" height={20} sx={{ mb: 2 }} />
        
        <Box sx={{ display: 'flex', gap: 3, mb: 2 }}>
          <Skeleton variant="text" width={100} height={20} />
          <Skeleton variant="text" width={100} height={20} />
          <Skeleton variant="text" width={100} height={20} />
        </Box>
      </Box>
      
      {/* Project Stats */}
      <Grid container spacing={3} sx={{ mb: 3 }}>
        {Array.from({ length: 4 }).map((_, index) => (
          <Grid item xs={6} sm={3} key={index}>
            <Card>
              <CardContent>
                <Skeleton variant="text" width="60%" height={24} sx={{ mb: 1 }} />
                <Skeleton variant="text" width="40%" height={16} />
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>
      
      {/* Project Content */}
      <Grid container spacing={3}>
        <Grid item xs={12} md={8}>
          <Card sx={{ mb: 3 }}>
            <CardContent>
              <Skeleton variant="text" width="30%" height={24} sx={{ mb: 2 }} />
              <Skeleton variant="text" width="100%" height={16} sx={{ mb: 1 }} />
              <Skeleton variant="text" width="90%" height={16} sx={{ mb: 1 }} />
              <Skeleton variant="text" width="70%" height={16} />
            </CardContent>
          </Card>
          
          <Card>
            <CardContent>
              <Skeleton variant="text" width="40%" height={24} sx={{ mb: 2 }} />
              <Skeleton variant="rectangular" width="100%" height={200} />
            </CardContent>
          </Card>
        </Grid>
        
        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Skeleton variant="text" width="50%" height={24} sx={{ mb: 2 }} />
              <Box sx={{ mb: 2 }}>
                <Skeleton variant="text" width="100%" height={16} sx={{ mb: 1 }} />
                <Skeleton variant="text" width="80%" height={16} />
              </Box>
              <Skeleton variant="rectangular" width="100%" height={100} />
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
};

export default ProjectSkeleton; 