import React, { useState } from 'react';
import {
  Box,
  Chip,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  OutlinedInput,
  Checkbox,
  ListItemText,
} from '@mui/material';

interface ReportFiltersProps {
  selectedProjects: string[];
  onProjectSelection: (projects: string[]) => void;
}

const ReportFilters: React.FC<ReportFiltersProps> = ({
  selectedProjects,
  onProjectSelection,
}) => {
  // Mock project data - in real app, this would come from API
  const projects = [
    { id: '1', name: 'Project Alpha' },
    { id: '2', name: 'Project Beta' },
    { id: '3', name: 'Project Gamma' },
    { id: '4', name: 'Project Delta' },
  ];

  const handleProjectChange = (event: any) => {
    const value = event.target.value;
    onProjectSelection(typeof value === 'string' ? value.split(',') : value);
  };

  return (
    <Box>
      <FormControl fullWidth>
        <InputLabel>Select Projects</InputLabel>
        <Select
          multiple
          value={selectedProjects}
          onChange={handleProjectChange}
          input={<OutlinedInput label="Select Projects" />}
          renderValue={(selected) => (
            <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 0.5 }}>
              {selected.map((value) => {
                const project = projects.find(p => p.id === value);
                return (
                  <Chip
                    key={value}
                    label={project?.name || value}
                    size="small"
                  />
                );
              })}
            </Box>
          )}
        >
          {projects.map((project) => (
            <MenuItem key={project.id} value={project.id}>
              <Checkbox checked={selectedProjects.indexOf(project.id) > -1} />
              <ListItemText primary={project.name} />
            </MenuItem>
          ))}
        </Select>
      </FormControl>
    </Box>
  );
};

export default ReportFilters; 