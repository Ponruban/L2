// DataTable Component
// Simple table component for displaying data

import React from 'react';
import {
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Box,
} from '@mui/material';
import { LoadingSpinner, EmptyState } from './index';

interface Column<T = any> {
  id: keyof T | string;
  label: string;
  render?: (value: any, row: T) => React.ReactNode;
}

interface DataTableProps<T = any> {
  data: T[];
  columns: Column<T>[];
  loading?: boolean;
  emptyMessage?: string;
}

const DataTable = <T extends Record<string, any>>({
  data,
  columns,
  loading = false,
  emptyMessage = 'No data available',
}: DataTableProps<T>) => {
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
    <TableContainer component={Paper}>
      <Table>
        <TableHead>
          <TableRow>
            {columns.map((column) => (
              <TableCell key={String(column.id)}>
                {column.label}
              </TableCell>
            ))}
          </TableRow>
        </TableHead>
        <TableBody>
          {data.map((row, index) => (
            <TableRow key={index}>
              {columns.map((column) => (
                <TableCell key={String(column.id)}>
                  {column.render 
                    ? column.render(row[column.id], row)
                    : row[column.id]
                  }
                </TableCell>
              ))}
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
};

export default DataTable; 