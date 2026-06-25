import { Injectable, inject } from '@angular/core';
import { ApiService } from './api.service';
import { Observable } from 'rxjs';

export interface RepositoryMember {
  id: string;
  userId: string;
  username: string;
  role: 'ADMIN' | 'WRITE' | 'READ';
  createdAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class RepoMemberService {
  private apiService = inject(ApiService);

  getMembers(ownerUsername: string, repoSlug: string): Observable<RepositoryMember[]> {
    return this.apiService.get<RepositoryMember[]>(`/repos/${ownerUsername}/${repoSlug}/members`);
  }

  addMember(ownerUsername: string, repoSlug: string, username: string, role: string): Observable<RepositoryMember> {
    return this.apiService.post<RepositoryMember>(`/repos/${ownerUsername}/${repoSlug}/members`, { username, role });
  }

  updateMemberRole(ownerUsername: string, repoSlug: string, username: string, role: string): Observable<RepositoryMember> {
    return this.apiService.put<RepositoryMember>(`/repos/${ownerUsername}/${repoSlug}/members/${username}`, { role });
  }

  removeMember(ownerUsername: string, repoSlug: string, username: string): Observable<void> {
    return this.apiService.delete<void>(`/repos/${ownerUsername}/${repoSlug}/members/${username}`);
  }
}
