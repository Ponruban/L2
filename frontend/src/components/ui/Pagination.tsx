// Pagination Component
// Simple pagination component

import React from 'react';
import {
  Pagination as MuiPagination,
  PaginationItem,
  Box,
  Typography,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
} from '@mui/material';
import {
  FirstPage as FirstPageIcon,
  LastPage as LastPageIcon,
  NavigateBefore as NavigateBeforeIcon,
  NavigateNext as NavigateNextIcon,
} from '@mui/icons-material';

interface PaginationProps {
  page: number;
  totalPages: number;
  totalCount: number;
  rowsPerPage: number;
  rowsPerPageOptions?: number[];
  onPageChange: (page: number) => void;
  onRowsPerPageChange?: (rowsPerPage: number) => void;
  showRowsPerPage?: boolean;
  showTotalCount?: boolean;
  size?: 'small' | 'medium' | 'large';
  sx?: any;
}

const Pagination: React.FC<PaginationProps> = ({
  page,
  totalPages,
  totalCount,
  rowsPerPage,
  rowsPerPageOptions = [10, 25, 50, 100],
  onPageChange,
  onRowsPerPageChange,
  showRowsPerPage = true,
  showTotalCount = true,
  size = 'medium',
  sx,
}) => {
  const handlePageChange = (event: React.ChangeEvent<unknown>, newPage: number) => {
    onPageChange(newPage);
  };

  const handleRowsPerPageChange = (event: any) => {
    if (onRowsPerPageChange) {
      onRowsPerPageChange(event.target.value);
    }
  };

  const startItem = page * rowsPerPage + 1;
  const endItem = Math.min((page + 1) * rowsPerPage, totalCount);

  return (
    <Box
      sx={{
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        gap: 2,
        p: 2,
        ...sx,
      }}
    >
      {showTotalCount && (
        <Typography variant="body2" color="text.secondary">
          Showing {startItem} to {endItem} of {totalCount} results
        </Typography>
      )}

      <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
        {showRowsPerPage && onRowsPerPageChange && (
          <FormControl size="small" sx={{ minWidth: 120 }}>
            <InputLabel>Rows per page</InputLabel>
            <Select
              value={rowsPerPage}
              label="Rows per page"
              onChange={handleRowsPerPageChange}
            >
              {rowsPerPageOptions.map((option) => (
                <MenuItem key={option} value={option}>
                  {option}
                </MenuItem>
              ))}
            </Select>
          </FormControl>
        )}

        <MuiPagination
          page={page + 1}
          count={totalPages}
          onChange={handlePageChange}
          size={size}
          showFirstButton
          showLastButton
          renderItem={(item) => (
            <PaginationItem
              slots={{
                first: FirstPageIcon,
                last: LastPageIcon,
                previous: NavigateBeforeIcon,
                next: NavigateNextIcon,
              }}
              {...item}
            />
          )}
        />
      </Box>
    </Box>
  );
};

export default Pagination; 