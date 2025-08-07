package com.projectmanagement.dto.analytics;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for general analytics data response
 */
public class AnalyticsDataResponse {
    private TimeTrackingData timeTracking;
    private TaskCompletionData taskCompletion;
    private PerformanceData performance;

    public AnalyticsDataResponse() {}

    public AnalyticsDataResponse(TimeTrackingData timeTracking, TaskCompletionData taskCompletion, PerformanceData performance) {
        this.timeTracking = timeTracking;
        this.taskCompletion = taskCompletion;
        this.performance = performance;
    }

    // Getters and Setters
    public TimeTrackingData getTimeTracking() {
        return timeTracking;
    }

    public void setTimeTracking(TimeTrackingData timeTracking) {
        this.timeTracking = timeTracking;
    }

    public TaskCompletionData getTaskCompletion() {
        return taskCompletion;
    }

    public void setTaskCompletion(TaskCompletionData taskCompletion) {
        this.taskCompletion = taskCompletion;
    }

    public PerformanceData getPerformance() {
        return performance;
    }

    public void setPerformance(PerformanceData performance) {
        this.performance = performance;
    }

    /**
     * Time tracking data structure
     */
    public static class TimeTrackingData {
        private List<String> labels;
        private List<Dataset> datasets;

        public TimeTrackingData() {}

        public TimeTrackingData(List<String> labels, List<Dataset> datasets) {
            this.labels = labels;
            this.datasets = datasets;
        }

        public List<String> getLabels() {
            return labels;
        }

        public void setLabels(List<String> labels) {
            this.labels = labels;
        }

        public List<Dataset> getDatasets() {
            return datasets;
        }

        public void setDatasets(List<Dataset> datasets) {
            this.datasets = datasets;
        }
    }

    /**
     * Task completion data structure
     */
    public static class TaskCompletionData {
        private List<String> labels;
        private List<Dataset> datasets;

        public TaskCompletionData() {}

        public TaskCompletionData(List<String> labels, List<Dataset> datasets) {
            this.labels = labels;
            this.datasets = datasets;
        }

        public List<String> getLabels() {
            return labels;
        }

        public void setLabels(List<String> labels) {
            this.labels = labels;
        }

        public List<Dataset> getDatasets() {
            return datasets;
        }

        public void setDatasets(List<Dataset> datasets) {
            this.datasets = datasets;
        }
    }

    /**
     * Performance data structure
     */
    public static class PerformanceData {
        private BigDecimal totalHours;
        private int completedTasks;
        private BigDecimal averageCompletionTime;
        private BigDecimal productivityScore;

        public PerformanceData() {}

        public PerformanceData(BigDecimal totalHours, int completedTasks, BigDecimal averageCompletionTime, BigDecimal productivityScore) {
            this.totalHours = totalHours;
            this.completedTasks = completedTasks;
            this.averageCompletionTime = averageCompletionTime;
            this.productivityScore = productivityScore;
        }

        public BigDecimal getTotalHours() {
            return totalHours;
        }

        public void setTotalHours(BigDecimal totalHours) {
            this.totalHours = totalHours;
        }

        public int getCompletedTasks() {
            return completedTasks;
        }

        public void setCompletedTasks(int completedTasks) {
            this.completedTasks = completedTasks;
        }

        public BigDecimal getAverageCompletionTime() {
            return averageCompletionTime;
        }

        public void setAverageCompletionTime(BigDecimal averageCompletionTime) {
            this.averageCompletionTime = averageCompletionTime;
        }

        public BigDecimal getProductivityScore() {
            return productivityScore;
        }

        public void setProductivityScore(BigDecimal productivityScore) {
            this.productivityScore = productivityScore;
        }
    }

    /**
     * Dataset structure for charts
     */
    public static class Dataset {
        private String label;
        private List<Number> data;
        private List<String> backgroundColor;
        private List<String> borderColor;

        public Dataset() {}

        public Dataset(String label, List<Number> data, List<String> backgroundColor, List<String> borderColor) {
            this.label = label;
            this.data = data;
            this.backgroundColor = backgroundColor;
            this.borderColor = borderColor;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public List<Number> getData() {
            return data;
        }

        public void setData(List<Number> data) {
            this.data = data;
        }

        public List<String> getBackgroundColor() {
            return backgroundColor;
        }

        public void setBackgroundColor(List<String> backgroundColor) {
            this.backgroundColor = backgroundColor;
        }

        public List<String> getBorderColor() {
            return borderColor;
        }

        public void setBorderColor(List<String> borderColor) {
            this.borderColor = borderColor;
        }
    }
} 