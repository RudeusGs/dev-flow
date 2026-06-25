import { Injectable, inject } from '@angular/core';
import { ApiService } from './api.service';
import { HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Repository {
  id: string;
  name: string;
  slug: string;
  description: string;
  visibility: 'PUBLIC' | 'PRIVATE';
  defaultBranchName: string;
  starsCount: number;
  forksCount?: number;
  isStarred: boolean;
  isWatched?: boolean;
  watchLevel?: string;
  ownerType: 'USER' | 'ORGANIZATION';
  ownerId: string;
  ownerUsername: string;
  createdAt: string;
  updatedAt: string;
}

export interface RepositoryRelease {
  id: string;
  name: string;
  tagName: string;
  body: string;
  isPreRelease: boolean;
  authorUsername: string;
  createdAt: string;
  publishedAt: string;
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

@Injectable({
  providedIn: 'root'
})
export class RepoService {
  private apiService = inject(ApiService);

  getPublicRepositories(page: number = 0, size: number = 10): Observable<Page<Repository>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.apiService.get<Page<Repository>>('/repos', params);
  }

  getUserRepositories(username: string, page: number = 0, size: number = 10): Observable<Page<Repository>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.apiService.get<Page<Repository>>(`/repos/users/${username}`, params);
  }

  getRepository(ownerUsername: string, repoName: string): Observable<Repository> {
    return this.apiService.get<Repository>(`/repos/${ownerUsername}/${repoName}`);
  }

  createRepository(data: { name: string; description: string; visibility: 'PUBLIC' | 'PRIVATE' }): Observable<Repository> {
    return this.apiService.post<Repository>('/repos', data);
  }

  deleteRepository(ownerUsername: string, repoName: string): Observable<void> {
    return this.apiService.delete<void>(`/repos/${ownerUsername}/${repoName}`);
  }

  starRepository(ownerUsername: string, repoName: string): Observable<void> {
    return this.apiService.post<void>(`/repos/${ownerUsername}/${repoName}/star`, {});
  }

  unstarRepository(ownerUsername: string, repoName: string): Observable<void> {
    return this.apiService.delete<void>(`/repos/${ownerUsername}/${repoName}/star`);
  }

  forkRepository(ownerUsername: string, repoName: string): Observable<Repository> {
    return this.apiService.post<Repository>(`/repos/${ownerUsername}/${repoName}/forks`, {});
  }

  watchRepository(ownerUsername: string, repoName: string, level: string = 'ALL'): Observable<void> {
    return this.apiService.post<void>(`/repos/${ownerUsername}/${repoName}/watch`, { level });
  }

  unwatchRepository(ownerUsername: string, repoName: string): Observable<void> {
    return this.apiService.delete<void>(`/repos/${ownerUsername}/${repoName}/watch`);
  }

  listReleases(ownerUsername: string, repoName: string, page: number = 0, size: number = 10): Observable<Page<RepositoryRelease>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.apiService.get<Page<RepositoryRelease>>(`/repos/${ownerUsername}/${repoName}/releases`, params);
  }

  createRelease(ownerUsername: string, repoName: string, data: { name: string, tagName: string, body: string, isPreRelease: boolean }): Observable<RepositoryRelease> {
    return this.apiService.post<RepositoryRelease>(`/repos/${ownerUsername}/${repoName}/releases`, data);
  }
}
