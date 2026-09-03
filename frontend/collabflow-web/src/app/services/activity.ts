import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ActivityLogModel {
  id: number;
  taskId: number;
  projectId: number;
  description: string;
  createdAt: string;
}

export interface CommentModel {
  id: number;
  taskId: number;
  authorId: number;
  content: string;
  createdAt: string;
}

export interface CreateCommentRequest {
  content: string;
  authorId: number;
}

@Injectable({
  providedIn: 'root'
})
export class Activity {
  private readonly baseUrl = 'http://localhost:8000/activity';

  constructor(private http: HttpClient) {}

  getActivityForTask(taskId: number): Observable<ActivityLogModel[]> {
    return this.http.get<ActivityLogModel[]>(`${this.baseUrl}/task/${taskId}`);
  }

  getCommentsForTask(taskId: number): Observable<CommentModel[]> {
    return this.http.get<CommentModel[]>(`${this.baseUrl}/task/${taskId}/comments`);
  }

  createComment(taskId: number, request: CreateCommentRequest): Observable<CommentModel> {
    return this.http.post<CommentModel>(`${this.baseUrl}/task/${taskId}/comments`, request);
  }
}