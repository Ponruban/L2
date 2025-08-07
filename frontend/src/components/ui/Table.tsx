// Table Component
// Basic table component with sorting

import React, { useState } from 'react';
import {
  Table as MuiTable,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TableSortLabel,
  Paper,
  Box,
  IconButton,
  Tooltip,
} from '@mui/material';
import { Edit as EditIcon, Delete as DeleteIcon } from '@mui/icons-material';
import { LoadingSpinner, EmptyState } from './index';

interface TableColumn<T = any> {
  id: keyof T | string;
  label: string;
  align?: 'left' | 'right' | 'center';
  render?: (value: any, row: T) => React.ReactNode;
}

interface TableProps<T = any> {
  data: T[];
  columns: TableColumn<T>[];
  loading?: boolean;
  emptyMessage?: string;
  actions?: {
    edit?: boolean;
    delete?: boolean;
  };
  onAction?: (action: string, row: T) => void;
  sx?: any;
}

const Table = <T extends Record<string, any>>({
  data,
  columns,
  loading = false,
  emptyMessage = 'No data available',
  actions,
  onAction,
  sx,
}: TableProps<T>) => {
  const [sortColumn, setSortColumn] = useState<keyof T | string | null>(null);
  const [sortOrder, setSortOrder] = useState<'asc' | 'desc'>('asc');

  const handleSort = (column: keyof T | string) => {
    const isAsc = sortColumn === column && sortOrder === 'asc';
    setSortColumn(column);
    setSortOrder(isAsc ? 'desc' : 'asc');
  };

  const sortedData = React.useMemo(() => {
    if (!sortColumn) return data;

    return [...data].sort((a, b) => {
      const aValue = a[sortColumn];
      const bValue = b[sortColumn];

      if (aValue < bValue) return sortOrder === 'asc' ? -1 : 1;
      if (aValue > bValue) return sortOrder === 'asc' ? 1 : -1;
      return 0;
    });
  }, [data, sortColumn, sortOrder]);

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
    <Paper sx={{ width: '100%', overflow: 'hidden', ...sx }}>
      <TableContainer>
        <MuiTable>
          <TableHead>
            <TableRow>
              {columns.map((column) => (
                <TableCell key={String(column.id)} align={column.align || 'left'}>
                  <TableSortLabel
                    active={sortColumn === column.id}
                    direction={sortColumn === column.id ? sortOrder : 'asc'}
                    onClick={() => handleSort(column.id)}
                  >
                    {column.label}
                  </TableSortLabel>
                </TableCell>
              ))}
              {actions && <TableCell align="center">Actions</TableCell>}
            </TableRow>
          </TableHead>
          <TableBody>
            {sortedData.map((row, index) => (
              <TableRow key={index} hover>
                {columns.map((column) => (
                  <TableCell key={String(column.id)} align={column.align || 'left'}>
                    {column.render ? column.render(row[column.id], row) : row[column.id]}
                  </TableCell>
                ))}
                {actions && (
                  <TableCell align="center">
                    <Box sx={{ display: 'flex', gap: 0.5, justifyContent: 'center' }}>
                      {actions.edit && (
                        <Tooltip title="Edit">
                          <IconButton
                            size="small"
                            onClick={() => onAction?.('edit', row)}
                          >
                            <EditIcon fontSize="small" />
                          </IconButton>
                        </Tooltip>
                      )}
                      {actions.delete && (
                        <Tooltip title="Delete">
                          <IconButton
                            size="small"
                            color="error"
                            onClick={() => onAction?.('delete', row)}
                          >
                            <DeleteIcon fontSize="small" />
                          </IconButton>
                        </Tooltip>
                      )}
                    </Box>
                  </TableCell>
                )}
              </TableRow>
            ))}
          </TableBody>
        </MuiTable>
      </TableContainer>
    </Paper>
  );
};

export default Table; 