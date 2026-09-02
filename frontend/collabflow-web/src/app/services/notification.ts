import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface NotificationModel {
  id: number;
  userId: number;
  message: string;
  read: boolean;
  createdAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class Notification {
  private readonly baseUrl = 'http://localhost:8000/notifications';

  constructor(private http: HttpClient) {}

  getNotificationsForUser(userId: number): Observable<NotificationModel[]> {
    return this.http.get<NotificationModel[]>(`${this.baseUrl}/user/${userId}`);
  }

  streamNotifications(userId: number): Observable<NotificationModel> {
    return new Observable<NotificationModel>((subscriber) => {
      const eventSource = new EventSource(`${this.baseUrl}/stream/${userId}`);

      eventSource.addEventListener('notification', (event: MessageEvent) => {
        const notification: NotificationModel = JSON.parse(event.data);
        subscriber.next(notification);
      });

      eventSource.onerror = (error) => {
        console.error('SSE connection error', error);
      };

      return () => {
        eventSource.close();
      };
    });
  }
}