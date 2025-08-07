import React from 'react';
import {
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Avatar,
  Chip,
  IconButton,
  Typography,
  Box,
} from '@mui/material';
import {
  Edit as EditIcon,
  Delete as DeleteIcon,
} from '@mui/icons-material';

interface User {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  role: string;
  department: string;
  position: string;
  status: 'active' | 'inactive';
  joinDate: string;
}

interface UserListProps {
  users: User[];
  onEditUser: (user: User) => void;
  onDeleteUser: (user: User) => void;
}

const UserList: React.FC<UserListProps> = ({ users, onEditUser, onDeleteUser }) => {
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

  const getStatusColor = (status: string) => {
    return status === 'active' ? 'success' : 'error';
  };

  return (
    <TableContainer component={Paper} variant="outlined">
      <Table>
        <TableHead>
          <TableRow>
            <TableCell>User</TableCell>
            <TableCell>Email</TableCell>
            <TableCell>Role</TableCell>
            <TableCell>Department</TableCell>
            <TableCell>Status</TableCell>
            <TableCell>Join Date</TableCell>
            <TableCell align="center">Actions</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {users.map((user) => (
            <TableRow key={user.id}>
              <TableCell>
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                  <Avatar sx={{ width: 32, height: 32 }}>
                    {user.firstName.charAt(0)}{user.lastName.charAt(0)}
                  </Avatar>
                  <Box>
                    <Typography variant="body2" fontWeight="medium">
                      {user.firstName} {user.lastName}
                    </Typography>
                    <Typography variant="caption" color="text.secondary">
                      {user.position}
                    </Typography>
                  </Box>
                </Box>
              </TableCell>
              <TableCell>{user.email}</TableCell>
              <TableCell>
                <Chip
                  label={getRoleDisplayName(user.role)}
                  color={getRoleColor(user.role)}
                  size="small"
                />
              </TableCell>
              <TableCell>{user.department}</TableCell>
              <TableCell>
                <Chip
                  label={user.status}
                  color={getStatusColor(user.status)}
                  size="small"
                />
              </TableCell>
              <TableCell>
                {new Date(user.joinDate).toLocaleDateString()}
              </TableCell>
              <TableCell align="center">
                <IconButton
                  size="small"
                  onClick={() => onEditUser(user)}
                  color="primary"
                >
                  <EditIcon />
                </IconButton>
                <IconButton
                  size="small"
                  onClick={() => onDeleteUser(user)}
                  color="error"
                >
                  <DeleteIcon />
                </IconButton>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
};

export default UserList; 