import { Injectable, inject } from '@angular/core';
import { ApiService } from './api.service';
import { HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Page } from './repo.service';

export interface PullRequest {
  id: string;
  number: number;
  title: string;
  description: string;
  state: 'OPEN' | 'CLOSED' | 'MERGED';
  sourceBranchName: string;
  targetBranchName: string;
  authorId: string;
  authorUsername: string;
  repositoryId: string;
  createdAt: string;
  updatedAt: string;
}

export interface PullRequestComment {
  id: string;
  body: string;
  authorUsername: string;
  authorId: string;
  pullRequestId: string;
  createdAt: string;
  updatedAt: string;
}

export interface PullRequestReview {
  id: string;
  body: string;
  state: 'APPROVED' | 'CHANGES_REQUESTED' | 'COMMENTED';
  authorUsername: string;
  authorId: string;
  pullRequestId: string;
  createdAt: string;
  updatedAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class PullRequestService {
  private apiService = inject(ApiService);

  getPullRequests(ownerUsername: string, repoName: string, status?: string, page: number = 0, size: number = 10): Observable<Page<PullRequest>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (status) {
      params = params.set('status', status);
    }
    return this.apiService.get<Page<PullRequest>>(`/repos/${ownerUsername}/${repoName}/pulls`, params);
  }

  getPullRequest(ownerUsername: string, repoName: string, prNumber: number): Observable<PullRequest> {
    return this.apiService.get<PullRequest>(`/repos/${ownerUsername}/${repoName}/pulls/${prNumber}`);
  }

  createPullRequest(ownerUsername: string, repoName: string, data: { title: string; description: string; sourceBranch: string; targetBranch: string }): Observable<PullRequest> {
    return this.apiService.post<PullRequest>(`/repos/${ownerUsername}/${repoName}/pulls`, data);
  }

  mergePullRequest(ownerUsername: string, repoName: string, prNumber: number): Observable<PullRequest> {
    return this.apiService.post<PullRequest>(`/repos/${ownerUsername}/${repoName}/pulls/${prNumber}/merge`, {});
  }

  listComments(ownerUsername: string, repoName: string, prNumber: number, page: number = 0, size: number = 20): Observable<Page<PullRequestComment>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.apiService.get<Page<PullRequestComment>>(`/repos/${ownerUsername}/${repoName}/pulls/${prNumber}/comments`, params);
  }

  createComment(ownerUsername: string, repoName: string, prNumber: number, body: string): Observable<PullRequestComment> {
    return this.apiService.post<PullRequestComment>(`/repos/${ownerUsername}/${repoName}/pulls/${prNumber}/comments`, { body });
  }

  listReviews(ownerUsername: string, repoName: string, prNumber: number, page: number = 0, size: number = 20): Observable<Page<PullRequestReview>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.apiService.get<Page<PullRequestReview>>(`/repos/${ownerUsername}/${repoName}/pulls/${prNumber}/reviews`, params);
  }

  createReview(ownerUsername: string, repoName: string, prNumber: number, data: { body: string, state: string }): Observable<PullRequestReview> {
    return this.apiService.post<PullRequestReview>(`/repos/${ownerUsername}/${repoName}/pulls/${prNumber}/reviews`, data);
  }
}
