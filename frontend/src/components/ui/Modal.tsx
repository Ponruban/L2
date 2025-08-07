// Modal Component
// Reusable modal component with backdrop, animations, and flexible content

import React, { useEffect, useRef } from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  IconButton,
  Typography,
  Box,
  useTheme,
} from '@mui/material';
import {
  Close as CloseIcon,
} from '@mui/icons-material';
import { useAccessibility, useFocusRestoration } from '@/hooks';

interface ModalProps {
  open: boolean;
  onClose: () => void;
  title?: string;
  children: React.ReactNode;
  actions?: React.ReactNode;
  maxWidth?: 'xs' | 'sm' | 'md' | 'lg' | 'xl';
  fullWidth?: boolean;
  closeOnBackdropClick?: boolean;
  closeOnEscape?: boolean;
  showCloseButton?: boolean;
  disableBackdropClick?: boolean;
  disableEscapeKeyDown?: boolean;
  keepMounted?: boolean;
  className?: string;
  sx?: React.ComponentProps<typeof Dialog>['sx'];
  // Accessibility props
  'aria-label'?: string;
  'aria-describedby'?: string;
  'aria-labelledby'?: string;
  role?: string;
}

const Modal: React.FC<ModalProps> = ({
  open,
  onClose,
  title,
  children,
  actions,
  maxWidth = 'sm',
  fullWidth = false,
  closeOnBackdropClick = true,
  closeOnEscape = true,
  showCloseButton = true,
  disableBackdropClick = false,
  disableEscapeKeyDown = false,
  keepMounted = false,
  className,
  sx,
  // Accessibility props
  'aria-label': ariaLabel,
  'aria-describedby': ariaDescribedby,
  'aria-labelledby': ariaLabelledby,
  role = 'dialog',
}) => {
  const theme = useTheme();
  const dialogRef = useRef<HTMLDivElement>(null);
  const { setupFocusTrap, announce } = useAccessibility();
  const { saveFocus, restoreFocus } = useFocusRestoration();

  // Generate unique IDs for accessibility
  const modalId = `modal-${title?.toLowerCase().replace(/\s+/g, '-') || 'dialog'}`;
  const titleId = title ? `${modalId}-title` : undefined;
  const contentId = `${modalId}-content`;

  // Handle close
  const handleClose = (event: object, reason: string) => {
    if (reason === 'backdropClick' && disableBackdropClick) return;
    if (reason === 'escapeKeyDown' && disableEscapeKeyDown) return;
    
    announce('Modal closed');
    onClose();
  };

  // Focus management
  useEffect(() => {
    if (open) {
      saveFocus();
      announce(`Modal opened: ${title || 'Dialog'}`);
      
      if (dialogRef.current) {
        const cleanup = setupFocusTrap(dialogRef.current);
        return cleanup;
      }
    } else {
      restoreFocus();
    }
  }, [open, title, announce, setupFocusTrap, saveFocus, restoreFocus]);

  return (
    <Dialog
      open={open}
      onClose={handleClose}
      maxWidth={maxWidth}
      fullWidth={fullWidth}
      keepMounted={keepMounted}
      disableEscapeKeyDown={disableEscapeKeyDown}
      className={className}
      sx={sx}
      // Accessibility attributes
      aria-labelledby={ariaLabelledby || titleId}
      aria-describedby={ariaDescribedby || contentId}
      aria-label={ariaLabel}
      role={role}
      aria-modal="true"
      ref={dialogRef}
    >
      {/* Title */}
      {(title || showCloseButton) && (
        <DialogTitle
          id={titleId}
          sx={{
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            borderBottom: `1px solid ${theme.palette.divider}`,
            pb: 2,
            position: 'relative',
          }}
        >
          {title && (
            <Typography
              variant="h6"
              component="h2"
              sx={{
                fontWeight: 600,
                color: 'text.primary',
                pr: showCloseButton ? 4 : 0,
              }}
            >
              {title}
            </Typography>
          )}
          
          {showCloseButton && (
            <IconButton
              onClick={onClose}
              aria-label="Close modal"
              sx={{
                position: 'absolute',
                right: 8,
                top: 8,
                color: 'text.secondary',
                '&:hover': {
                  color: 'text.primary',
                },
              }}
            >
              <CloseIcon />
            </IconButton>
          )}
        </DialogTitle>
      )}

      {/* Content */}
      <DialogContent
        id={contentId}
        sx={{
          p: 3,
          overflow: 'auto',
        }}
      >
        {children}
      </DialogContent>

      {/* Actions */}
      {actions && (
        <DialogActions
          sx={{
            p: 3,
            pt: 1,
            borderTop: `1px solid ${theme.palette.divider}`,
            gap: 1,
          }}
        >
          {actions}
        </DialogActions>
      )}
    </Dialog>
  );
};

export default Modal; 