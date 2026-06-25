import { Injectable, inject } from '@angular/core';
import { ApiService } from './api.service';
import { HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Page } from './repo.service';

export interface Issue {
  id: string;
  number: number;
  title: string;
  description: string;
  state: 'OPEN' | 'CLOSED';
  authorId: string;
  authorUsername: string;
  repositoryId: string;
  createdAt: string;
  updatedAt: string;
}

export interface IssueComment {
  id: string;
  body: string;
  authorUsername: string;
  authorId: string;
  issueId: string;
  createdAt: string;
  updatedAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class IssueService {
  private apiService = inject(ApiService);

  listIssues(ownerUsername: string, repoName: string, status?: string, page: number = 0, size: number = 10): Observable<Page<Issue>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (status) {
      params = params.set('status', status);
    }
    return this.apiService.get<Page<Issue>>(`/repos/${ownerUsername}/${repoName}/issues`, params);
  }

  getIssue(ownerUsername: string, repoName: string, issueNumber: number): Observable<Issue> {
    return this.apiService.get<Issue>(`/repos/${ownerUsername}/${repoName}/issues/${issueNumber}`);
  }

  createIssue(ownerUsername: string, repoName: string, data: { title: string; description: string }): Observable<Issue> {
    return this.apiService.post<Issue>(`/repos/${ownerUsername}/${repoName}/issues`, data);
  }

  updateIssue(ownerUsername: string, repoName: string, issueNumber: number, data: { title?: string; description?: string; state?: string }): Observable<Issue> {
    return this.apiService.patch<Issue>(`/repos/${ownerUsername}/${repoName}/issues/${issueNumber}`, data); 
  }

  listIssueComments(ownerUsername: string, repoName: string, issueNumber: number, page: number = 0, size: number = 20): Observable<Page<IssueComment>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.apiService.get<Page<IssueComment>>(`/repos/${ownerUsername}/${repoName}/issues/${issueNumber}/comments`, params);
  }

  createIssueComment(ownerUsername: string, repoName: string, issueNumber: number, body: string): Observable<IssueComment> {
    return this.apiService.post<IssueComment>(`/repos/${ownerUsername}/${repoName}/issues/${issueNumber}/comments`, { body });
  }
}
