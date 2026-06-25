import { Injectable, inject } from '@angular/core';
import { ApiService } from './api.service';
import { Observable } from 'rxjs';
import { Page } from './repo.service';

export interface UserProfile {
  id: string;
  username: string;
  email: string;
  displayName: string;
  avatarUrl: string;
  followersCount: number;
  followingCount: number;
  isFollowing: boolean;
  createdAt: string;
}

export interface UserSummary {
  id: string;
  username: string;
  avatarUrl: string;
}

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiService = inject(ApiService);

  getUserProfile(username: string): Observable<UserProfile> {
    return this.apiService.get<UserProfile>(`/users/${username}`);
  }

  followUser(username: string): Observable<void> {
    return this.apiService.post<void>(`/users/${username}/follow`, {});
  }

  unfollowUser(username: string): Observable<void> {
    return this.apiService.delete<void>(`/users/${username}/follow`);
  }

  getFollowers(username: string, page: number = 0, size: number = 20): Observable<Page<UserSummary>> {
    return this.apiService.get<Page<UserSummary>>(`/users/${username}/followers?page=${page}&size=${size}`);
  }

  getFollowing(username: string, page: number = 0, size: number = 20): Observable<Page<UserSummary>> {
    return this.apiService.get<Page<UserSummary>>(`/users/${username}/following?page=${page}&size=${size}`);
  }
}
