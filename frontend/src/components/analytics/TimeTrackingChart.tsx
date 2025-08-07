// TimeTrackingChart Component
import React from 'react';
import { Box, Typography, Paper } from '@mui/material';

interface TimeTrackingData {
  labels: string[];
  datasets: Array<{
    label: string;
    data: number[];
    backgroundColor: string;
    borderColor: string;
  }>;
}

interface TimeTrackingChartProps {
  data: TimeTrackingData;
}

const TimeTrackingChart: React.FC<TimeTrackingChartProps> = ({ data }) => {
  // Calculate max value for scaling
  const maxValue = Math.max(...data.datasets[0].data);
  const height = 200;

  return (
    <Box sx={{ width: '100%', height: height + 60 }}>
      {/* Chart Title */}
      <Typography variant="subtitle2" color="text.secondary" gutterBottom>
        Hours logged per day
      </Typography>
      
      {/* Chart Container */}
      <Box sx={{ position: 'relative', height, mt: 2 }}>
        {/* Y-axis labels */}
        <Box sx={{ position: 'absolute', left: 0, top: 0, bottom: 0, width: 40 }}>
          {[0, 2, 4, 6, 8, 10].map((value) => (
            <Box
              key={value}
              sx={{
                position: 'absolute',
                left: 0,
                top: height - (value / maxValue) * height,
                fontSize: '0.75rem',
                color: 'text.secondary',
                transform: 'translateY(-50%)',
              }}
            >
              {value}h
            </Box>
          ))}
        </Box>

        {/* Chart bars */}
        <Box sx={{ ml: 5, height: '100%', display: 'flex', alignItems: 'end', gap: 1 }}>
          {data.labels.map((label, index) => {
            const value = data.datasets[0].data[index];
            const barHeight = (value / maxValue) * height;
            
            return (
              <Box key={label} sx={{ flex: 1, display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
                {/* Bar */}
                <Box
                  sx={{
                    width: '80%',
                    height: barHeight,
                    backgroundColor: data.datasets[0].backgroundColor,
                    border: `2px solid ${data.datasets[0].borderColor}`,
                    borderRadius: '4px 4px 0 0',
                    transition: 'all 0.3s ease',
                    '&:hover': {
                      backgroundColor: data.datasets[0].borderColor,
                      transform: 'scaleY(1.05)',
                    },
                  }}
                />
                
                {/* Value label */}
                <Typography
                  variant="caption"
                  sx={{
                    mt: 0.5,
                    fontSize: '0.7rem',
                    fontWeight: 'bold',
                    color: 'text.primary',
                  }}
                >
                  {value}h
                </Typography>
                
                {/* Day label */}
                <Typography
                  variant="caption"
                  sx={{
                    mt: 0.5,
                    fontSize: '0.7rem',
                    color: 'text.secondary',
                  }}
                >
                  {label}
                </Typography>
              </Box>
            );
          })}
        </Box>

        {/* Grid lines */}
        <Box sx={{ position: 'absolute', top: 0, left: 45, right: 0, bottom: 0, pointerEvents: 'none' }}>
          {[0, 2, 4, 6, 8, 10].map((value) => (
            <Box
              key={value}
              sx={{
                position: 'absolute',
                left: 0,
                right: 0,
                top: height - (value / maxValue) * height,
                borderTop: '1px dashed',
                borderColor: 'divider',
              }}
            />
          ))}
        </Box>
      </Box>

      {/* Legend */}
      <Box sx={{ mt: 2, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
        <Box
          sx={{
            width: 12,
            height: 12,
            backgroundColor: data.datasets[0].backgroundColor,
            border: `1px solid ${data.datasets[0].borderColor}`,
            borderRadius: 1,
            mr: 1,
          }}
        />
        <Typography variant="caption" color="text.secondary">
          {data.datasets[0].label}
        </Typography>
      </Box>
    </Box>
  );
};

export default TimeTrackingChart; 
