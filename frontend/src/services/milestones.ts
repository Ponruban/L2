// Milestones Service
// API service functions for milestone operations

import api from './api';
import type { 
  Milestone, 
  CreateMilestoneRequest, 
  UpdateMilestoneRequest, 
  MilestoneFilters,
  MilestoneDetails,
  MilestoneStats,
  MilestoneAnalytics 
} from '@/types/milestone';

export const getProjectMilestones = async (projectId: number, filters?: MilestoneFilters) => {
  const params = new URLSearchParams();
  if (filters) {
    Object.entries(filters).forEach(([key, value]) => {
      if (value !== undefined && value !== null) {
        params.append(key, String(value));
      }
    });
  }
  
  const response = await api.get(`/projects/${projectId}/milestones?${params.toString()}`);
  return response.data;
};

export const getMilestone = async (milestoneId: number) => {
  const response = await api.get(`/milestones/${milestoneId}`);
  return response.data;
};

export const createMilestone = async (projectId: number, data: CreateMilestoneRequest) => {
  const response = await api.post(`/projects/${projectId}/milestones`, data);
  return response.data;
};

export const updateMilestone = async (milestoneId: number, data: UpdateMilestoneRequest) => {
  const response = await api.put(`/milestones/${milestoneId}`, data);
  return response.data;
};

export const deleteMilestone = async (milestoneId: number) => {
  const response = await api.delete(`/milestones/${milestoneId}`);
  return response.data;
};

export const getMilestoneDetails = async (milestoneId: number): Promise<MilestoneDetails> => {
  const response = await api.get(`/milestones/${milestoneId}/details`);
  return response.data;
};

export const getMilestoneStats = async (milestoneId: number): Promise<MilestoneStats> => {
  const response = await api.get(`/milestones/${milestoneId}/stats`);
  return response.data;
};

export const getMilestoneAnalytics = async (milestoneId: number): Promise<MilestoneAnalytics> => {
  const response = await api.get(`/milestones/${milestoneId}/analytics`);
  return response.data;
};

export const milestonesService = {
  getProjectMilestones,
  getMilestone,
  createMilestone,
  updateMilestone,
  deleteMilestone,
  getMilestoneDetails,
  getMilestoneStats,
  getMilestoneAnalytics,
}; 