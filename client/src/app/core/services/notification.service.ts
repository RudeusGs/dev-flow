import { Injectable, inject } from '@angular/core';
import { ApiService } from './api.service';
import { HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Page } from './repo.service';

export interface Notification {
  id: string;
  userId: string;
  type: string;
  title: string;
  content: string;
  referenceUrl: string;
  isRead: boolean;
  createdAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private apiService = inject(ApiService);

  getNotifications(unreadOnly = false, page = 0, size = 10): Observable<Page<Notification>> {
    const params = new HttpParams().set('unreadOnly', unreadOnly).set('page', page).set('size', size);
    return this.apiService.get<Page<Notification>>('/notifications', params);
  }

  markAsRead(notificationId: string): Observable<void> {
    return this.apiService.patch<void>(`/notifications/${notificationId}/read`, {});
  }
}
