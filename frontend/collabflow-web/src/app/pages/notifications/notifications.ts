import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Auth } from '../../services/auth';
import { Notification, NotificationModel } from '../../services/notification';

@Component({
  selector: 'app-notifications',
  imports: [CommonModule, RouterLink],
  templateUrl: './notifications.html',
  styleUrl: './notifications.scss',
})
export class Notifications implements OnInit {
  notifications = signal<NotificationModel[]>([]);
  loading = signal<boolean>(true);
  error = signal<string | null>(null);

  constructor(
    private auth: Auth,
    private notificationService: Notification
  ) {}

  ngOnInit(): void {
    this.auth.getCurrentUser().subscribe({
      next: (user) => {
        this.notificationService.getNotificationsForUser(user.id).subscribe({
          next: (notifications) => {
            this.notifications.set(notifications);
            this.loading.set(false);
          },
          error: (err) => {
            this.error.set('Failed to load notifications');
            this.loading.set(false);
            console.error(err);
          }
        });
      },
      error: (err) => {
        this.error.set('Failed to load user info');
        this.loading.set(false);
        console.error(err);
      }
    });
  }
}