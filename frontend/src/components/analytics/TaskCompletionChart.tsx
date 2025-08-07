// TaskCompletionChart Component
import React from 'react';
import { Box, Typography } from '@mui/material';

interface TaskCompletionData {
  labels: string[];
  datasets: Array<{
    label: string;
    data: number[];
    backgroundColor: string | string[];
    borderColor: string | string[];
  }>;
}

interface TaskCompletionChartProps {
  data: TaskCompletionData;
}

const TaskCompletionChart: React.FC<TaskCompletionChartProps> = ({ data }) => {
  const total = data.datasets[0].data.reduce((sum, value) => sum + value, 0);
  const colors = Array.isArray(data.datasets[0].backgroundColor) 
    ? data.datasets[0].backgroundColor 
    : [data.datasets[0].backgroundColor];

  return (
    <Box sx={{ width: '100%' }}>
      {/* Chart Title */}
      <Typography variant="subtitle2" color="text.secondary" gutterBottom>
        Task status distribution
      </Typography>
      
      {/* Pie Chart */}
      <Box sx={{ position: 'relative', width: '100%', height: 200, mt: 2 }}>
        {/* Center total */}
        <Box
          sx={{
            position: 'absolute',
            top: '50%',
            left: '50%',
            transform: 'translate(-50%, -50%)',
            textAlign: 'center',
            zIndex: 2,
          }}
        >
          <Typography variant="h4" component="div" color="primary">
            {total}
          </Typography>
          <Typography variant="caption" color="text.secondary">
            Total Tasks
          </Typography>
        </Box>

        {/* Pie segments */}
        <Box sx={{ position: 'relative', width: '100%', height: '100%' }}>
          {data.labels.map((label, index) => {
            const value = data.datasets[0].data[index];
            const percentage = (value / total) * 100;
            const color = colors[index % colors.length];
            
            // Calculate angles for pie segment
            const startAngle = data.datasets[0].data
              .slice(0, index)
              .reduce((sum, val) => sum + (val / total) * 360, 0);
            const endAngle = startAngle + (value / total) * 360;
            
            // Convert angles to radians
            const startRad = (startAngle - 90) * (Math.PI / 180);
            const endRad = (endAngle - 90) * (Math.PI / 180);
            
            // Calculate path for pie segment
            const radius = 80;
            const x1 = 100 + radius * Math.cos(startRad);
            const y1 = 100 + radius * Math.sin(startRad);
            const x2 = 100 + radius * Math.cos(endRad);
            const y2 = 100 + radius * Math.sin(endRad);
            
            const largeArcFlag = endAngle - startAngle > 180 ? 1 : 0;
            
            const pathData = [
              `M 100 100`,
              `L ${x1} ${y1}`,
              `A ${radius} ${radius} 0 ${largeArcFlag} 1 ${x2} ${y2}`,
              'Z'
            ].join(' ');

            return (
              <svg
                key={label}
                style={{
                  position: 'absolute',
                  top: 0,
                  left: 0,
                  width: '100%',
                  height: '100%',
                  zIndex: 1,
                }}
              >
                <path
                  d={pathData}
                  fill={color}
                  stroke="#fff"
                  strokeWidth="2"
                  style={{
                    transition: 'all 0.3s ease',
                    cursor: 'pointer',
                  }}
                  onMouseEnter={(e) => {
                    e.currentTarget.style.transform = 'scale(1.05)';
                  }}
                  onMouseLeave={(e) => {
                    e.currentTarget.style.transform = 'scale(1)';
                  }}
                />
              </svg>
            );
          })}
        </Box>
      </Box>

      {/* Legend */}
      <Box sx={{ mt: 3 }}>
        {data.labels.map((label, index) => {
          const value = data.datasets[0].data[index];
          const percentage = ((value / total) * 100).toFixed(1);
          const color = colors[index % colors.length];
          
          return (
            <Box
              key={label}
              sx={{
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'space-between',
                mb: 1,
                p: 1,
                borderRadius: 1,
                '&:hover': {
                  backgroundColor: 'action.hover',
                },
              }}
            >
              <Box sx={{ display: 'flex', alignItems: 'center' }}>
                <Box
                  sx={{
                    width: 12,
                    height: 12,
                    backgroundColor: color,
                    borderRadius: '50%',
                    mr: 1,
                  }}
                />
                <Typography variant="body2" sx={{ fontWeight: 500 }}>
                  {label}
                </Typography>
              </Box>
              <Box sx={{ textAlign: 'right' }}>
                <Typography variant="body2" sx={{ fontWeight: 'bold' }}>
                  {value}
                </Typography>
                <Typography variant="caption" color="text.secondary">
                  {percentage}%
                </Typography>
              </Box>
            </Box>
          );
        })}
      </Box>
    </Box>
  );
};

export default TaskCompletionChart; 