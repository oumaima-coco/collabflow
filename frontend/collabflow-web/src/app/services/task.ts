import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export type TaskStatus = 'TODO' | 'IN_PROGRESS' | 'DONE';

export interface TaskModel {
  id: number;
  title: string;
  description: string;
  status: TaskStatus;
  assigneeId: number | null;
  dueDate: string | null;
  projectId: number;
  createdAt: string;
}

export interface CreateTaskRequest {
  title: string;
  description?: string;
  assigneeId?: number;
  dueDate?: string;
}

export interface UpdateStatusRequest {
  status: TaskStatus;
}

export interface AssignTaskRequest {
  assigneeId: number;
}

@Injectable({
  providedIn: 'root'
})
export class Task {
  private readonly baseUrl = 'http://localhost:8000/projects';

  constructor(private http: HttpClient) {}

  createTask(projectId: number, request: CreateTaskRequest): Observable<TaskModel> {
    return this.http.post<TaskModel>(`${this.baseUrl}/${projectId}/tasks`, request);
  }

  getTasksForProject(projectId: number): Observable<TaskModel[]> {
    return this.http.get<TaskModel[]>(`${this.baseUrl}/${projectId}/tasks`);
  }

  updateStatus(taskId: number, status: TaskStatus): Observable<TaskModel> {
    return this.http.patch<TaskModel>(`${this.baseUrl}/tasks/${taskId}/status`, { status });
  }

  assignTask(taskId: number, assigneeId: number): Observable<TaskModel> {
    return this.http.patch<TaskModel>(`${this.baseUrl}/tasks/${taskId}/assign`, { assigneeId });
  }
}
