import React, { useState } from 'react';
import {
  Box,
  Typography,
  Card,
  CardContent,
  Grid,
  Button,
  Alert,
  Switch,
  FormControlLabel,
  Divider,
} from '@mui/material';
import { Save, Settings as SettingsIcon } from '@mui/icons-material';
import { useAuthContext } from '@/contexts/AuthContext';
import { useTheme } from '@/contexts/ThemeContext';

const Settings: React.FC = () => {
  const { user } = useAuthContext();
  const { mode, setTheme } = useTheme();
  const [darkMode, setDarkMode] = useState(mode === 'dark');

  if (!user) {
    return (
      <Box sx={{ p: 3 }}>
        <Typography variant="h4" sx={{ mb: 3 }}>
          Settings
        </Typography>
        <Alert severity="error">User not found. Please log in again.</Alert>
      </Box>
    );
  }

  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h4" sx={{ mb: 3 }}>
        Settings
      </Typography>

      <Grid container spacing={3}>
        {/* General Settings */}
        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                General Settings
              </Typography>
              <Divider sx={{ mb: 2 }} />
              
              <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                <FormControlLabel
                  control={
                    <Switch
                      checked={darkMode}
                      onChange={(e) => {
                        setDarkMode(e.target.checked);
                        setTheme(e.target.checked ? 'dark' : 'light');
                      }}
                    />
                  }
                  label="Dark Mode"
                />
                <FormControlLabel
                  control={<Switch defaultChecked />}
                  label="Auto-save"
                />
                <FormControlLabel
                  control={<Switch />}
                  label="Show welcome screen"
                />
              </Box>
            </CardContent>
          </Card>
        </Grid>

        {/* Notification Settings */}
        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Notification Settings
              </Typography>
              <Divider sx={{ mb: 2 }} />
              
              <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                <FormControlLabel
                  control={<Switch defaultChecked />}
                  label="Email Notifications"
                />
                <FormControlLabel
                  control={<Switch defaultChecked />}
                  label="Push Notifications"
                />
                <FormControlLabel
                  control={<Switch defaultChecked />}
                  label="Task Assignments"
                />
                <FormControlLabel
                  control={<Switch defaultChecked />}
                  label="Project Updates"
                />
                <FormControlLabel
                  control={<Switch defaultChecked />}
                  label="Deadline Reminders"
                />
              </Box>
            </CardContent>
          </Card>
        </Grid>

        {/* Account Settings */}
        <Grid item xs={12}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Account Settings
              </Typography>
              <Divider sx={{ mb: 2 }} />
              
              <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                <Typography variant="body2" color="text.secondary">
                  Email: {user.email}
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  Role: {user.role}
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  Member since: {user.createdAt ? new Date(user.createdAt).toLocaleDateString() : 'N/A'}
                </Typography>
              </Box>
            </CardContent>
          </Card>
        </Grid>

        {/* Save Button */}
        <Grid item xs={12}>
          <Box sx={{ display: 'flex', justifyContent: 'flex-end' }}>
            <Button
              variant="contained"
              startIcon={<Save />}
              onClick={() => alert('Settings saved! (This is a demo)')}
            >
              Save Settings
            </Button>
          </Box>
        </Grid>
      </Grid>
    </Box>
  );
};

export default Settings; 