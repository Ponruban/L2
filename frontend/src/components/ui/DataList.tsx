// DataList Component
// Simple list component for displaying data

import React from 'react';
import {
  List,
  ListItem,
  ListItemText,
  ListItemSecondaryAction,
  IconButton,
  Paper,
  Box,
  Typography,
  Divider,
} from '@mui/material';
import {
  Edit as EditIcon,
  Delete as DeleteIcon,
  Visibility as ViewIcon,
} from '@mui/icons-material';
import { LoadingSpinner, EmptyState } from './index';

interface DataListProps<T = any> {
  data: T[];
  renderItem: (item: T, index: number) => React.ReactNode;
  loading?: boolean;
  emptyMessage?: string;
  actions?: {
    view?: boolean;
    edit?: boolean;
    delete?: boolean;
  };
  onAction?: (action: string, item: T) => void;
  dense?: boolean;
  sx?: any;
}

const DataList = <T extends Record<string, any>>({
  data,
  renderItem,
  loading = false,
  emptyMessage = 'No data available',
  actions,
  onAction,
  dense = false,
  sx,
}: DataListProps<T>) => {
  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', p: 4 }}>
        <LoadingSpinner />
      </Box>
    );
  }

  if (!data || data.length === 0) {
    return <EmptyState title="No Data" message={emptyMessage} />;
  }

  return (
    <Paper sx={{ ...sx }}>
      <List dense={dense}>
        {data.map((item, index) => (
          <React.Fragment key={index}>
            <ListItem>
              <ListItemText
                primary={renderItem(item, index)}
              />
              {actions && (
                <ListItemSecondaryAction>
                  <Box sx={{ display: 'flex', gap: 0.5 }}>
                    {actions.view && (
                      <IconButton
                        edge="end"
                        size="small"
                        onClick={() => onAction?.('view', item)}
                      >
                        <ViewIcon fontSize="small" />
                      </IconButton>
                    )}
                    {actions.edit && (
                      <IconButton
                        edge="end"
                        size="small"
                        onClick={() => onAction?.('edit', item)}
                      >
                        <EditIcon fontSize="small" />
                      </IconButton>
                    )}
                    {actions.delete && (
                      <IconButton
                        edge="end"
                        size="small"
                        color="error"
                        onClick={() => onAction?.('delete', item)}
                      >
                        <DeleteIcon fontSize="small" />
                      </IconButton>
                    )}
                  </Box>
                </ListItemSecondaryAction>
              )}
            </ListItem>
            {index < data.length - 1 && <Divider />}
          </React.Fragment>
        ))}
      </List>
    </Paper>
  );
};

export default DataList; 