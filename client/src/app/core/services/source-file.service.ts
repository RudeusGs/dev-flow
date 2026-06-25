import { Injectable, inject } from '@angular/core';
import { ApiService } from './api.service';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';

export interface SourceFile {
  name: string;
  path: string;
  type: 'FILE' | 'DIRECTORY';
  size: number;
}

@Injectable({
  providedIn: 'root'
})
export class SourceFileService {
  private apiService = inject(ApiService);
  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl;

  listFiles(ownerUsername: string, repoName: string, branchName: string, path?: string): Observable<SourceFile[]> {
    let params = new HttpParams();
    if (path) {
      params = params.set('path', path);
    }
    return this.apiService.get<SourceFile[]>(`/repos/${ownerUsername}/${repoName}/branches/${branchName}/files`, params);
  }

  getFileContent(ownerUsername: string, repoName: string, branchName: string, path: string): Observable<string> {
    const params = new HttpParams().set('path', path);
    return this.http.get(`${this.apiUrl}/repos/${ownerUsername}/${repoName}/branches/${branchName}/files/content`, { params, responseType: 'text' });
  }
}
