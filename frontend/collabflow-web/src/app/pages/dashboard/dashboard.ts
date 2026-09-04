import { Component, OnInit, OnDestroy, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { Subscription } from 'rxjs';
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
export class Dashboard implements OnInit, OnDestroy {
  userId = signal<number | null>(null);
  userEmail = signal<string>('');
  teams = signal<TeamModel[]>([]);
  projects = signal<ProjectModel[]>([]);
  notifications = signal<NotificationModel[]>([]);
  loading = signal<boolean>(true);
  error = signal<string | null>(null);
  showNotifications = signal<boolean>(false);

  unreadCount = computed(() => this.notifications().filter(n => !n.read).length);

  private notificationStreamSubscription: Subscription | null = null;

  constructor(
    private auth: Auth,
    private teamService: Team,
    private projectService: Project,
    private notificationService: Notification,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.auth.getCurrentUser().subscribe({
      next: (user) => {
        this.userId.set(user.id);
        this.userEmail.set(user.email);
        this.loadTeams(user.id);
        this.loadNotifications(user.id);
        this.subscribeToLiveNotifications(user.id);
      },
      error: (err) => {
        this.error.set('Failed to load user info');
        this.loading.set(false);
        console.error(err);
      }
    });
  }

  ngOnDestroy(): void {
    this.notificationStreamSubscription?.unsubscribe();
  }

  logout(): void {
    this.auth.logout();
    this.router.navigate(['/login']);
  }

  toggleNotifications(): void {
    const opening = !this.showNotifications();
    this.showNotifications.set(opening);

    if (opening && this.unreadCount() > 0) {
      const userId = this.userId();
      if (userId) {
        this.notificationService.markAllAsRead(userId).subscribe({
          next: () => {
            this.notifications.update(current => current.map(n => ({ ...n, read: true })));
          },
          error: (err) => console.error('Failed to mark notifications as read', err)
        });
      }
    }
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

  subscribeToLiveNotifications(userId: number): void {
    this.notificationStreamSubscription = this.notificationService.streamNotifications(userId).subscribe({
      next: (notification) => {
        this.notifications.update(current => [notification, ...current]);
      },
      error: (err) => {
        console.error('Notification stream error', err);
      }
    });
  }
}