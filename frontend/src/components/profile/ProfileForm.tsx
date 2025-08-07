import React from 'react';
import { useForm } from 'react-hook-form';
import {
  Box,
  Grid,
  TextField,
  Button,
  Typography,
} from '@mui/material';

interface UserProfile {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  role: string;
  department: string;
  position: string;
  joinDate: string;
  avatar?: string;
  phone?: string;
  bio?: string;
}

interface ProfileFormProps {
  profile: UserProfile;
  onSave: (data: Partial<UserProfile>) => void;
  onCancel: () => void;
}

interface FormData {
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  bio: string;
}

const ProfileForm: React.FC<ProfileFormProps> = ({ profile, onSave, onCancel }) => {
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<FormData>({
    defaultValues: {
      firstName: profile.firstName,
      lastName: profile.lastName,
      email: profile.email,
      phone: profile.phone || '',
      bio: profile.bio || '',
    },
  });

  const onSubmit = (data: FormData) => {
    onSave(data);
  };

  return (
    <Box component="form" onSubmit={handleSubmit(onSubmit)}>
      <Grid container spacing={2}>
        <Grid item xs={12} sm={6}>
          <TextField
            {...register('firstName', { required: 'First name is required' })}
            label="First Name"
            fullWidth
            error={!!errors.firstName}
            helperText={errors.firstName?.message}
          />
        </Grid>
        
        <Grid item xs={12} sm={6}>
          <TextField
            {...register('lastName', { required: 'Last name is required' })}
            label="Last Name"
            fullWidth
            error={!!errors.lastName}
            helperText={errors.lastName?.message}
          />
        </Grid>
        
        <Grid item xs={12} sm={6}>
          <TextField
            {...register('email', {
              required: 'Email is required',
              pattern: {
                value: /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i,
                message: 'Invalid email address',
              },
            })}
            label="Email"
            type="email"
            fullWidth
            error={!!errors.email}
            helperText={errors.email?.message}
          />
        </Grid>
        
        <Grid item xs={12} sm={6}>
          <TextField
            {...register('phone')}
            label="Phone"
            fullWidth
            error={!!errors.phone}
            helperText={errors.phone?.message}
          />
        </Grid>
        
        <Grid item xs={12}>
          <TextField
            {...register('bio')}
            label="Bio"
            multiline
            rows={4}
            fullWidth
            error={!!errors.bio}
            helperText={errors.bio?.message}
          />
        </Grid>
      </Grid>
      
      <Box sx={{ mt: 3, display: 'flex', gap: 2 }}>
        <Button
          type="submit"
          variant="contained"
          disabled={isSubmitting}
        >
          Save Changes
        </Button>
        <Button
          variant="outlined"
          onClick={onCancel}
          disabled={isSubmitting}
        >
          Cancel
        </Button>
      </Box>
    </Box>
  );
};

export default ProfileForm; 