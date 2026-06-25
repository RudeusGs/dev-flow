import { Injectable, inject } from '@angular/core';
import { ApiService } from './api.service';
import { HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ContributionEvent {
  id: string;
  userId: string;
  eventType: 'COMMIT' | 'PULL_REQUEST' | 'ISSUE' | 'REVIEW';
  repositoryId: string;
  repositoryName: string;
  targetId: string;
  createdAt: string;
}

export interface ContributionDay {
  date: string;
  count: number;
}

export interface ContributionCalendar {
  userId: string;
  totalContributions: number;
  days: ContributionDay[];
}

@Injectable({
  providedIn: 'root'
})
export class ContributionService {
  private apiService = inject(ApiService);

  getCalendar(userId: string, from?: string, to?: string, includePrivate = false): Observable<ContributionCalendar> {
    let params = new HttpParams().set('includePrivate', includePrivate);
    if (from) params = params.set('from', from);
    if (to) params = params.set('to', to);
    return this.apiService.get<ContributionCalendar>(`/users/${userId}/contributions/calendar`, params);
  }

  getEvents(userId: string, from?: string, to?: string, includePrivate = false): Observable<ContributionEvent[]> {
    let params = new HttpParams().set('includePrivate', includePrivate);
    if (from) params = params.set('from', from);
    if (to) params = params.set('to', to);
    return this.apiService.get<ContributionEvent[]>(`/users/${userId}/contributions/events`, params);
  }
}
