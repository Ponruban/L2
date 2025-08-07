import React from 'react';
import { useForm } from 'react-hook-form';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Button,
  Alert,
} from '@mui/material';

interface PasswordChangeData {
  currentPassword: string;
  newPassword: string;
  confirmPassword: string;
}

interface PasswordChangeFormProps {
  open: boolean;
  onClose: () => void;
  onSubmit: (data: PasswordChangeData) => void;
}

const PasswordChangeForm: React.FC<PasswordChangeFormProps> = ({
  open,
  onClose,
  onSubmit,
}) => {
  const {
    register,
    handleSubmit,
    watch,
    formState: { errors, isSubmitting },
    reset,
  } = useForm<PasswordChangeData>();

  const newPassword = watch('newPassword');

  const handleFormSubmit = (data: PasswordChangeData) => {
    onSubmit(data);
    reset();
  };

  const handleClose = () => {
    reset();
    onClose();
  };

  return (
    <Dialog open={open} onClose={handleClose} maxWidth="sm" fullWidth>
      <DialogTitle>Change Password</DialogTitle>
      <form onSubmit={handleSubmit(handleFormSubmit)}>
        <DialogContent>
          <TextField
            {...register('currentPassword', { required: 'Current password is required' })}
            label="Current Password"
            type="password"
            fullWidth
            margin="normal"
            error={!!errors.currentPassword}
            helperText={errors.currentPassword?.message}
          />
          
          <TextField
            {...register('newPassword', {
              required: 'New password is required',
              minLength: {
                value: 8,
                message: 'Password must be at least 8 characters long',
              },
              pattern: {
                value: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)/,
                message: 'Password must contain at least one uppercase letter, one lowercase letter, and one number',
              },
            })}
            label="New Password"
            type="password"
            fullWidth
            margin="normal"
            error={!!errors.newPassword}
            helperText={errors.newPassword?.message}
          />
          
          <TextField
            {...register('confirmPassword', {
              required: 'Please confirm your new password',
              validate: (value) => value === newPassword || 'Passwords do not match',
            })}
            label="Confirm New Password"
            type="password"
            fullWidth
            margin="normal"
            error={!!errors.confirmPassword}
            helperText={errors.confirmPassword?.message}
          />
          
          <Alert severity="info" sx={{ mt: 2 }}>
            Password must be at least 8 characters long and contain uppercase, lowercase, and numeric characters.
          </Alert>
        </DialogContent>
        
        <DialogActions>
          <Button onClick={handleClose} disabled={isSubmitting}>
            Cancel
          </Button>
          <Button
            type="submit"
            variant="contained"
            disabled={isSubmitting}
          >
            Change Password
          </Button>
        </DialogActions>
      </form>
    </Dialog>
  );
};

export default PasswordChangeForm; 