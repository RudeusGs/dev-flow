import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { OrganizationService } from '../../core/services/organization.service';

@Component({
  selector: 'app-create-org',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="fade-in create-container">
      <div class="glass-panel p-4">
        <h2 class="mb-4">Create a new Organization</h2>
        <p class="text-secondary mb-4">A great way to group repositories and collaborate with your team.</p>

        <form (ngSubmit)="onSubmit()" #orgForm="ngForm">
          <div class="form-group">
            <label for="name">Organization Name <span class="text-danger">*</span></label>
            <input type="text" id="name" name="name" class="input-field" [(ngModel)]="formData.name" required pattern="[a-zA-Z0-9-]+" placeholder="e.g. acme-corp">
            <p class="text-sm text-secondary mt-1">This will be your organization's URL. Only alphanumeric characters and hyphens.</p>
          </div>

          <div class="form-group mt-3">
            <label for="displayName">Display Name <span class="text-danger">*</span></label>
            <input type="text" id="displayName" name="displayName" class="input-field" [(ngModel)]="formData.displayName" required placeholder="e.g. Acme Corporation">
          </div>

          <div class="form-group mt-3">
            <label for="description">Description</label>
            <textarea id="description" name="description" class="input-field" rows="3" [(ngModel)]="formData.description" placeholder="What is this organization about?"></textarea>
          </div>

          <div class="form-group mt-3">
            <label for="websiteUrl">Website URL</label>
            <input type="url" id="websiteUrl" name="websiteUrl" class="input-field" [(ngModel)]="formData.websiteUrl" placeholder="https://example.com">
          </div>

          <div class="mt-4 border-top pt-4">
            <button type="submit" class="btn btn-primary" [disabled]="!orgForm.form.valid || loading">
              <span *ngIf="loading" class="spinner-small"></span>
              {{ loading ? 'Creating...' : 'Create Organization' }}
            </button>
            <button type="button" class="btn ml-2" (click)="router.navigate(['/'])">Cancel</button>
          </div>
        </form>
      </div>
    </div>
  `,
  styles: [`
    .create-container { max-width: 600px; margin: 2rem auto; }
    .p-4 { padding: 2rem; }
    .mb-4 { margin-bottom: 1.5rem; }
    .mt-3 { margin-top: 1rem; }
    .mt-4 { margin-top: 1.5rem; }
    .pt-4 { padding-top: 1.5rem; }
    .ml-2 { margin-left: 0.5rem; }
    .border-top { border-top: 1px solid var(--border-color); }
    .text-danger { color: var(--accent); }
    .text-sm { font-size: 0.875rem; }
    .spinner-small { display: inline-block; width: 1rem; height: 1rem; border: 2px solid rgba(255,255,255,0.3); border-radius: 50%; border-top-color: white; animation: spin 1s ease-in-out infinite; margin-right: 0.5rem; vertical-align: middle; }
    @keyframes spin { to { transform: rotate(360deg); } }
  `]
})
export class CreateOrgComponent {
  router = inject(Router);
  orgService = inject(OrganizationService);

  formData = {
    name: '',
    displayName: '',
    description: '',
    websiteUrl: ''
  };
  
  loading = false;

  onSubmit() {
    if (!this.formData.name || !this.formData.displayName) return;
    this.loading = true;
    this.orgService.createOrganization(this.formData).subscribe({
      next: (org) => {
        this.loading = false;
        this.router.navigate(['/orgs', org.name]);
      },
      error: (err) => {
        this.loading = false;
        alert('Failed to create organization. Name might be taken.');
        console.error(err);
      }
    });
  }
}
