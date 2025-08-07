import React from 'react';
import {
  Box,
  Typography,
  Card,
  CardContent,
  Grid,
  Button,
  Alert,
  Avatar,
  Chip,
  Divider,
} from '@mui/material';
import {
  Edit as EditIcon,
  Email as EmailIcon,
  Business as BusinessIcon,
  CalendarToday as CalendarIcon,
  Lock as LockIcon,
} from '@mui/icons-material';
import { useAuthContext } from '@/contexts/AuthContext';

const Profile: React.FC = () => {
  const { user } = useAuthContext();

  const getRoleDisplayName = (role: string) => {
    const roleMap: Record<string, string> = {
      'ADMIN': 'Administrator',
      'PROJECT_MANAGER': 'Project Manager',
      'DEVELOPER': 'Developer',
      'TEAM_LEAD': 'Team Lead',
      'QA': 'QA Engineer',
    };
    return roleMap[role] || role;
  };

  const getRoleColor = (role: string) => {
    const colorMap: Record<string, 'primary' | 'secondary' | 'success' | 'warning' | 'error'> = {
      'ADMIN': 'error',
      'PROJECT_MANAGER': 'primary',
      'DEVELOPER': 'success',
      'TEAM_LEAD': 'warning',
      'QA': 'secondary',
    };
    return colorMap[role] || 'default';
  };

  if (!user) {
    return (
      <Box sx={{ p: 3 }}>
        <Typography variant="h4" sx={{ mb: 3 }}>
          Profile
        </Typography>
        <Alert severity="error">User not found. Please log in again.</Alert>
      </Box>
    );
  }

  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h4" sx={{ mb: 3 }}>
        Profile
      </Typography>

      <Grid container spacing={3}>
        {/* Profile Overview Card */}
        <Grid item xs={12} md={8}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
                <Avatar
                  sx={{
                    width: 80,
                    height: 80,
                    mr: 3,
                    bgcolor: 'primary.main',
                    fontSize: '2rem',
                  }}
                >
                  {user.firstName.charAt(0)}{user.lastName.charAt(0)}
                </Avatar>
                <Box>
                  <Typography variant="h5" gutterBottom>
                    {user.firstName} {user.lastName}
                  </Typography>
                  <Chip
                    label={getRoleDisplayName(user.role)}
                    color={getRoleColor(user.role)}
                    size="small"
                  />
                </Box>
              </Box>

              <Divider sx={{ my: 2 }} />

              <Grid container spacing={2}>
                <Grid item xs={12} sm={6}>
                  <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                    <EmailIcon sx={{ mr: 1, color: 'text.secondary' }} />
                    <Typography variant="body2" color="text.secondary">
                      Email
                    </Typography>
                  </Box>
                  <Typography variant="body1">{user.email}</Typography>
                </Grid>

                <Grid item xs={12} sm={6}>
                  <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                    <BusinessIcon sx={{ mr: 1, color: 'text.secondary' }} />
                    <Typography variant="body2" color="text.secondary">
                      Role
                    </Typography>
                  </Box>
                  <Typography variant="body1">{getRoleDisplayName(user.role)}</Typography>
                </Grid>

                <Grid item xs={12} sm={6}>
                  <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                    <CalendarIcon sx={{ mr: 1, color: 'text.secondary' }} />
                    <Typography variant="body2" color="text.secondary">
                      Member Since
                    </Typography>
                  </Box>
                  <Typography variant="body1">
                    {user.createdAt ? new Date(user.createdAt).toLocaleDateString() : 'N/A'}
                  </Typography>
                </Grid>

                <Grid item xs={12} sm={6}>
                  <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                    <LockIcon sx={{ mr: 1, color: 'text.secondary' }} />
                    <Typography variant="body2" color="text.secondary">
                      Status
                    </Typography>
                  </Box>
                  <Chip
                    label={user.isActive ? 'Active' : 'Inactive'}
                    color={user.isActive ? 'success' : 'error'}
                    size="small"
                  />
                </Grid>
              </Grid>
            </CardContent>
          </Card>
        </Grid>

        {/* Quick Actions Card */}
        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Quick Actions
              </Typography>
              <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                <Button
                  variant="outlined"
                  startIcon={<EditIcon />}
                  fullWidth
                  onClick={() => alert('Edit profile functionality coming soon!')}
                >
                  Edit Profile
                </Button>
                <Button
                  variant="outlined"
                  startIcon={<LockIcon />}
                  fullWidth
                  onClick={() => alert('Change password functionality coming soon!')}
                >
                  Change Password
                </Button>
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
};

export default Profile; 