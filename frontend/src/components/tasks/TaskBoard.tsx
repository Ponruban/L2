// TaskBoard Component
import React from "react";
import { Box, Typography, Grid } from "@mui/material";
import { Card, Button } from "@/components/ui";
import { Add as AddIcon } from "@mui/icons-material";
import type { Task } from "@/types";

interface TaskBoardProps {
  tasks: Task[];
  onAddTask: () => void;
}

const TaskBoard: React.FC<TaskBoardProps> = ({ tasks, onAddTask }) => {
  return (
    <Box sx={{ p: 3 }}>
      <Box sx={{ display: "flex", alignItems: "center", mb: 3 }}>
        <Typography variant="h4" sx={{ flexGrow: 1 }}>
          Task Board
        </Typography>
        <Button variant="primary" startIcon={<AddIcon />} onClick={onAddTask}>
          Add Task
        </Button>
      </Box>

      <Grid container spacing={3}>
        <Grid item xs={12} md={4}>
          <Card sx={{ p: 2 }}>
            <Typography variant="h6" sx={{ mb: 2 }}>
              To Do ({tasks.filter(t => t.status === "TODO").length})
            </Typography>
            {tasks.filter(t => t.status === "TODO").map((task) => (
              <Box key={task.id} sx={{ mb: 2, p: 2, border: "1px solid", borderColor: "divider", borderRadius: 1 }}>
                <Typography variant="subtitle2">{task.title}</Typography>
                <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
                  {task.description}
                </Typography>
                <Typography variant="caption" color="text.secondary">
                  Assigned to: {`User ${task.assigneeId}`?.name}
                </Typography>
              </Box>
            ))}
          </Card>
        </Grid>

        <Grid item xs={12} md={4}>
          <Card sx={{ p: 2 }}>
            <Typography variant="h6" sx={{ mb: 2 }}>
              In Progress ({tasks.filter(t => t.status === "IN_PROGRESS").length})
            </Typography>
            {tasks.filter(t => t.status === "IN_PROGRESS").map((task) => (
              <Box key={task.id} sx={{ mb: 2, p: 2, border: "1px solid", borderColor: "divider", borderRadius: 1 }}>
                <Typography variant="subtitle2">{task.title}</Typography>
                <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
                  {task.description}
                </Typography>
                <Typography variant="caption" color="text.secondary">
                  Assigned to: {`User ${task.assigneeId}`?.name}
                </Typography>
              </Box>
            ))}
          </Card>
        </Grid>

        <Grid item xs={12} md={4}>
          <Card sx={{ p: 2 }}>
            <Typography variant="h6" sx={{ mb: 2 }}>
              Completed ({tasks.filter(t => t.status === "DONE").length})
            </Typography>
            {tasks.filter(t => t.status === "DONE").map((task) => (
              <Box key={task.id} sx={{ mb: 2, p: 2, border: "1px solid", borderColor: "divider", borderRadius: 1 }}>
                <Typography variant="subtitle2">{task.title}</Typography>
                <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
                  {task.description}
                </Typography>
                <Typography variant="caption" color="text.secondary">
                  Assigned to: {`User ${task.assigneeId}`?.name}
                </Typography>
              </Box>
            ))}
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
};

export default TaskBoard;
