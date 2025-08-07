// Form Dialog Component
// Reusable form dialog for creating and editing data

import React from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Typography,
  Box,
  Divider,
  useTheme,
} from '@mui/material';
import {
  Close as CloseIcon,
} from '@mui/icons-material';

// Components
import { Button } from './index';

// Form dialog props interface
interface FormDialogProps {
  open: boolean;
  onClose: () => void;
  onSubmit?: () => void;
  title: string;
  subtitle?: string;
  children: React.ReactNode;
  submitText?: string;
  cancelText?: string;
  submitVariant?: 'primary' | 'secondary';
  cancelVariant?: 'primary' | 'secondary' | 'outlined' | 'text';
  loading?: boolean;
  maxWidth?: 'xs' | 'sm' | 'md' | 'lg' | 'xl';
  fullWidth?: boolean;
  showCloseButton?: boolean;
  disableBackdropClick?: boolean;
  disableEscapeKeyDown?: boolean;
}

// Form dialog component
const FormDialog: React.FC<FormDialogProps> = ({
  open,
  onClose,
  onSubmit,
  title,
  subtitle,
  children,
  submitText = 'Save',
  cancelText = 'Cancel',
  submitVariant = 'primary',
  cancelVariant = 'outlined',
  loading = false,
  maxWidth = 'md',
  fullWidth = true,
  showCloseButton = true,
  disableBackdropClick = false,
  disableEscapeKeyDown = false,
}) => {
  const theme = useTheme();

  // Handle close
  const handleClose = () => {
    if (!loading) {
      onClose();
    }
  };

  // Handle submit
  const handleSubmit = () => {
    if (!loading && onSubmit) {
      onSubmit();
    }
  };

  return (
    <Dialog
      open={open}
      onClose={handleClose}
      maxWidth={maxWidth}
      fullWidth={fullWidth}
      disableBackdropClick={disableBackdropClick || loading}
      disableEscapeKeyDown={disableEscapeKeyDown || loading}
      sx={{
        '& .MuiDialog-paper': {
          borderRadius: theme.shape.borderRadius * 2,
          boxShadow: theme.shadows[24],
          maxHeight: '90vh',
        },
      }}
    >
      {/* Header */}
      <DialogTitle
        sx={{
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          pb: 2,
          borderBottom: `1px solid ${theme.palette.divider}`,
          position: 'relative',
        }}
      >
        <Box>
          <Typography
            variant="h6"
            component="div"
            sx={{
              fontWeight: 600,
              lineHeight: 1.2,
              pr: showCloseButton ? 4 : 0,
            }}
          >
            {title}
          </Typography>
          {subtitle && (
            <Typography
              variant="body2"
              color="text.secondary"
              sx={{
                mt: 0.5,
                pr: showCloseButton ? 4 : 0,
              }}
            >
              {subtitle}
            </Typography>
          )}
        </Box>
        {showCloseButton && (
          <Button
            variant="text"
            size="small"
            onClick={handleClose}
            disabled={loading}
            sx={{
              position: 'absolute',
              right: 8,
              top: 8,
              minWidth: 'auto',
              p: 1,
            }}
          >
            <CloseIcon />
          </Button>
        )}
      </DialogTitle>

      {/* Content */}
      <DialogContent
        sx={{
          p: 3,
          overflow: 'auto',
          '&::-webkit-scrollbar': {
            width: 8,
          },
          '&::-webkit-scrollbar-track': {
            backgroundColor: theme.palette.grey[100],
            borderRadius: 4,
          },
          '&::-webkit-scrollbar-thumb': {
            backgroundColor: theme.palette.grey[300],
            borderRadius: 4,
            '&:hover': {
              backgroundColor: theme.palette.grey[400],
            },
          },
        }}
      >
        {children}
      </DialogContent>

      {/* Actions */}
      <Divider />
      <DialogActions
        sx={{
          p: 3,
          gap: 1,
        }}
      >
        <Button
          variant={cancelVariant}
          onClick={handleClose}
          disabled={loading}
          size="medium"
        >
          {cancelText}
        </Button>
        {onSubmit && (
          <Button
            variant={submitVariant}
            onClick={handleSubmit}
            loading={loading}
            size="medium"
          >
            {submitText}
          </Button>
        )}
      </DialogActions>
    </Dialog>
  );
};

export default FormDialog; 