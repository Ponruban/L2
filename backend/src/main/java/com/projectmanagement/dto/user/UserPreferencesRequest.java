package com.projectmanagement.dto.user;

import jakarta.validation.constraints.NotNull;

public class UserPreferencesRequest {
    
    @NotNull(message = "Theme preference is required")
    private String theme;
    
    @NotNull(message = "Notification preferences are required")
    private NotificationPreferences notifications;
    
    // Constructors
    public UserPreferencesRequest() {
    }
    
    public UserPreferencesRequest(String theme, NotificationPreferences notifications) {
        this.theme = theme;
        this.notifications = notifications;
    }
    
    // Getters and Setters
    public String getTheme() {
        return theme;
    }
    
    public void setTheme(String theme) {
        this.theme = theme;
    }
    
    public NotificationPreferences getNotifications() {
        return notifications;
    }
    
    public void setNotifications(NotificationPreferences notifications) {
        this.notifications = notifications;
    }
    
    // Inner class for notification preferences
    public static class NotificationPreferences {
        private boolean emailNotifications;
        private boolean pushNotifications;
        private boolean taskAssignments;
        private boolean projectUpdates;
        private boolean deadlineReminders;
        
        // Constructors
        public NotificationPreferences() {
        }
        
        public NotificationPreferences(boolean emailNotifications, boolean pushNotifications, 
                                     boolean taskAssignments, boolean projectUpdates, boolean deadlineReminders) {
            this.emailNotifications = emailNotifications;
            this.pushNotifications = pushNotifications;
            this.taskAssignments = taskAssignments;
            this.projectUpdates = projectUpdates;
            this.deadlineReminders = deadlineReminders;
        }
        
        // Getters and Setters
        public boolean isEmailNotifications() {
            return emailNotifications;
        }
        
        public void setEmailNotifications(boolean emailNotifications) {
            this.emailNotifications = emailNotifications;
        }
        
        public boolean isPushNotifications() {
            return pushNotifications;
        }
        
        public void setPushNotifications(boolean pushNotifications) {
            this.pushNotifications = pushNotifications;
        }
        
        public boolean isTaskAssignments() {
            return taskAssignments;
        }
        
        public void setTaskAssignments(boolean taskAssignments) {
            this.taskAssignments = taskAssignments;
        }
        
        public boolean isProjectUpdates() {
            return projectUpdates;
        }
        
        public void setProjectUpdates(boolean projectUpdates) {
            this.projectUpdates = projectUpdates;
        }
        
        public boolean isDeadlineReminders() {
            return deadlineReminders;
        }
        
        public void setDeadlineReminders(boolean deadlineReminders) {
            this.deadlineReminders = deadlineReminders;
        }
    }
} 