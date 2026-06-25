import { Injectable, inject } from '@angular/core';
import { ApiService } from './api.service';
import { HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Page } from './repo.service';

export interface Commit {
  id: string;
  commitHash: string;
  message: string;
  authorName: string;
  authorEmail: string;
  committerName: string;
  committerEmail: string;
  authoredAt: string;
  committedAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class CommitService {
  private apiService = inject(ApiService);

  createCommit(ownerUsername: string, repoName: string, data: { message: string; branchName: string; modifications: any[] }): Observable<Commit> {
    return this.apiService.post<Commit>(`/repos/${ownerUsername}/${repoName}/commits`, data);
  }

  listCommits(ownerUsername: string, repoName: string, branchName: string, page: number = 0, size: number = 20): Observable<Page<Commit>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    
    if (branchName) {
      params = params.set('branchName', branchName);
    }
      
    return this.apiService.get<Page<Commit>>(`/repos/${ownerUsername}/${repoName}/commits`, params);
  }
}
