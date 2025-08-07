import React from 'react';
import { Box, Card, CardContent, Grid, Chip } from '@mui/material';
import { Skeleton } from '@/components/ui/Skeleton';

interface TaskSkeletonProps {
  count?: number;
  variant?: 'card' | 'list' | 'kanban';
  columns?: number;
}

export const TaskSkeleton: React.FC<TaskSkeletonProps> = ({
  count = 5,
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
                {/* Task Priority/Status */}
                <Skeleton variant="circular" width={12} height={12} />
                
                <Box sx={{ flex: 1 }}>
                  {/* Task Title */}
                  <Skeleton variant="text" width="70%" height={20} sx={{ mb: 1 }} />
                  
                  {/* Task Description */}
                  <Skeleton variant="text" width="90%" height={16} sx={{ mb: 1 }} />
                  <Skeleton variant="text" width="60%" height={16} sx={{ mb: 2 }} />
                  
                  {/* Task Meta */}
                  <Box sx={{ display: 'flex', gap: 2, mb: 2, flexWrap: 'wrap' }}>
                    <Skeleton variant="rectangular" width={60} height={24} sx={{ borderRadius: 1 }} />
                    <Skeleton variant="rectangular" width={80} height={24} sx={{ borderRadius: 1 }} />
                    <Skeleton variant="rectangular" width={70} height={24} sx={{ borderRadius: 1 }} />
                  </Box>
                  
                  {/* Task Footer */}
                  <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <Box sx={{ display: 'flex', gap: 1, alignItems: 'center' }}>
                      <Skeleton variant="circular" width={24} height={24} />
                      <Skeleton variant="text" width={40} height={14} />
                    </Box>
                    <Box sx={{ display: 'flex', gap: 1 }}>
                      <Skeleton variant="rectangular" width={60} height={28} />
                      <Skeleton variant="rectangular" width={60} height={28} />
                    </Box>
                  </Box>
                </Box>
              </Box>
            </CardContent>
          </Card>
        ))}
      </Box>
    );
  }

  if (variant === 'kanban') {
    return (
      <Grid container spacing={2}>
        {Array.from({ length: columns }).map((_, columnIndex) => (
          <Grid item xs={12} sm={6} md={12 / columns} key={columnIndex}>
            <Card>
              <CardContent>
                {/* Column Header */}
                <Box sx={{ mb: 2 }}>
                  <Skeleton variant="text" width="60%" height={24} sx={{ mb: 1 }} />
                  <Skeleton variant="text" width="30%" height={16} />
                </Box>
                
                {/* Tasks in Column */}
                <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                  {Array.from({ length: Math.ceil(count / columns) }).map((_, taskIndex) => (
                    <Card key={taskIndex} variant="outlined" sx={{ p: 2 }}>
                      <Skeleton variant="text" width="80%" height={18} sx={{ mb: 1 }} />
                      <Skeleton variant="text" width="60%" height={14} sx={{ mb: 1 }} />
                      
                      <Box sx={{ display: 'flex', gap: 1, mb: 1 }}>
                        <Skeleton variant="rectangular" width={50} height={20} sx={{ borderRadius: 1 }} />
                        <Skeleton variant="rectangular" width={60} height={20} sx={{ borderRadius: 1 }} />
                      </Box>
                      
                      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                        <Skeleton variant="circular" width={20} height={20} />
                        <Skeleton variant="text" width={30} height={12} />
                      </Box>
                    </Card>
                  ))}
                </Box>
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>
    );
  }

  // Default card variant
  return (
    <Grid container spacing={2}>
      {Array.from({ length: count }).map((_, index) => (
        <Grid item xs={12} sm={6} md={4} key={index}>
          <Card sx={{ height: 200 }}>
            <CardContent>
              {/* Task Header */}
              <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
                <Skeleton variant="text" width="60%" height={20} />
                <Skeleton variant="circular" width={16} height={16} />
              </Box>
              
              {/* Task Description */}
              <Skeleton variant="text" width="100%" height={16} sx={{ mb: 1 }} />
              <Skeleton variant="text" width="80%" height={16} sx={{ mb: 2 }} />
              
              {/* Task Tags */}
              <Box sx={{ display: 'flex', gap: 1, mb: 2, flexWrap: 'wrap' }}>
                <Skeleton variant="rectangular" width={50} height={20} sx={{ borderRadius: 1 }} />
                <Skeleton variant="rectangular" width={60} height={20} sx={{ borderRadius: 1 }} />
              </Box>
              
              {/* Task Progress */}
              <Box sx={{ mb: 2 }}>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                  <Skeleton variant="text" width="20%" height={12} />
                  <Skeleton variant="text" width="10%" height={12} />
                </Box>
                <Skeleton variant="rectangular" width="100%" height={4} />
              </Box>
              
              {/* Task Footer */}
              <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <Box sx={{ display: 'flex', gap: 1, alignItems: 'center' }}>
                  <Skeleton variant="circular" width={20} height={20} />
                  <Skeleton variant="text" width={30} height={12} />
                </Box>
                <Skeleton variant="text" width={40} height={12} />
              </Box>
            </CardContent>
          </Card>
        </Grid>
      ))}
    </Grid>
  );
};

export const TaskDetailSkeleton: React.FC = () => {
  return (
    <Box>
      {/* Task Header */}
      <Box sx={{ mb: 3 }}>
        <Skeleton variant="text" width="70%" height={32} sx={{ mb: 1 }} />
        <Skeleton variant="text" width="90%" height={20} sx={{ mb: 2 }} />
        
        <Box sx={{ display: 'flex', gap: 2, mb: 2, flexWrap: 'wrap' }}>
          <Skeleton variant="rectangular" width={80} height={28} sx={{ borderRadius: 1 }} />
          <Skeleton variant="rectangular" width={100} height={28} sx={{ borderRadius: 1 }} />
          <Skeleton variant="rectangular" width={90} height={28} sx={{ borderRadius: 1 }} />
        </Box>
      </Box>
      
      {/* Task Content */}
      <Grid container spacing={3}>
        <Grid item xs={12} md={8}>
          {/* Description */}
          <Card sx={{ mb: 3 }}>
            <CardContent>
              <Skeleton variant="text" width="30%" height={24} sx={{ mb: 2 }} />
              <Skeleton variant="text" width="100%" height={16} sx={{ mb: 1 }} />
              <Skeleton variant="text" width="95%" height={16} sx={{ mb: 1 }} />
              <Skeleton variant="text" width="80%" height={16} sx={{ mb: 1 }} />
              <Skeleton variant="text" width="70%" height={16} />
            </CardContent>
          </Card>
          
          {/* Comments */}
          <Card sx={{ mb: 3 }}>
            <CardContent>
              <Skeleton variant="text" width="40%" height={24} sx={{ mb: 2 }} />
              
              {Array.from({ length: 3 }).map((_, index) => (
                <Box key={index} sx={{ mb: 2, pb: 2, borderBottom: index < 2 ? 1 : 0, borderColor: 'divider' }}>
                  <Box sx={{ display: 'flex', gap: 2, mb: 1 }}>
                    <Skeleton variant="circular" width={32} height={32} />
                    <Box sx={{ flex: 1 }}>
                      <Skeleton variant="text" width="40%" height={16} sx={{ mb: 0.5 }} />
                      <Skeleton variant="text" width="20%" height={12} />
                    </Box>
                  </Box>
                  <Skeleton variant="text" width="100%" height={16} sx={{ mb: 0.5 }} />
                  <Skeleton variant="text" width="80%" height={16} />
                </Box>
              ))}
              
              <Box sx={{ display: 'flex', gap: 1 }}>
                <Skeleton variant="rectangular" width="100%" height={40} sx={{ flex: 1 }} />
                <Skeleton variant="rectangular" width={80} height={40} />
              </Box>
            </CardContent>
          </Card>
          
          {/* Attachments */}
          <Card>
            <CardContent>
              <Skeleton variant="text" width="35%" height={24} sx={{ mb: 2 }} />
              
              {Array.from({ length: 2 }).map((_, index) => (
                <Box key={index} sx={{ display: 'flex', gap: 2, mb: 2, alignItems: 'center' }}>
                  <Skeleton variant="rectangular" width={40} height={40} />
                  <Box sx={{ flex: 1 }}>
                    <Skeleton variant="text" width="60%" height={16} />
                    <Skeleton variant="text" width="40%" height={12} />
                  </Box>
                  <Skeleton variant="rectangular" width={60} height={28} />
                </Box>
              ))}
            </CardContent>
          </Card>
        </Grid>
        
        <Grid item xs={12} md={4}>
          {/* Task Info */}
          <Card sx={{ mb: 3 }}>
            <CardContent>
              <Skeleton variant="text" width="50%" height={24} sx={{ mb: 2 }} />
              
              <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                <Box>
                  <Skeleton variant="text" width="30%" height={14} sx={{ mb: 0.5 }} />
                  <Skeleton variant="text" width="60%" height={16} />
                </Box>
                <Box>
                  <Skeleton variant="text" width="30%" height={14} sx={{ mb: 0.5 }} />
                  <Skeleton variant="text" width="70%" height={16} />
                </Box>
                <Box>
                  <Skeleton variant="text" width="30%" height={14} sx={{ mb: 0.5 }} />
                  <Skeleton variant="text" width="50%" height={16} />
                </Box>
                <Box>
                  <Skeleton variant="text" width="30%" height={14} sx={{ mb: 0.5 }} />
                  <Skeleton variant="text" width="40%" height={16} />
                </Box>
              </Box>
            </CardContent>
          </Card>
          
          {/* Time Tracking */}
          <Card>
            <CardContent>
              <Skeleton variant="text" width="60%" height={24} sx={{ mb: 2 }} />
              
              <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                <Box>
                  <Skeleton variant="text" width="40%" height={16} />
                  <Skeleton variant="text" width="30%" height={14} />
                </Box>
                <Box>
                  <Skeleton variant="text" width="50%" height={16} />
                  <Skeleton variant="text" width="25%" height={14} />
                </Box>
              </Box>
              
              <Box sx={{ mt: 2 }}>
                <Skeleton variant="rectangular" width="100%" height={40} />
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
};

export const TaskBoardSkeleton: React.FC = () => {
  return (
    <Box>
      {/* Board Header */}
      <Box sx={{ mb: 3 }}>
        <Skeleton variant="text" width="40%" height={32} sx={{ mb: 1 }} />
        <Skeleton variant="text" width="60%" height={20} sx={{ mb: 2 }} />
        
        <Box sx={{ display: 'flex', gap: 2, mb: 2 }}>
          <Skeleton variant="rectangular" width={120} height={36} />
          <Skeleton variant="rectangular" width={100} height={36} />
          <Skeleton variant="rectangular" width={80} height={36} />
        </Box>
      </Box>
      
      {/* Board Columns */}
      <Box sx={{ display: 'flex', gap: 2, overflowX: 'auto', pb: 2 }}>
        {Array.from({ length: 4 }).map((_, columnIndex) => (
          <Box
            key={columnIndex}
            sx={{
              minWidth: 300,
              flex: 1,
              backgroundColor: 'background.paper',
              borderRadius: 1,
              p: 2,
              border: 1,
              borderColor: 'divider',
            }}
          >
            {/* Column Header */}
            <Box sx={{ mb: 2 }}>
              <Skeleton variant="text" width="60%" height={24} sx={{ mb: 1 }} />
              <Skeleton variant="text" width="30%" height={16} />
            </Box>
            
            {/* Column Tasks */}
            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
              {Array.from({ length: 3 }).map((_, taskIndex) => (
                <Card key={taskIndex} variant="outlined" sx={{ p: 2 }}>
                  <Skeleton variant="text" width="80%" height={18} sx={{ mb: 1 }} />
                  <Skeleton variant="text" width="60%" height={14} sx={{ mb: 1 }} />
                  
                  <Box sx={{ display: 'flex', gap: 1, mb: 1 }}>
                    <Skeleton variant="rectangular" width={50} height={20} sx={{ borderRadius: 1 }} />
                    <Skeleton variant="rectangular" width={60} height={20} sx={{ borderRadius: 1 }} />
                  </Box>
                  
                  <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <Skeleton variant="circular" width={20} height={20} />
                    <Skeleton variant="text" width={30} height={12} />
                  </Box>
                </Card>
              ))}
            </Box>
          </Box>
        ))}
      </Box>
    </Box>
  );
};

export default TaskSkeleton; 