// Header Component
// Main application header with user menu, notifications, and navigation

import React, { useState } from 'react';
import {
  AppBar,
  Toolbar,
  Typography,
  IconButton,
  Box,
  Avatar,
  Menu,
  MenuItem,
  Badge,
  Tooltip,
  Divider,
} from '@mui/material';
import {
  Menu as MenuIcon,
  Notifications as NotificationsIcon,
  AccountCircle as AccountIcon,
  Settings as SettingsIcon,
  Logout as LogoutIcon,
  Person as ProfileIcon,
} from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import { useAuthContext } from '@/contexts/AuthContext';
import ThemeToggle from '@/components/ui/ThemeToggle';

interface HeaderProps {
  onMenuClick: () => void;
}

const Header: React.FC<HeaderProps> = ({ onMenuClick }) => {
  const navigate = useNavigate();
  const { user, logout } = useAuthContext();
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
  const [notificationAnchorEl, setNotificationAnchorEl] = useState<null | HTMLElement>(null);

  const handleProfileMenuOpen = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(event.currentTarget);
  };

  const handleNotificationMenuOpen = (event: React.MouseEvent<HTMLElement>) => {
    setNotificationAnchorEl(event.currentTarget);
  };

  const handleMenuClose = () => {
    setAnchorEl(null);
    setNotificationAnchorEl(null);
  };

  const handleProfileClick = () => {
    handleMenuClose();
    navigate('/profile');
  };

  const handleSettingsClick = () => {
    handleMenuClose();
    navigate('/settings');
  };

  const handleLogout = async () => {
    handleMenuClose();
    try {
      await logout();
      navigate('/login');
    } catch (error) {
      console.error('Logout failed:', error);
    }
  };

  const getUserInitials = () => {
    if (!user) return 'U';
    return `${user.firstName?.charAt(0) || ''}${user.lastName?.charAt(0) || ''}`.toUpperCase();
  };

  const getUserDisplayName = () => {
    if (!user) return 'User';
    return `${user.firstName} ${user.lastName}`;
  };

  return (
    <AppBar
      position="sticky"
      sx={{
        backgroundColor: 'background.paper',
        color: 'text.primary',
        boxShadow: '0 1px 3px rgba(0,0,0,0.12)',
        zIndex: (theme) => theme.zIndex.drawer + 1,
      }}
    >
      <Toolbar>
        <IconButton
          edge="start"
          color="inherit"
          aria-label="menu"
          onClick={onMenuClick}
          sx={{ mr: 2 }}
        >
          <MenuIcon />
        </IconButton>

        <Typography
          variant="h6"
          component="div"
          sx={{ flexGrow: 1, fontWeight: 600 }}
        >
          Project Management Dashboard
        </Typography>

        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
          {/* Theme Toggle */}
          <ThemeToggle />

          {/* Notifications */}
          <Tooltip title="Notifications">
            <IconButton
              color="inherit"
              onClick={handleNotificationMenuOpen}
            >
              <Badge badgeContent={3} color="error">
                <NotificationsIcon />
              </Badge>
            </IconButton>
          </Tooltip>

          {/* User Menu */}
          <Tooltip title="User Menu">
            <IconButton
              color="inherit"
              onClick={handleProfileMenuOpen}
              sx={{ ml: 1 }}
            >
              <Avatar
                sx={{
                  width: 32,
                  height: 32,
                  backgroundColor: 'primary.main',
                  color: 'white',
                  fontSize: '0.875rem',
                }}
              >
                {getUserInitials()}
              </Avatar>
            </IconButton>
          </Tooltip>
        </Box>

        {/* Notifications Menu */}
        <Menu
          anchorEl={notificationAnchorEl}
          open={Boolean(notificationAnchorEl)}
          onClose={handleMenuClose}
          PaperProps={{
            sx: {
              mt: 1,
              minWidth: 300,
              maxHeight: 400,
            },
          }}
        >
          <MenuItem>
            <Typography variant="subtitle2" sx={{ fontWeight: 600 }}>
              Notifications
            </Typography>
          </MenuItem>
          <Divider />
          <MenuItem>
            <Box>
              <Typography variant="body2">New task assigned to you</Typography>
              <Typography variant="caption" color="text.secondary">
                2 minutes ago
              </Typography>
            </Box>
          </MenuItem>
          <MenuItem>
            <Box>
              <Typography variant="body2">Project status updated</Typography>
              <Typography variant="caption" color="text.secondary">
                1 hour ago
              </Typography>
            </Box>
          </MenuItem>
          <MenuItem>
            <Box>
              <Typography variant="body2">Team meeting reminder</Typography>
              <Typography variant="caption" color="text.secondary">
                3 hours ago
              </Typography>
            </Box>
          </MenuItem>
        </Menu>

        {/* User Profile Menu */}
        <Menu
          anchorEl={anchorEl}
          open={Boolean(anchorEl)}
          onClose={handleMenuClose}
          PaperProps={{
            sx: {
              mt: 1,
              minWidth: 200,
            },
          }}
        >
          <MenuItem disabled>
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
              <Avatar
                sx={{
                  width: 24,
                  height: 24,
                  backgroundColor: 'primary.main',
                  color: 'white',
                  fontSize: '0.75rem',
                }}
              >
                {getUserInitials()}
              </Avatar>
              <Box>
                <Typography variant="body2" sx={{ fontWeight: 600 }}>
                  {getUserDisplayName()}
                </Typography>
                <Typography variant="caption" color="text.secondary">
                  {user?.role || 'User'}
                </Typography>
              </Box>
            </Box>
          </MenuItem>
          <Divider />
          <MenuItem onClick={handleProfileClick}>
            <ProfileIcon sx={{ mr: 1, fontSize: 20 }} />
            Profile
          </MenuItem>
          <MenuItem onClick={handleSettingsClick}>
            <SettingsIcon sx={{ mr: 1, fontSize: 20 }} />
            Settings
          </MenuItem>
          <Divider />
          <MenuItem onClick={handleLogout}>
            <LogoutIcon sx={{ mr: 1, fontSize: 20 }} />
            Logout
          </MenuItem>
        </Menu>
      </Toolbar>
    </AppBar>
  );
};

export default Header;