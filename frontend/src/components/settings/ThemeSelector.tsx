import React from 'react';
import {
  Card,
  CardContent,
  Typography,
  FormControl,
  FormControlLabel,
  Radio,
  RadioGroup,
  Box,
} from '@mui/material';
import { Brightness4, Brightness7 } from '@mui/icons-material';

interface ThemeSelectorProps {
  theme: 'light' | 'dark';
  onThemeChange: (theme: 'light' | 'dark') => void;
}

const ThemeSelector: React.FC<ThemeSelectorProps> = ({ theme, onThemeChange }) => {
  const handleThemeChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    onThemeChange(event.target.value as 'light' | 'dark');
  };

  return (
    <Card>
      <CardContent>
        <Typography variant="h6" gutterBottom>
          Theme Settings
        </Typography>
        <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
          Choose your preferred theme for the application
        </Typography>
        
        <FormControl component="fieldset">
          <RadioGroup
            value={theme}
            onChange={handleThemeChange}
            name="theme-radio-group"
          >
            <FormControlLabel
              value="light"
              control={<Radio />}
              label={
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                  <Brightness7 color="primary" />
                  <Typography>Light Theme</Typography>
                </Box>
              }
            />
            <FormControlLabel
              value="dark"
              control={<Radio />}
              label={
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                  <Brightness4 color="primary" />
                  <Typography>Dark Theme</Typography>
                </Box>
              }
            />
          </RadioGroup>
        </FormControl>
      </CardContent>
    </Card>
  );
};

export default ThemeSelector; 