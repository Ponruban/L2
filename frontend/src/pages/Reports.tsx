import React, { useState } from 'react';
import {
  Box,
  Typography,
  Card,
  CardContent,
  Button,
  Grid,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Chip,
  Alert,
  CircularProgress,
} from '@mui/material';
import {
  PictureAsPdf as PdfIcon,
  TableChart as ExcelIcon,
  Assessment as ReportIcon,
} from '@mui/icons-material';

interface ReportType {
  id: string;
  name: string;
  description: string;
}

interface ReportData {
  id: string;
  title: string;
  generatedAt: string;
  type: string;
  status: 'completed' | 'processing' | 'failed';
}

const Reports: React.FC = () => {
  const [selectedReportType, setSelectedReportType] = useState<string>('');
  const [dateRange, setDateRange] = useState<string>('30d');
  const [isGenerating, setIsGenerating] = useState(false);
  const [reports, setReports] = useState<ReportData[]>([]);

  const reportTypes: ReportType[] = [
    {
      id: 'time-tracking',
      name: 'Time Tracking Report',
      description: 'Detailed time tracking data with hours logged per user and task',
    },
    {
      id: 'task-completion',
      name: 'Task Completion Report',
      description: 'Task completion statistics and performance metrics',
    },
    {
      id: 'project-progress',
      name: 'Project Progress Report',
      description: 'Project progress overview with milestones and deadlines',
    },
    {
      id: 'user-performance',
      name: 'User Performance Report',
      description: 'Individual user performance and productivity metrics',
    },
  ];

  const handleReportTypeChange = (event: any) => {
    setSelectedReportType(event.target.value);
  };

  const handleDateRangeChange = (event: any) => {
    setDateRange(event.target.value);
  };

  const generateReport = async () => {
    if (!selectedReportType) return;

    setIsGenerating(true);

    try {
      await new Promise(resolve => setTimeout(resolve, 2000));

      const newReport: ReportData = {
        id: Date.now().toString(),
        title: `${reportTypes.find(r => r.id === selectedReportType)?.name} - ${new Date().toLocaleDateString()}`,
        generatedAt: new Date().toISOString(),
        type: selectedReportType,
        status: 'completed',
      };

      setReports(prev => [newReport, ...prev]);
    } catch (error) {
      console.error('Error generating report:', error);
    } finally {
      setIsGenerating(false);
    }
  };

  const downloadReport = (report: ReportData, format: 'pdf' | 'excel') => {

    // In real implementation, this would trigger actual file download
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'completed': return 'success';
      case 'processing': return 'warning';
      case 'failed': return 'error';
      default: return 'default';
    }
  };

  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h4" component="h1" gutterBottom>
        Reports
      </Typography>
      <Typography variant="body1" color="text.secondary" sx={{ mb: 3 }}>
        Generate and export detailed reports for time tracking, task completion, and performance metrics
      </Typography>

      <Grid container spacing={3}>
        <Grid item xs={12} lg={8}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Generate Report
              </Typography>

              <Grid container spacing={2}>
                <Grid item xs={12} md={6}>
                  <FormControl fullWidth>
                    <InputLabel>Report Type</InputLabel>
                    <Select
                      value={selectedReportType}
                      label="Report Type"
                      onChange={handleReportTypeChange}
                    >
                      {reportTypes.map((type) => (
                        <MenuItem key={type.id} value={type.id}>
                          {type.name}
                        </MenuItem>
                      ))}
                    </Select>
                  </FormControl>
                </Grid>

                <Grid item xs={12} md={6}>
                  <FormControl fullWidth>
                    <InputLabel>Date Range</InputLabel>
                    <Select
                      value={dateRange}
                      label="Date Range"
                      onChange={handleDateRangeChange}
                    >
                      <MenuItem value="7d">Last 7 Days</MenuItem>
                      <MenuItem value="30d">Last 30 Days</MenuItem>
                      <MenuItem value="90d">Last 90 Days</MenuItem>
                      <MenuItem value="1y">Last Year</MenuItem>
                    </Select>
                  </FormControl>
                </Grid>
              </Grid>

              <Box sx={{ mt: 3 }}>
                <Button
                  variant="contained"
                  startIcon={isGenerating ? <CircularProgress size={20} /> : <ReportIcon />}
                  onClick={generateReport}
                  disabled={!selectedReportType || isGenerating}
                  size="large"
                >
                  {isGenerating ? 'Generating...' : 'Generate Report'}
                </Button>
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} lg={4}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Available Reports
              </Typography>
              <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                {reportTypes.map((type) => (
                  <Box
                    key={type.id}
                    sx={{
                      p: 2,
                      border: '1px solid',
                      borderColor: selectedReportType === type.id ? 'primary.main' : 'divider',
                      borderRadius: 1,
                      backgroundColor: selectedReportType === type.id ? 'primary.50' : 'background.paper',
                    }}
                  >
                    <Typography variant="subtitle2" gutterBottom>
                      {type.name}
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      {type.description}
                    </Typography>
                  </Box>
                ))}
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {reports.length > 0 && (
        <Box sx={{ mt: 3 }}>
          <Typography variant="h6" gutterBottom>
            Generated Reports
          </Typography>
          <Grid container spacing={2}>
            {reports.map((report) => (
              <Grid item xs={12} md={6} lg={4} key={report.id}>
                <Card>
                  <CardContent>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', mb: 2 }}>
                      <Typography variant="subtitle1" sx={{ fontWeight: 'bold' }}>
                        {report.title}
                      </Typography>
                      <Chip
                        label={report.status}
                        color={getStatusColor(report.status) as any}
                        size="small"
                      />
                    </Box>
                    
                    <Typography variant="body2" color="text.secondary" gutterBottom>
                      Generated: {new Date(report.generatedAt).toLocaleString()}
                    </Typography>

                    {report.status === 'completed' && (
                      <Box sx={{ display: 'flex', gap: 1, mt: 2 }}>
                        <Button
                          size="small"
                          startIcon={<PdfIcon />}
                          onClick={() => downloadReport(report, 'pdf')}
                          variant="outlined"
                        >
                          PDF
                        </Button>
                        <Button
                          size="small"
                          startIcon={<ExcelIcon />}
                          onClick={() => downloadReport(report, 'excel')}
                          variant="outlined"
                        >
                          Excel
                        </Button>
                      </Box>
                    )}

                    {report.status === 'processing' && (
                      <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mt: 2 }}>
                        <CircularProgress size={16} />
                        <Typography variant="body2" color="text.secondary">
                          Processing...
                        </Typography>
                      </Box>
                    )}

                    {report.status === 'failed' && (
                      <Alert severity="error" sx={{ mt: 2 }}>
                        Report generation failed. Please try again.
                      </Alert>
                    )}
                  </CardContent>
                </Card>
              </Grid>
            ))}
          </Grid>
        </Box>
      )}
    </Box>
  );
};

export default Reports;