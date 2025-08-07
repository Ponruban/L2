// Sidebar Component
// Navigation sidebar with menu items and role-based access control

import React from "react";
import {
  Drawer,
  List,
  ListItem,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Divider,
  Box,
  Typography,
  Avatar,
  useMediaQuery,
} from "@mui/material";
import {
  Dashboard as DashboardIcon,
  Folder as ProjectsIcon,
  Assignment as TasksIcon,
  Analytics as AnalyticsIcon,
  AdminPanelSettings as AdminIcon,
  Person as ProfileIcon,
  Settings as SettingsIcon,
} from "@mui/icons-material";
import { useLocation, useNavigate } from "react-router-dom";
import { useAuthContext } from "@/contexts/AuthContext";

interface SidebarProps {
  open: boolean;
  onToggle: () => void;
}

const Sidebar: React.FC<SidebarProps> = ({ open, onToggle }) => {
  const location = useLocation();
  const navigate = useNavigate();
  const isMobile = useMediaQuery("(max-width:768px)");
  const { user } = useAuthContext();

  const menuItems = [
    { text: "Dashboard", icon: <DashboardIcon />, path: "/dashboard" },
    { text: "Projects", icon: <ProjectsIcon />, path: "/projects" },
    { text: "Tasks", icon: <TasksIcon />, path: "/tasks" },
    { 
      text: "Analytics", 
      icon: <AnalyticsIcon />, 
      path: "/analytics",
      roles: ['ADMIN', 'PROJECT_MANAGER']
    },
    { 
      text: "Admin", 
      icon: <AdminIcon />, 
      path: "/admin",
      roles: ['ADMIN']
    },
    { text: "Profile", icon: <ProfileIcon />, path: "/profile" },
    { text: "Settings", icon: <SettingsIcon />, path: "/settings" },
  ];

  // Filter menu items based on user role
  const filteredMenuItems = menuItems.filter(item => {
    if (!item.roles) return true;
    return user && item.roles.includes(user.role);
  });

  const handleNavigation = (path: string) => {
    navigate(path);
    if (isMobile) {
      onToggle();
    }
  };

  return (
    <Drawer
      variant={isMobile ? "temporary" : "persistent"}
      open={open}
      onClose={onToggle}
      sx={{
        width: 280,
        flexShrink: 0,
        "& .MuiDrawer-paper": {
          width: 280,
          boxSizing: "border-box",
          borderRight: "1px solid",
          borderColor: "divider",
        },
      }}
    >
      <Box sx={{ p: 2 }}>
        <Typography variant="h6" sx={{ mb: 2 }}>
          Project Management
        </Typography>
        <Avatar sx={{ width: 40, height: 40, mb: 2 }}>
          {user ? user.firstName?.charAt(0) || user.email?.charAt(0) || 'U' : 'U'}
        </Avatar>
      </Box>
      <Divider />
      <List>
        {filteredMenuItems.map((item) => (
          <ListItem key={item.text} disablePadding>
            <ListItemButton
              selected={location.pathname === item.path}
              onClick={() => handleNavigation(item.path)}
            >
              <ListItemIcon>{item.icon}</ListItemIcon>
              <ListItemText primary={item.text} />
            </ListItemButton>
          </ListItem>
        ))}
      </List>
    </Drawer>
  );
};

export default Sidebar; 