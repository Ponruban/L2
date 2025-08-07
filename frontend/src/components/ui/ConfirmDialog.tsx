// Confirm Dialog Component
// Reusable confirmation dialog for delete confirmations and critical actions

import React from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  DialogContentText,
  Typography,
  Box,
  useTheme,
} from '@mui/material';
import {
  Warning as WarningIcon,
  Error as ErrorIcon,
  Info as InfoIcon,
  Help as HelpIcon,
} from '@mui/icons-material';

// Components
import { Button } from './index';

// Confirm dialog props interface
interface ConfirmDialogProps {
  open: boolean;
  onClose: () => void;
  onConfirm: () => void;
  title: string;
  message: string;
  type?: 'warning' | 'error' | 'info' | 'question';
  confirmText?: string;
  cancelText?: string;
  confirmVariant?: 'primary' | 'secondary' | 'danger';
  cancelVariant?: 'primary' | 'secondary' | 'outlined' | 'text';
  loading?: boolean;
  maxWidth?: 'xs' | 'sm' | 'md';
  fullWidth?: boolean;
}

// Confirm dialog component
const ConfirmDialog: React.FC<ConfirmDialogProps> = ({
  open,
  onClose,
  onConfirm,
  title,
  message,
  type = 'warning',
  confirmText = 'Confirm',
  cancelText = 'Cancel',
  confirmVariant = 'danger',
  cancelVariant = 'outlined',
  loading = false,
  maxWidth = 'sm',
  fullWidth = true,
}) => {
  const theme = useTheme();

  // Get type configuration
  const getTypeConfig = () => {
    switch (type) {
      case 'error':
        return {
          icon: <ErrorIcon />,
          color: theme.palette.error.main,
          backgroundColor: theme.palette.error.light + '20',
        };
      case 'info':
        return {
          icon: <InfoIcon />,
          color: theme.palette.info.main,
          backgroundColor: theme.palette.info.light + '20',
        };
      case 'question':
        return {
          icon: <HelpIcon />,
          color: theme.palette.primary.main,
          backgroundColor: theme.palette.primary.light + '20',
        };
      default: // warning
        return {
          icon: <WarningIcon />,
          color: theme.palette.warning.main,
          backgroundColor: theme.palette.warning.light + '20',
        };
    }
  };

  const typeConfig = getTypeConfig();

  // Handle confirm
  const handleConfirm = () => {
    if (!loading) {
      onConfirm();
    }
  };

  // Handle close
  const handleClose = () => {
    if (!loading) {
      onClose();
    }
  };

  return (
    <Dialog
      open={open}
      onClose={handleClose}
      maxWidth={maxWidth}
      fullWidth={fullWidth}
      disableEscapeKeyDown={loading}
      disableBackdropClick={loading}
      sx={{
        '& .MuiDialog-paper': {
          borderRadius: theme.shape.borderRadius * 2,
          boxShadow: theme.shadows[24],
        },
      }}
    >
      {/* Header with Icon */}
      <DialogTitle
        sx={{
          display: 'flex',
          alignItems: 'center',
          gap: 2,
          pb: 2,
          borderBottom: `1px solid ${theme.palette.divider}`,
        }}
      >
        <Box
          sx={{
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            width: 48,
            height: 48,
            borderRadius: '50%',
            backgroundColor: typeConfig.backgroundColor,
            color: typeConfig.color,
          }}
        >
          {typeConfig.icon}
        </Box>
        <Typography
          variant="h6"
          component="div"
          sx={{
            fontWeight: 600,
            lineHeight: 1.2,
          }}
        >
          {title}
        </Typography>
      </DialogTitle>

      {/* Content */}
      <DialogContent sx={{ pt: 3, pb: 2 }}>
        <DialogContentText
          sx={{
            fontSize: '1rem',
            lineHeight: 1.5,
            color: 'text.primary',
            mb: 0,
          }}
        >
          {message}
        </DialogContentText>
      </DialogContent>

      {/* Actions */}
      <DialogActions
        sx={{
          p: 3,
          pt: 1,
          gap: 1,
          borderTop: `1px solid ${theme.palette.divider}`,
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
        <Button
          variant={confirmVariant}
          onClick={handleConfirm}
          loading={loading}
          size="medium"
        >
          {confirmText}
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default ConfirmDialog; 