import React from 'react';
import { Skeleton as MuiSkeleton, Box, Card, CardContent } from '@mui/material';

interface SkeletonProps {
  variant?: 'text' | 'rectangular' | 'circular';
  width?: string | number;
  height?: string | number;
  animation?: 'pulse' | 'wave' | false;
  className?: string;
  sx?: React.ComponentProps<typeof MuiSkeleton>['sx'];
}

interface SkeletonCardProps {
  title?: boolean;
  content?: number;
  actions?: boolean;
  height?: string | number;
}

interface SkeletonListProps {
  count: number;
  itemHeight?: string | number;
  showAvatar?: boolean;
  showTitle?: boolean;
  showSubtitle?: boolean;
  showActions?: boolean;
}

export const Skeleton: React.FC<SkeletonProps> = ({
  variant = 'text',
  width = '100%',
  height,
  animation = 'pulse',
  className,
  sx,
}) => {
  return (
    <MuiSkeleton
      variant={variant}
      width={width}
      height={height}
      animation={animation}
      className={className}
      sx={sx}
    />
  );
};

export const SkeletonCard: React.FC<SkeletonCardProps> = ({
  title = true,
  content = 3,
  actions = true,
  height,
}) => {
  return (
    <Card sx={{ height, width: '100%' }}>
      <CardContent>
        {title && (
          <Box sx={{ mb: 2 }}>
            <Skeleton variant="text" width="60%" height={32} />
          </Box>
        )}
        
        {Array.from({ length: content }).map((_, index) => (
          <Box key={index} sx={{ mb: 1 }}>
            <Skeleton variant="text" width={`${80 - index * 10}%`} />
          </Box>
        ))}
        
        {actions && (
          <Box sx={{ mt: 2, display: 'flex', gap: 1 }}>
            <Skeleton variant="rectangular" width={80} height={36} />
            <Skeleton variant="rectangular" width={80} height={36} />
          </Box>
        )}
      </CardContent>
    </Card>
  );
};

export const SkeletonList: React.FC<SkeletonListProps> = ({
  count,
  itemHeight = 72,
  showAvatar = true,
  showTitle = true,
  showSubtitle = true,
  showActions = false,
}) => {
  return (
    <Box>
      {Array.from({ length: count }).map((_, index) => (
        <Box
          key={index}
          sx={{
            display: 'flex',
            alignItems: 'center',
            gap: 2,
            p: 2,
            borderBottom: index < count - 1 ? 1 : 0,
            borderColor: 'divider',
            height: itemHeight,
          }}
        >
          {showAvatar && (
            <Skeleton variant="circular" width={40} height={40} />
          )}
          
          <Box sx={{ flex: 1 }}>
            {showTitle && (
              <Skeleton variant="text" width="60%" height={24} />
            )}
            {showSubtitle && (
              <Skeleton variant="text" width="40%" height={20} />
            )}
          </Box>
          
          {showActions && (
            <Box sx={{ display: 'flex', gap: 1 }}>
              <Skeleton variant="rectangular" width={60} height={32} />
              <Skeleton variant="rectangular" width={60} height={32} />
            </Box>
          )}
        </Box>
      ))}
    </Box>
  );
};

export const SkeletonTable: React.FC<{
  rows: number;
  columns: number;
  showHeader?: boolean;
}> = ({ rows, columns, showHeader = true }) => {
  return (
    <Box>
      {showHeader && (
        <Box sx={{ display: 'flex', p: 2, borderBottom: 1, borderColor: 'divider' }}>
          {Array.from({ length: columns }).map((_, index) => (
            <Box key={index} sx={{ flex: 1, mr: 2 }}>
              <Skeleton variant="text" width="80%" height={24} />
            </Box>
          ))}
        </Box>
      )}
      
      {Array.from({ length: rows }).map((_, rowIndex) => (
        <Box
          key={rowIndex}
          sx={{
            display: 'flex',
            p: 2,
            borderBottom: rowIndex < rows - 1 ? 1 : 0,
            borderColor: 'divider',
          }}
        >
          {Array.from({ length: columns }).map((_, colIndex) => (
            <Box key={colIndex} sx={{ flex: 1, mr: 2 }}>
              <Skeleton variant="text" width={`${70 + Math.random() * 30}%`} />
            </Box>
          ))}
        </Box>
      ))}
    </Box>
  );
};

export const SkeletonGrid: React.FC<{
  items: number;
  columns?: number;
  itemHeight?: string | number;
}> = ({ items, columns = 3, itemHeight = 200 }) => {
  return (
    <Box
      sx={{
        display: 'grid',
        gridTemplateColumns: `repeat(${columns}, 1fr)`,
        gap: 2,
      }}
    >
      {Array.from({ length: items }).map((_, index) => (
        <SkeletonCard
          key={index}
          title={true}
          content={2}
          actions={true}
          height={itemHeight}
        />
      ))}
    </Box>
  );
};

export default Skeleton; 