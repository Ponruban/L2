// ProjectMilestones Component
import React from "react";
import { Box, Typography, Grid } from "@mui/material";
import { Card, StatusBadge } from "@/components/ui";
import type { Milestone } from "@/types";

interface ProjectMilestonesProps {
  milestones: Milestone[];
}

const ProjectMilestones: React.FC<ProjectMilestonesProps> = ({ milestones }) => {
  return (
    <Card sx={{ p: 3 }}>
      <Typography variant="h6" sx={{ mb: 2 }}>
        Milestones ({milestones.length})
      </Typography>
      <Grid container spacing={2}>
        {milestones.map((milestone) => (
          <Grid item xs={12} sm={6} md={4} key={milestone.id}>
            <Box sx={{ p: 2, border: "1px solid", borderColor: "divider", borderRadius: 1 }}>
              <Typography variant="subtitle2" sx={{ mb: 1 }}>
                {milestone.name}
              </Typography>
              <Typography variant="caption" color="text.secondary" sx={{ mb: 1, display: "block" }}>
                {new Date(milestone.dueDate).toLocaleDateString()}
              </Typography>
              <StatusBadge status={milestone.status} size="small" />
            </Box>
          </Grid>
        ))}
      </Grid>
    </Card>
  );
};

export default ProjectMilestones;
