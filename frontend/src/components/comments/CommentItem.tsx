// CommentItem Component
import React from "react";
import { Box, Typography } from "@mui/material";
import { Button } from "@/components/ui";
import { Delete as DeleteIcon } from "@mui/icons-material";
import type { Comment } from "@/types";

interface CommentItemProps {
  comment: Comment;
  onDelete?: (commentId: number) => void;
}

const CommentItem: React.FC<CommentItemProps> = ({ comment, onDelete }) => {
  return (
    <Box sx={{ p: 2, border: "1px solid", borderColor: "divider", borderRadius: 1 }}>
      <Box sx={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start", mb: 1 }}>
        <Box sx={{ display: "flex", alignItems: "center" }}>
          <Box
            sx={{
              width: 24,
              height: 24,
              borderRadius: "50%",
              bgcolor: "primary.main",
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              color: "white",
              fontWeight: "bold",
              mr: 1,
            }}
          >
            {`User ${comment.userId}`.charAt(0)}
          </Box>
          <Typography variant="subtitle2">{`User ${comment.userId}`}</Typography>
        </Box>
        {onDelete && (
          <Button
            variant="outlined"
            size="small"
            startIcon={<DeleteIcon />}
            onClick={() => onDelete(comment.id)}
          >
            Delete
          </Button>
        )}
      </Box>
      <Typography variant="body2" sx={{ mb: 1 }}>
        {comment.content}
      </Typography>
      <Typography variant="caption" color="text.secondary">
        {new Date(comment.timestamp).toLocaleString()}
      </Typography>
    </Box>
  );
};

export default CommentItem;
