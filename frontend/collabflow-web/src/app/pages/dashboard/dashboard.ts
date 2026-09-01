import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Auth } from '../../services/auth';
import { Team, TeamModel } from '../../services/team';
import { Project, ProjectModel } from '../../services/project';
import { Notification, NotificationModel } from '../../services/notification';

@Component({
  selector: 'app-dashboard',
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss',
})
export class Dashboard implements OnInit {
  userId = signal<number | null>(null);
  userEmail = signal<string>('');
  teams = signal<TeamModel[]>([]);
  projects = signal<ProjectModel[]>([]);
  notifications = signal<NotificationModel[]>([]);
  loading = signal<boolean>(true);
  error = signal<string | null>(null);

  constructor(
    private auth: Auth,
    private teamService: Team,
    private projectService: Project,
    private notificationService: Notification
  ) {}

  ngOnInit(): void {
    this.auth.getCurrentUser().subscribe({
      next: (user) => {
        this.userId.set(user.id);
        this.userEmail.set(user.email);
        this.loadTeams(user.id);
        this.loadNotifications(user.id);
      },
      error: (err) => {
        this.error.set('Failed to load user info');
        this.loading.set(false);
        console.error(err);
      }
    });
  }

  loadTeams(userId: number): void {
    this.teamService.getTeamsForUser(userId).subscribe({
      next: (teams) => {
        this.teams.set(teams);
        if (teams.length > 0) {
          this.loadProjects(teams[0].id);
        } else {
          this.loading.set(false);
        }
      },
      error: (err) => {
        this.error.set('Failed to load teams');
        this.loading.set(false);
        console.error(err);
      }
    });
  }

  loadProjects(teamId: number): void {
    this.projectService.getProjectsForTeam(teamId).subscribe({
      next: (projects) => {
        this.projects.set(projects);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set('Failed to load projects');
        this.loading.set(false);
        console.error(err);
      }
    });
  }

  loadNotifications(userId: number): void {
    this.notificationService.getNotificationsForUser(userId).subscribe({
      next: (notifications) => {
        this.notifications.set(notifications);
      },
      error: (err) => {
        console.error('Failed to load notifications', err);
      }
    });
  }
}
