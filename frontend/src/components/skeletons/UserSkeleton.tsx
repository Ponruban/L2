import React from 'react';
import { Box, Card, CardContent, Grid, Avatar } from '@mui/material';
import { Skeleton } from '@/components/ui/Skeleton';

interface UserSkeletonProps {
  count?: number;
  variant?: 'card' | 'list' | 'table';
  showActions?: boolean;
}

export const UserSkeleton: React.FC<UserSkeletonProps> = ({
  count = 5,
  variant = 'card',
  showActions = true,
}) => {
  if (variant === 'list') {
    return (
      <Box>
        {Array.from({ length: count }).map((_, index) => (
          <Card key={index} sx={{ mb: 2 }}>
            <CardContent>
              <Box sx={{ display: 'flex', gap: 2, alignItems: 'center' }}>
                {/* User Avatar */}
                <Skeleton variant="circular" width={48} height={48} />
                
                <Box sx={{ flex: 1 }}>
                  {/* User Name */}
                  <Skeleton variant="text" width="60%" height={20} sx={{ mb: 1 }} />
                  
                  {/* User Email */}
                  <Skeleton variant="text" width="80%" height={16} sx={{ mb: 1 }} />
                  
                  {/* User Role */}
                  <Skeleton variant="text" width="40%" height={16} sx={{ mb: 2 }} />
                  
                  {/* User Status */}
                  <Box sx={{ display: 'flex', gap: 2, alignItems: 'center' }}>
                    <Skeleton variant="circular" width={8} height={8} />
                    <Skeleton variant="text" width={60} height={14} />
                    <Skeleton variant="text" width={80} height={14} />
                  </Box>
                </Box>
                
                {showActions && (
                  <Box sx={{ display: 'flex', gap: 1 }}>
                    <Skeleton variant="rectangular" width={60} height={32} />
                    <Skeleton variant="rectangular" width={60} height={32} />
                  </Box>
                )}
              </Box>
            </CardContent>
          </Card>
        ))}
      </Box>
    );
  }

  if (variant === 'table') {
    return (
      <Box>
        {/* Table Header */}
        <Box sx={{ display: 'flex', p: 2, borderBottom: 1, borderColor: 'divider' }}>
          <Box sx={{ flex: 1, mr: 2 }}>
            <Skeleton variant="text" width="80%" height={24} />
          </Box>
          <Box sx={{ flex: 1, mr: 2 }}>
            <Skeleton variant="text" width="80%" height={24} />
          </Box>
          <Box sx={{ flex: 1, mr: 2 }}>
            <Skeleton variant="text" width="80%" height={24} />
          </Box>
          <Box sx={{ flex: 1, mr: 2 }}>
            <Skeleton variant="text" width="80%" height={24} />
          </Box>
          {showActions && (
            <Box sx={{ width: 120 }}>
              <Skeleton variant="text" width="80%" height={24} />
            </Box>
          )}
        </Box>
        
        {/* Table Rows */}
        {Array.from({ length: count }).map((_, index) => (
          <Box
            key={index}
            sx={{
              display: 'flex',
              p: 2,
              borderBottom: index < count - 1 ? 1 : 0,
              borderColor: 'divider',
              alignItems: 'center',
            }}
          >
            <Box sx={{ flex: 1, mr: 2, display: 'flex', alignItems: 'center', gap: 2 }}>
              <Skeleton variant="circular" width={32} height={32} />
              <Skeleton variant="text" width="70%" height={16} />
            </Box>
            <Box sx={{ flex: 1, mr: 2 }}>
              <Skeleton variant="text" width="80%" height={16} />
            </Box>
            <Box sx={{ flex: 1, mr: 2 }}>
              <Skeleton variant="text" width="60%" height={16} />
            </Box>
            <Box sx={{ flex: 1, mr: 2 }}>
              <Skeleton variant="text" width="40%" height={16} />
            </Box>
            {showActions && (
              <Box sx={{ width: 120, display: 'flex', gap: 1 }}>
                <Skeleton variant="rectangular" width={50} height={28} />
                <Skeleton variant="rectangular" width={50} height={28} />
              </Box>
            )}
          </Box>
        ))}
      </Box>
    );
  }

  // Default card variant
  return (
    <Grid container spacing={2}>
      {Array.from({ length: count }).map((_, index) => (
        <Grid item xs={12} sm={6} md={4} key={index}>
          <Card sx={{ height: 200 }}>
            <CardContent>
              {/* User Header */}
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 2 }}>
                <Skeleton variant="circular" width={48} height={48} />
                <Box sx={{ flex: 1 }}>
                  <Skeleton variant="text" width="70%" height={20} />
                  <Skeleton variant="text" width="50%" height={16} />
                </Box>
              </Box>
              
              {/* User Info */}
              <Box sx={{ mb: 2 }}>
                <Skeleton variant="text" width="100%" height={16} sx={{ mb: 1 }} />
                <Skeleton variant="text" width="80%" height={16} sx={{ mb: 1 }} />
                <Skeleton variant="text" width="60%" height={16} />
              </Box>
              
              {/* User Status */}
              <Box sx={{ display: 'flex', gap: 1, mb: 2 }}>
                <Skeleton variant="rectangular" width={60} height={20} sx={{ borderRadius: 1 }} />
                <Skeleton variant="rectangular" width={80} height={20} sx={{ borderRadius: 1 }} />
              </Box>
              
              {/* Actions */}
              {showActions && (
                <Box sx={{ display: 'flex', gap: 1 }}>
                  <Skeleton variant="rectangular" width={60} height={28} />
                  <Skeleton variant="rectangular" width={60} height={28} />
                </Box>
              )}
            </CardContent>
          </Card>
        </Grid>
      ))}
    </Grid>
  );
};

export const UserProfileSkeleton: React.FC = () => {
  return (
    <Box>
      {/* Profile Header */}
      <Box sx={{ mb: 3 }}>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 3, mb: 2 }}>
          <Skeleton variant="circular" width={80} height={80} />
          <Box sx={{ flex: 1 }}>
            <Skeleton variant="text" width="60%" height={32} sx={{ mb: 1 }} />
            <Skeleton variant="text" width="40%" height={20} sx={{ mb: 1 }} />
            <Skeleton variant="text" width="50%" height={16} />
          </Box>
        </Box>
        
        <Box sx={{ display: 'flex', gap: 2, flexWrap: 'wrap' }}>
          <Skeleton variant="rectangular" width={100} height={28} sx={{ borderRadius: 1 }} />
          <Skeleton variant="rectangular" width={120} height={28} sx={{ borderRadius: 1 }} />
          <Skeleton variant="rectangular" width={90} height={28} sx={{ borderRadius: 1 }} />
        </Box>
      </Box>
      
      {/* Profile Content */}
      <Grid container spacing={3}>
        <Grid item xs={12} md={8}>
          {/* Personal Information */}
          <Card sx={{ mb: 3 }}>
            <CardContent>
              <Skeleton variant="text" width="40%" height={24} sx={{ mb: 2 }} />
              
              <Grid container spacing={2}>
                <Grid item xs={12} sm={6}>
                  <Skeleton variant="text" width="30%" height={16} sx={{ mb: 1 }} />
                  <Skeleton variant="text" width="80%" height={20} />
                </Grid>
                <Grid item xs={12} sm={6}>
                  <Skeleton variant="text" width="30%" height={16} sx={{ mb: 1 }} />
                  <Skeleton variant="text" width="80%" height={20} />
                </Grid>
                <Grid item xs={12}>
                  <Skeleton variant="text" width="30%" height={16} sx={{ mb: 1 }} />
                  <Skeleton variant="text" width="60%" height={20} />
                </Grid>
              </Grid>
            </CardContent>
          </Card>
          
          {/* Recent Activity */}
          <Card>
            <CardContent>
              <Skeleton variant="text" width="50%" height={24} sx={{ mb: 2 }} />
              
              {Array.from({ length: 3 }).map((_, index) => (
                <Box key={index} sx={{ mb: 2, pb: 2, borderBottom: index < 2 ? 1 : 0, borderColor: 'divider' }}>
                  <Box sx={{ display: 'flex', gap: 2, mb: 1 }}>
                    <Skeleton variant="circular" width={32} height={32} />
                    <Box sx={{ flex: 1 }}>
                      <Skeleton variant="text" width="60%" height={16} sx={{ mb: 0.5 }} />
                      <Skeleton variant="text" width="40%" height={12} />
                    </Box>
                  </Box>
                  <Skeleton variant="text" width="100%" height={16} sx={{ mb: 0.5 }} />
                  <Skeleton variant="text" width="70%" height={16} />
                </Box>
              ))}
            </CardContent>
          </Card>
        </Grid>
        
        <Grid item xs={12} md={4}>
          {/* Statistics */}
          <Card sx={{ mb: 3 }}>
            <CardContent>
              <Skeleton variant="text" width="50%" height={24} sx={{ mb: 2 }} />
              
              <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                <Box>
                  <Skeleton variant="text" width="40%" height={16} />
                  <Skeleton variant="text" width="60%" height={20} />
                </Box>
                <Box>
                  <Skeleton variant="text" width="40%" height={16} />
                  <Skeleton variant="text" width="60%" height={20} />
                </Box>
                <Box>
                  <Skeleton variant="text" width="40%" height={16} />
                  <Skeleton variant="text" width="60%" height={20} />
                </Box>
              </Box>
            </CardContent>
          </Card>
          
          {/* Skills */}
          <Card>
            <CardContent>
              <Skeleton variant="text" width="40%" height={24} sx={{ mb: 2 }} />
              
              <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1 }}>
                {Array.from({ length: 6 }).map((_, index) => (
                  <Skeleton
                    key={index}
                    variant="rectangular"
                    width={60 + Math.random() * 40}
                    height={24}
                    sx={{ borderRadius: 1 }}
                  />
                ))}
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
};

export const UserManagementSkeleton: React.FC = () => {
  return (
    <Box>
      {/* Header */}
      <Box sx={{ mb: 3 }}>
        <Skeleton variant="text" width="40%" height={32} sx={{ mb: 1 }} />
        <Skeleton variant="text" width="60%" height={20} sx={{ mb: 2 }} />
        
        <Box sx={{ display: 'flex', gap: 2, mb: 2 }}>
          <Skeleton variant="rectangular" width={200} height={40} />
          <Skeleton variant="rectangular" width={120} height={40} />
          <Skeleton variant="rectangular" width={100} height={40} />
        </Box>
      </Box>
      
      {/* Filters */}
      <Card sx={{ mb: 3 }}>
        <CardContent>
          <Box sx={{ display: 'flex', gap: 2, flexWrap: 'wrap' }}>
            <Skeleton variant="rectangular" width={150} height={40} />
            <Skeleton variant="rectangular" width={120} height={40} />
            <Skeleton variant="rectangular" width={100} height={40} />
            <Skeleton variant="rectangular" width={80} height={40} />
          </Box>
        </CardContent>
      </Card>
      
      {/* User Table */}
      <Card>
        <CardContent sx={{ p: 0 }}>
          <UserSkeleton count={8} variant="table" showActions={true} />
        </CardContent>
      </Card>
    </Box>
  );
};

export default UserSkeleton; 