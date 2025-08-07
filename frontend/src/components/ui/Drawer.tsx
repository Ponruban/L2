// Drawer Component
// Reusable drawer component for side panels and navigation

import React from 'react';
import {
  Drawer as MuiDrawer,
  Box,
  Typography,
  IconButton,
  Divider,
  useTheme,
  useMediaQuery,
} from '@mui/material';
import {
  Close as CloseIcon,
  ChevronLeft as ChevronLeftIcon,
  ChevronRight as ChevronRightIcon,
} from '@mui/icons-material';

// Components
import { Button } from './index';

// Drawer props interface
interface DrawerProps {
  open: boolean;
  onClose: () => void;
  children: React.ReactNode;
  title?: string;
  subtitle?: string;
  anchor?: 'left' | 'right' | 'top' | 'bottom';
  width?: number | string;
  height?: number | string;
  variant?: 'temporary' | 'persistent' | 'permanent';
  showCloseButton?: boolean;
  showBackdrop?: boolean;
  disableEscapeKeyDown?: boolean;
  className?: string;
  sx?: any;
}

// Drawer component
const Drawer: React.FC<DrawerProps> = ({
  open,
  onClose,
  children,
  title,
  subtitle,
  anchor = 'right',
  width = 400,
  height = '100%',
  variant = 'temporary',
  showCloseButton = true,
  showBackdrop = true,
  disableEscapeKeyDown = false,
  className,
  sx,
}) => {
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('md'));

  // Determine variant based on screen size
  const getVariant = () => {
    if (isMobile) {
      return 'temporary';
    }
    return variant;
  };

  // Get drawer styles
  const getDrawerStyles = () => {
    const isHorizontal = anchor === 'top' || anchor === 'bottom';
    
    return {
      width: isHorizontal ? '100%' : width,
      height: isHorizontal ? height : '100%',
      flexShrink: 0,
      '& .MuiDrawer-paper': {
        width: isHorizontal ? '100%' : width,
        height: isHorizontal ? height : '100%',
        boxSizing: 'border-box',
        border: `1px solid ${theme.palette.divider}`,
        borderRadius: isHorizontal ? 0 : theme.shape.borderRadius,
        boxShadow: theme.shadows[8],
        overflow: 'visible',
        ...sx,
      },
    };
  };

  return (
    <MuiDrawer
      anchor={anchor}
      open={open}
      onClose={onClose}
      variant={getVariant()}
      disableEscapeKeyDown={disableEscapeKeyDown}
      ModalProps={{
        keepMounted: true, // Better open performance on mobile
        disableBackdropClick: !showBackdrop,
      }}
      className={className}
      sx={getDrawerStyles()}
    >
      {/* Header */}
      {(title || subtitle || showCloseButton) && (
        <Box
          sx={{
            p: 3,
            borderBottom: `1px solid ${theme.palette.divider}`,
            backgroundColor: 'background.paper',
            position: 'sticky',
            top: 0,
            zIndex: 1,
          }}
        >
          <Box
            sx={{
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'space-between',
            }}
          >
            <Box sx={{ flex: 1 }}>
              {title && (
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
              )}
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
              <Box sx={{ display: 'flex', gap: 1 }}>
                <Button
                  variant="text"
                  size="small"
                  onClick={onClose}
                  sx={{
                    minWidth: 'auto',
                    p: 1,
                  }}
                >
                  {anchor === 'left' ? <ChevronLeftIcon /> : <ChevronRightIcon />}
                </Button>
                <Button
                  variant="text"
                  size="small"
                  onClick={onClose}
                  sx={{
                    minWidth: 'auto',
                    p: 1,
                  }}
                >
                  <CloseIcon />
                </Button>
              </Box>
            )}
          </Box>
        </Box>
      )}

      {/* Content */}
      <Box
        sx={{
          flex: 1,
          overflow: 'auto',
          p: 3,
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
      </Box>
    </MuiDrawer>
  );
};

export default Drawer; 