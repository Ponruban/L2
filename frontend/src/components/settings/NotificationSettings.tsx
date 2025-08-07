import React from 'react';
import {
  Card,
  CardContent,
  Typography,
  FormControlLabel,
  Switch,
  Box,
  Divider,
} from '@mui/material';
import {
  Email,
  Notifications,
  Assignment,
  Update,
  Schedule,
} from '@mui/icons-material';

interface NotificationPreferences {
  emailNotifications: boolean;
  pushNotifications: boolean;
  taskAssignments: boolean;
  projectUpdates: boolean;
  deadlineReminders: boolean;
}

interface NotificationSettingsProps {
  notifications: NotificationPreferences;
  onNotificationChange: (notifications: NotificationPreferences) => void;
}

const NotificationSettings: React.FC<NotificationSettingsProps> = ({
  notifications,
  onNotificationChange,
}) => {
  const handleToggle = (key: keyof NotificationPreferences) => {
    onNotificationChange({
      ...notifications,
      [key]: !notifications[key],
    });
  };

  return (
    <Card>
      <CardContent>
        <Typography variant="h6" gutterBottom>
          Notification Settings
        </Typography>
        <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
          Configure how you want to receive notifications
        </Typography>

        <Box sx={{ mb: 3 }}>
          <Typography variant="subtitle1" gutterBottom>
            General Notifications
          </Typography>
          <FormControlLabel
            control={
              <Switch
                checked={notifications.emailNotifications}
                onChange={() => handleToggle('emailNotifications')}
              />
            }
            label={
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                <Email color="primary" />
                <Typography>Email Notifications</Typography>
              </Box>
            }
          />
          <FormControlLabel
            control={
              <Switch
                checked={notifications.pushNotifications}
                onChange={() => handleToggle('pushNotifications')}
              />
            }
            label={
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                <Notifications color="primary" />
                <Typography>Push Notifications</Typography>
              </Box>
            }
          />
        </Box>

        <Divider sx={{ my: 2 }} />

        <Box>
          <Typography variant="subtitle1" gutterBottom>
            Specific Notifications
          </Typography>
          <FormControlLabel
            control={
              <Switch
                checked={notifications.taskAssignments}
                onChange={() => handleToggle('taskAssignments')}
              />
            }
            label={
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                <Assignment color="primary" />
                <Typography>Task Assignments</Typography>
              </Box>
            }
          />
          <FormControlLabel
            control={
              <Switch
                checked={notifications.projectUpdates}
                onChange={() => handleToggle('projectUpdates')}
              />
            }
            label={
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                <Update color="primary" />
                <Typography>Project Updates</Typography>
              </Box>
            }
          />
          <FormControlLabel
            control={
              <Switch
                checked={notifications.deadlineReminders}
                onChange={() => handleToggle('deadlineReminders')}
              />
            }
            label={
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                <Schedule color="primary" />
                <Typography>Deadline Reminders</Typography>
              </Box>
            }
          />
        </Box>
      </CardContent>
    </Card>
  );
};

export default NotificationSettings; 