import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { OrganizationService, Organization } from '../../core/services/organization.service';

@Component({
  selector: 'app-organization',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="fade-in org-container" *ngIf="org">
      <div class="glass-panel p-4 flex-center flex-column mb-4">
        <div class="avatar-large flex-center mb-4">
          {{ org.displayName.charAt(0).toUpperCase() }}
        </div>
        <h2>{{ org.displayName }}</h2>
        <a *ngIf="org.websiteUrl" [href]="org.websiteUrl" target="_blank" class="text-primary">{{ org.websiteUrl }}</a>
        <p class="description mt-4">{{ org.description || 'No description provided.' }}</p>
        <p class="text-tertiary mt-2">Created on {{ org.createdAt | date:'mediumDate' }}</p>
      </div>

      <div class="tabs mt-4">
        <button class="tab-btn active">Repositories</button>
        <button class="tab-btn">People</button>
        <button class="tab-btn">Settings</button>
      </div>

      <div class="glass-panel p-4 mt-4">
        <div class="flex-between mb-4">
          <h3>Repositories</h3>
          <button class="btn btn-primary" routerLink="/repos/new">New Repository</button>
        </div>
        
        <div class="repo-list mt-4">
          <p class="text-secondary text-center p-4">No repositories found or this feature is still under development.</p>
        </div>
      </div>
    </div>
    
    <div *ngIf="loading" class="flex-center p-4">
      <div class="spinner"></div>
    </div>
  `,
  styles: [`
    .org-container { max-width: 800px; margin: 0 auto; padding: 2rem; }
    .flex-column { flex-direction: column; text-align: center; }
    .avatar-large {
      width: 120px;
      height: 120px;
      border-radius: var(--radius-lg);
      background: linear-gradient(135deg, var(--accent), var(--primary));
      color: white;
      font-size: 3rem;
      font-weight: 600;
    }
    .mb-4 { margin-bottom: 1rem; }
    .mt-4 { margin-top: 1rem; }
    .p-4 { padding: 2rem; }
    .description { color: var(--text-secondary); max-width: 600px; }
    .text-tertiary { color: var(--text-tertiary); font-size: 0.875rem; }
    .text-primary { color: var(--primary); text-decoration: none; }
    .text-primary:hover { text-decoration: underline; }
    .tabs { display: flex; gap: 1rem; border-bottom: 1px solid var(--border-color); padding: 0 1rem; }
    .tab-btn { background: none; border: none; padding: 1rem; color: var(--text-secondary); cursor: pointer; font-weight: 500; border-bottom: 2px solid transparent; transition: all 0.2s; }
    .tab-btn:hover { color: var(--text-primary); }
    .tab-btn.active { color: var(--primary); border-bottom-color: var(--primary); }
    .repo-list { border-top: 1px solid var(--border-color); }
  `]
})
export class OrganizationComponent implements OnInit {
  route = inject(ActivatedRoute);
  orgService = inject(OrganizationService);

  org: Organization | null = null;
  loading = true;
  orgName = '';

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      this.orgName = params.get('name')!;
      this.loadOrg();
    });
  }

  loadOrg() {
    this.loading = true;
    this.orgService.getOrganization(this.orgName).subscribe({
      next: (data) => {
        this.org = data;
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }
}
