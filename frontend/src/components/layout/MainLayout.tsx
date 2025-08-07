// Main Layout Component
// Provides the main application layout with sidebar, header, and content area

import React, { useState } from "react";
import { Box, CssBaseline, useTheme } from "@mui/material";
import Sidebar from "./Sidebar";
import Header from "./Header";

interface MainLayoutProps {
  children: React.ReactNode;
}

const MainLayout: React.FC<MainLayoutProps> = ({ children }) => {
  const [sidebarOpen, setSidebarOpen] = useState(true);
  // const theme = useTheme();

  const handleSidebarToggle = () => {
    setSidebarOpen(!sidebarOpen);
  };

  return (
    <Box sx={{ display: "flex", minHeight: "100vh" }}>
      <CssBaseline />
      <Sidebar open={sidebarOpen} onToggle={handleSidebarToggle} />
      <Box
        component="main"
        sx={{
          flexGrow: 1,
          display: "flex",
          flexDirection: "column",
          minHeight: "100vh",
          backgroundColor: "background.default",
        }}
      >
        <Header onMenuClick={handleSidebarToggle} />
        <Box sx={{ flexGrow: 1, overflow: 'auto' }}>
          {children}
        </Box>
      </Box>
    </Box>
  );
};

export default MainLayout; 
