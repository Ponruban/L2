// ProjectForm Component
import React, { useState, useEffect } from 'react';
import { 
  Box, 
  Grid, 
  Typography, 
  Chip, 
  IconButton, 
  List, 
  ListItem, 
  ListItemText, 
  ListItemSecondaryAction,
  Divider,
  Button as MuiButton,
} from '@mui/material';
import { Add as AddIcon, Remove as RemoveIcon } from '@mui/icons-material';
import { Input, Select, Button, Card, DatePicker } from '@/components/ui';
import { useUsers } from '@/hooks/useUsers';
import type { CreateProjectRequest, UpdateProjectRequest, ProjectMemberRole } from '@/types';

interface ProjectFormProps {
  mode: 'create' | 'edit';
  onSubmit: (data: CreateProjectRequest | UpdateProjectRequest) => void;
  onCancel?: () => void;
  initialData?: any;
}

interface ProjectMember {
  userId: number;
  role: ProjectMemberRole;
  userName?: string;
}

const ProjectForm: React.FC<ProjectFormProps> = ({
  mode,
  onSubmit,
  onCancel,
  initialData,
}) => {
  const { users, isLoading: isLoadingUsers } = useUsers();
  
  const [formData, setFormData] = useState({
    name: initialData?.name || '',
    description: initialData?.description || '',
    status: initialData?.status || 'ACTIVE',
    startDate: initialData?.startDate || '',
    endDate: initialData?.endDate || '',
  });
  
  const [members, setMembers] = useState<ProjectMember[]>(initialData?.members || []);
  const [selectedUserId, setSelectedUserId] = useState<number | ''>('');
  const [selectedRole, setSelectedRole] = useState<ProjectMemberRole>('DEVELOPER');

  // Calculate available users
  const availableUsers = users?.filter(user => 
    !members.find(m => m.userId === user.id)
  ) || [];

  const handleInputChange = (field: string, value: string) => {
    setFormData(prev => ({
      ...prev,
      [field]: value,
    }));
  };

  const handleUserChange = (value: string | number | (string | number)[]) => {
    if (typeof value === 'string' || typeof value === 'number') {
      setSelectedUserId(Number(value));
    }
  };

  const handleRoleChange = (value: string | number | (string | number)[]) => {
    if (typeof value === 'string') {
      setSelectedRole(value as ProjectMemberRole);
    }
  };

  const handleAddMember = () => {
    if (selectedUserId && !members.find(m => m.userId === selectedUserId)) {
      const user = users?.find(u => u.id === selectedUserId);
      
      const newMember: ProjectMember = {
        userId: selectedUserId,
        role: selectedRole,
        userName: user ? (user as any).name || user.email : '',
      };
      
      setMembers(prev => [...prev, newMember]);
      setSelectedUserId('');
    }
  };

  const handleRemoveMember = (userId: number) => {
    setMembers(prev => prev.filter(m => m.userId !== userId));
  };

  const handleSubmit = async () => {
    try {
      // For create mode, include members in the request
      if (mode === 'create') {
        const submitData = {
          ...formData,
          // Convert empty strings to null for dates
          startDate: formData.startDate || null,
          endDate: formData.endDate || null,
          members: members.map(m => ({
            userId: m.userId,
            role: m.role,
          })),
        };
        await onSubmit(submitData);
      } else {
        // For edit mode, include members in the update request
        const submitData = {
          ...formData,
          // Convert empty strings to null for dates
          startDate: formData.startDate || null,
          endDate: formData.endDate || null,
          members: members.map(m => ({
            userId: m.userId,
            role: m.role,
          })),
        };
        await onSubmit(submitData);
      }
    } catch (error) {
      console.error('ProjectForm: Error submitting form:', error);
      // Show error message to user instead of silent redirect
      alert('Error saving project. Please try again.');
    }
  };

  return (
    <Card sx={{ p: 3 }}>
      <Typography variant="h5" sx={{ mb: 3 }}>
        {mode === 'create' ? 'Create Project' : 'Edit Project'}
      </Typography>
      
      <Box sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
        {/* Basic Project Information */}
        <Grid container spacing={2}>
          <Grid item xs={12} md={6}>
            <Input 
              label="Project Name" 
              value={formData.name}
              onChange={(value: string) => handleInputChange('name', value)}
              fullWidth 
            />
          </Grid>
          <Grid item xs={12} md={6}>
            <Select
              label="Status"
              value={formData.status}
              onChange={(value: string | number | (string | number)[]) => {
                if (typeof value === 'string') {
                  handleInputChange('status', value);
                }
              }}
              options={[
                { value: 'ACTIVE', label: 'Active' },
                { value: 'ON_HOLD', label: 'On Hold' },
                { value: 'COMPLETED', label: 'Completed' },
                { value: 'CANCELLED', label: 'Cancelled' },
              ]}
              fullWidth
            />
          </Grid>
        </Grid>
        
        <Input 
          label="Description" 
          value={formData.description}
          onChange={(value: string) => handleInputChange('description', value)}
          multiline 
          rows={4} 
          fullWidth 
        />
        
        <Grid container spacing={2}>
          <Grid item xs={12} md={6}>
            <DatePicker
              label="Start Date"
              value={formData.startDate}
              onChange={(date) => handleInputChange('startDate', date || '')}
              fullWidth
            />
          </Grid>
          <Grid item xs={12} md={6}>
            <DatePicker
              label="End Date"
              value={formData.endDate}
              onChange={(date) => handleInputChange('endDate', date || '')}
              fullWidth
            />
          </Grid>
        </Grid>

        {/* Project Members Section */}
        <Box>
          <Typography variant="h6" sx={{ mb: 2 }}>
            Project Members
          </Typography>
          
          {mode === 'edit' && (
            <Box sx={{ mb: 2, p: 2, backgroundColor: '#fff3cd', border: '1px solid #ffeaa7', borderRadius: 1 }}>
              <Typography variant="body2" color="text.secondary">
                <strong>Note:</strong> You can add or remove project members using the form below. 
                Changes will be saved when you update the project.
              </Typography>
            </Box>
          )}

          {/* Add Member Section */}
          <Box sx={{ display: 'flex', gap: 2, mb: 3, alignItems: 'flex-end' }}>
            <Select
              label="Select User"
              value={selectedUserId}
              onChange={handleUserChange}
              options={availableUsers.map(user => {
                // Use the 'name' property from the user data (exists in actual data but not in type)
                const userName = (user as any).name || user.email || `User ${user.id}`;
                
                return {
                  value: user.id,
                  label: userName,
                };
              })}
              disabled={isLoadingUsers || availableUsers.length === 0}
              sx={{ minWidth: 200 }}
              placeholder={availableUsers.length === 0 ? "No users available" : "Choose a user"}
            />
            <Select
              label="Role"
              value={selectedRole}
              onChange={handleRoleChange}
              options={[
                { value: 'PROJECT_MANAGER', label: 'Project Manager' },
                { value: 'TEAM_LEAD', label: 'Team Lead' },
                { value: 'DEVELOPER', label: 'Developer' },
                { value: 'QA', label: 'QA' },
              ]}
              sx={{ minWidth: 150 }}
            />
            <MuiButton
              variant="outlined"
              startIcon={<AddIcon />}
              onClick={handleAddMember}
              disabled={!selectedUserId || isLoadingUsers}
            >
              Add Member
            </MuiButton>
          </Box>

          {/* Members List */}
          {members.length > 0 ? (
            <List sx={{ border: '1px solid #e0e0e0', borderRadius: 1 }}>
              {members.map((member, index) => (
                <React.Fragment key={member.userId}>
                  <ListItem>
                    <ListItemText
                      primary={member.userName || `User ${member.userId}`}
                      secondary={
                        <Chip 
                          label={member.role.replace('_', ' ')} 
                          size="small" 
                          color="primary" 
                          variant="outlined"
                        />
                      }
                    />
                    <ListItemSecondaryAction>
                      <IconButton
                        edge="end"
                        onClick={() => handleRemoveMember(member.userId)}
                        color="error"
                      >
                        <RemoveIcon />
                      </IconButton>
                    </ListItemSecondaryAction>
                  </ListItem>
                  {index < members.length - 1 && <Divider />}
                </React.Fragment>
              ))}
            </List>
          ) : (
            <Box sx={{ 
              p: 2, 
              border: '1px dashed #e0e0e0', 
              borderRadius: 1, 
              textAlign: 'center',
              color: 'text.secondary'
            }}>
              No members added yet. Add team members to get started.
            </Box>
          )}
        </Box>

        {/* Action Buttons */}
        <Box sx={{ display: 'flex', gap: 2, justifyContent: 'flex-end' }}>
          {onCancel && <Button variant="outlined" onClick={onCancel}>Cancel</Button>}
          <Button 
            variant="primary" 
            onClick={handleSubmit}
            disabled={!formData.name.trim()}
          >
            {mode === 'create' ? 'Create Project' : 'Update Project'}
          </Button>
        </Box>
      </Box>
    </Card>
  );
};

export default ProjectForm; 
