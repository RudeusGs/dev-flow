import { Injectable, inject } from '@angular/core';
import { ApiService } from './api.service';
import { Observable } from 'rxjs';

export interface Branch {
  name: string;
  commitHash: string;
}

@Injectable({
  providedIn: 'root'
})
export class BranchService {
  private apiService = inject(ApiService);

  listBranches(ownerUsername: string, repoName: string): Observable<Branch[]> {
    return this.apiService.get<Branch[]>(`/repos/${ownerUsername}/${repoName}/branches`);
  }

  createBranch(ownerUsername: string, repoName: string, data: { name: string; sourceBranch: string }): Observable<Branch> {
    return this.apiService.post<Branch>(`/repos/${ownerUsername}/${repoName}/branches`, data);
  }

  deleteBranch(ownerUsername: string, repoName: string, branchName: string): Observable<void> {
    return this.apiService.delete<void>(`/repos/${ownerUsername}/${repoName}/branches/${branchName}`);
  }
}
