import { apiClient } from "./client";
import type { Task, TaskStatus } from "../types";

export interface TaskInput {
  title: string;
  description: string;
  status: TaskStatus;
}

export function fetchTasks(status?: TaskStatus | "", search?: string) {
  const params: Record<string, string> = {};
  if (status) params.status = status;
  if (search) params.search = search;

  return apiClient.get<Task[]>("/tasks", { params }).then((res) => res.data);
}

export function createTask(input: TaskInput) {
  return apiClient.post<Task>("/tasks", input).then((res) => res.data);
}

export function updateTask(id: number, input: TaskInput) {
  return apiClient.put<Task>(`/tasks/${id}`, input).then((res) => res.data);
}

export function deleteTask(id: number) {
  return apiClient.delete(`/tasks/${id}`);
}
