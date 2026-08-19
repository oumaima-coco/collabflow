import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface TeamModel {
  id: number;
  name: string;
  description: string;
  ownerId: number;
  createdAt: string;
}

export interface CreateTeamRequest {
  name: string;
  description: string;
  ownerId: number;
}

@Injectable({
  providedIn: 'root'
})
export class Team {
  private readonly baseUrl = 'http://localhost:8000/teams';

  constructor(private http: HttpClient) {}

  createTeam(request: CreateTeamRequest): Observable<TeamModel> {
    return this.http.post<TeamModel>(this.baseUrl, request);
  }

  getTeamsForUser(userId: number): Observable<TeamModel[]> {
    return this.http.get<TeamModel[]>(`${this.baseUrl}/user/${userId}`);
  }
}
