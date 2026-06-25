import { Injectable, inject } from '@angular/core';
import { ApiService } from './api.service';
import { Observable } from 'rxjs';

export interface Organization {
  id: string;
  name: string;
  displayName: string;
  description: string;
  websiteUrl: string;
  createdAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class OrganizationService {
  private apiService = inject(ApiService);

  createOrganization(data: { name: string; displayName: string; description: string; websiteUrl: string }): Observable<Organization> {
    return this.apiService.post<Organization>('/orgs', data);
  }

  getOrganization(name: string): Observable<Organization> {
    return this.apiService.get<Organization>(`/orgs/${name}`);
  }
}
