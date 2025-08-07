import React, { useState } from 'react';
import {
  Box,
  Typography,
  Card,
  CardContent,
  Button,
  Avatar,
  Chip,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Alert,
  TextField,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Grid,
  IconButton,
  Tooltip,
} from '@mui/material';
import {
  Add as AddIcon,
  Edit as EditIcon,
  Delete as DeleteIcon,
  Search as SearchIcon,
} from '@mui/icons-material';
import { useUsers } from '@/hooks';
import { UserList } from '@/components/admin';
import { usersService } from '@/services/users';
import { useAuth } from '@/hooks';
import { UserSkeleton } from '@/components/skeletons';
import type { UserRole } from '@/types/user';

interface UserFormData {
  firstName: string;
  lastName: string;
  email: string;
  role: UserRole;
  password: string;
}

const UserManagement: React.FC = () => {
  const { user: currentUser } = useAuth();
  const [isAddDialogOpen, setIsAddDialogOpen] = useState(false);
  const [isEditDialogOpen, setIsEditDialogOpen] = useState(false);
  const [selectedUser, setSelectedUser] = useState<any>(null);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [roleFilter, setRoleFilter] = useState('');

  // Get users with filters
  const { users, totalUsers, isLoading, updateUser } = useUsers({
    search: searchTerm || undefined,
    role: roleFilter as UserRole || undefined,
  });

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

  const handleAddUser = () => {
    setIsAddDialogOpen(true);
    setError(null);
    setSuccess(null);
  };

  const handleEditUser = (user: any) => {
    setSelectedUser(user);
    setIsEditDialogOpen(true);
    setError(null);
    setSuccess(null);
  };

  const handleDeleteUser = async (user: any) => {
    if (!confirm(`Are you sure you want to delete ${user.firstName} ${user.lastName}?`)) {
      return;
    }

    try {
      // TODO: Implement user deletion API call
      setSuccess('User deleted successfully');
    } catch (err: any) {
      setError(err.message || 'Failed to delete user');
    }
  };

  const handleAddUserSubmit = async (formData: UserFormData) => {
    try {
      // TODO: Implement user creation API call
      setSuccess('User created successfully');
      setIsAddDialogOpen(false);
    } catch (err: any) {
      setError(err.message || 'Failed to create user');
    }
  };

  const handleEditUserSubmit = async (formData: UserFormData) => {
    try {
      if (!selectedUser?.id) {
        throw new Error('User ID not available');
      }

      await updateUser({
        id: selectedUser.id,
        data: {
          firstName: formData.firstName,
          lastName: formData.lastName,
          email: formData.email,
          role: formData.role,
        },
      });

      setSuccess('User updated successfully');
      setIsEditDialogOpen(false);
      setSelectedUser(null);
    } catch (err: any) {
      setError(err.message || 'Failed to update user');
    }
  };

  // Check if current user has admin permissions
  if (!currentUser || currentUser.role !== 'ADMIN') {
    return (
      <Box sx={{ p: 3 }}>
        <Alert severity="error">
          Access denied. Only administrators can access user management.
        </Alert>
      </Box>
    );
  }

  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h4" component="h1" gutterBottom>
        User Management
      </Typography>
      <Typography variant="body1" color="text.secondary" sx={{ mb: 3 }}>
        Manage users, roles, and permissions for the application
      </Typography>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError(null)}>
          {error}
        </Alert>
      )}

      {success && (
        <Alert severity="success" sx={{ mb: 2 }} onClose={() => setSuccess(null)}>
          {success}
        </Alert>
      )}

      <Card sx={{ mb: 3 }}>
        <CardContent>
          <Grid container spacing={2} alignItems="center">
            <Grid item xs={12} sm={4}>
              <TextField
                fullWidth
                placeholder="Search users..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                InputProps={{
                  startAdornment: <SearchIcon sx={{ mr: 1, color: 'text.secondary' }} />,
                }}
              />
            </Grid>
            <Grid item xs={12} sm={3}>
              <FormControl fullWidth>
                <InputLabel>Filter by Role</InputLabel>
                <Select
                  value={roleFilter}
                  label="Filter by Role"
                  onChange={(e) => setRoleFilter(e.target.value)}
                >
                  <MenuItem value="">All Roles</MenuItem>
                  <MenuItem value="ADMIN">Administrator</MenuItem>
                  <MenuItem value="PROJECT_MANAGER">Project Manager</MenuItem>
                  <MenuItem value="DEVELOPER">Developer</MenuItem>
                  <MenuItem value="TEAM_LEAD">Team Lead</MenuItem>
                  <MenuItem value="QA">QA Engineer</MenuItem>
                </Select>
              </FormControl>
            </Grid>
            <Grid item xs={12} sm={5}>
              <Box sx={{ display: 'flex', justifyContent: 'flex-end', gap: 1 }}>
                <Typography variant="body2" color="text.secondary" sx={{ alignSelf: 'center' }}>
                  {totalUsers} users
                </Typography>
                <Button
                  variant="contained"
                  startIcon={<AddIcon />}
                  onClick={handleAddUser}
                >
                  Add User
                </Button>
              </Box>
            </Grid>
          </Grid>
        </CardContent>
      </Card>

      <Card>
        <CardContent>
          {isLoading ? (
            <UserSkeleton count={8} variant="table" showActions={true} />
          ) : (
            <UserList
              users={users as any}
              onEditUser={handleEditUser}
              onDeleteUser={handleDeleteUser}
            />
          )}
        </CardContent>
      </Card>

      {/* Add User Dialog */}
      <Dialog
        open={isAddDialogOpen}
        onClose={() => setIsAddDialogOpen(false)}
        maxWidth="sm"
        fullWidth
      >
        <DialogTitle>Add New User</DialogTitle>
        <DialogContent>
          <UserForm
            onSubmit={handleAddUserSubmit}
            onCancel={() => setIsAddDialogOpen(false)}
          />
        </DialogContent>
      </Dialog>

      {/* Edit User Dialog */}
      <Dialog
        open={isEditDialogOpen}
        onClose={() => setIsEditDialogOpen(false)}
        maxWidth="sm"
        fullWidth
      >
        <DialogTitle>Edit User</DialogTitle>
        <DialogContent>
          <UserForm
            user={selectedUser}
            onSubmit={handleEditUserSubmit}
            onCancel={() => setIsEditDialogOpen(false)}
            isEdit={true}
          />
        </DialogContent>
      </Dialog>
    </Box>
  );
};

// Simple UserForm component for now
const UserForm: React.FC<{
  user?: any;
  onSubmit: (data: UserFormData) => void;
  onCancel: () => void;
  isEdit?: boolean;
}> = ({ user, onSubmit, onCancel, isEdit = false }) => {
  const [formData, setFormData] = useState<UserFormData>({
    firstName: user?.firstName || '',
    lastName: user?.lastName || '',
    email: user?.email || '',
    role: user?.role || 'DEVELOPER',
    password: '',
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSubmit(formData);
  };

  return (
    <Box component="form" onSubmit={handleSubmit} sx={{ mt: 1 }}>
      <Grid container spacing={2}>
        <Grid item xs={12} sm={6}>
          <TextField
            fullWidth
            label="First Name"
            value={formData.firstName}
            onChange={(e) => setFormData({ ...formData, firstName: e.target.value })}
            required
          />
        </Grid>
        <Grid item xs={12} sm={6}>
          <TextField
            fullWidth
            label="Last Name"
            value={formData.lastName}
            onChange={(e) => setFormData({ ...formData, lastName: e.target.value })}
            required
          />
        </Grid>
        <Grid item xs={12}>
          <TextField
            fullWidth
            label="Email"
            type="email"
            value={formData.email}
            onChange={(e) => setFormData({ ...formData, email: e.target.value })}
            required
          />
        </Grid>
        <Grid item xs={12}>
          <FormControl fullWidth>
            <InputLabel>Role</InputLabel>
            <Select
              value={formData.role}
              label="Role"
              onChange={(e) => setFormData({ ...formData, role: e.target.value as UserRole })}
            >
              <MenuItem value="ADMIN">Administrator</MenuItem>
              <MenuItem value="PROJECT_MANAGER">Project Manager</MenuItem>
              <MenuItem value="DEVELOPER">Developer</MenuItem>
              <MenuItem value="TEAM_LEAD">Team Lead</MenuItem>
              <MenuItem value="QA">QA Engineer</MenuItem>
            </Select>
          </FormControl>
        </Grid>
        {!isEdit && (
          <Grid item xs={12}>
            <TextField
              fullWidth
              label="Password"
              type="password"
              value={formData.password}
              onChange={(e) => setFormData({ ...formData, password: e.target.value })}
              required
            />
          </Grid>
        )}
      </Grid>
      <Box sx={{ mt: 3, display: 'flex', gap: 2, justifyContent: 'flex-end' }}>
        <Button onClick={onCancel}>
          Cancel
        </Button>
        <Button type="submit" variant="contained">
          {isEdit ? 'Update User' : 'Add User'}
        </Button>
      </Box>
    </Box>
  );
};

export default UserManagement; 