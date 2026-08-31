import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ProjectModel {
  id: number;
  name: string;
  description: string;
  teamId: number;
  createdAt: string;
}

export interface CreateProjectRequest {
  name: string;
  description: string;
  teamId: number;
}

@Injectable({
  providedIn: 'root'
})
export class Project {
  private readonly baseUrl = 'http://localhost:8000/projects';

  constructor(private http: HttpClient) {}

  createProject(request: CreateProjectRequest): Observable<ProjectModel> {
    return this.http.post<ProjectModel>(this.baseUrl, request);
  }

  getProjectsForTeam(teamId: number): Observable<ProjectModel[]> {
    return this.http.get<ProjectModel[]>(`${this.baseUrl}/team/${teamId}`);
  }

  getProjectById(projectId: number): Observable<ProjectModel> {
    return this.http.get<ProjectModel>(`${this.baseUrl}/${projectId}`);
  }
}