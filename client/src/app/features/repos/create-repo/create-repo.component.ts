import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { RepoService } from '../../../core/services/repo.service';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-create-repo',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="create-repo-wrapper fade-in">
      <div class="create-card glass-panel">
        <h2>Create a new repository</h2>
        <p class="text-secondary mb-4">A repository contains all project files, including the revision history.</p>
        
        <form [formGroup]="repoForm" (ngSubmit)="onSubmit()" class="form-layout">
          <div class="form-group">
            <label>Owner</label>
            <input type="text" class="input-field bg-disabled" [value]="currentUser?.username" disabled>
          </div>

          <div class="form-group">
            <label for="name">Repository name <span class="text-danger">*</span></label>
            <input id="name" type="text" class="input-field" formControlName="name" placeholder="my-awesome-project">
            <p *ngIf="repoForm.get('name')?.invalid && repoForm.get('name')?.touched" class="text-danger mt-1 text-sm">
              Repository name is required.
            </p>
          </div>

          <div class="form-group">
            <label for="description">Description <span class="text-secondary">(optional)</span></label>
            <input id="description" type="text" class="input-field" formControlName="description" placeholder="Short description of your project">
          </div>

          <div class="form-group mt-2">
            <label class="radio-label">
              <input type="radio" formControlName="visibility" value="PUBLIC">
              <div class="radio-text">
                <strong>Public</strong>
                <span class="text-secondary">Anyone on the internet can see this repository. You choose who can commit.</span>
              </div>
            </label>

            <label class="radio-label mt-2">
              <input type="radio" formControlName="visibility" value="PRIVATE">
              <div class="radio-text">
                <strong>Private</strong>
                <span class="text-secondary">You choose who can see and commit to this repository.</span>
              </div>
            </label>
          </div>
          
          <div class="border-t my-4"></div>

          <div class="flex gap-2">
            <button type="submit" class="btn btn-primary" [disabled]="repoForm.invalid || loading">
              {{ loading ? 'Creating...' : 'Create repository' }}
            </button>
            <button type="button" class="btn" (click)="cancel()">Cancel</button>
          </div>
        </form>
      </div>
    </div>
  `,
  styles: [`
    .create-repo-wrapper {
      max-width: 700px;
      margin: 2rem auto;
      padding: 0 1rem;
    }
    .create-card {
      padding: 2rem;
    }
    .form-layout {
      display: flex;
      flex-direction: column;
      gap: 1.5rem;
    }
    .form-group {
      display: flex;
      flex-direction: column;
      gap: 0.5rem;
    }
    .form-group label {
      font-weight: 600;
      font-size: 0.875rem;
    }
    .text-secondary { color: var(--text-secondary); }
    .text-danger { color: var(--accent); }
    .text-sm { font-size: 0.8rem; }
    .mb-4 { margin-bottom: 1rem; }
    .mt-1 { margin-top: 0.25rem; }
    .mt-2 { margin-top: 0.5rem; }
    .my-4 { margin: 1.5rem 0; }
    .bg-disabled { background-color: rgba(0,0,0,0.05); color: var(--text-secondary); cursor: not-allowed; }
    .radio-label {
      display: flex;
      align-items: flex-start;
      gap: 0.75rem;
      cursor: pointer;
    }
    .radio-label input[type="radio"] {
      margin-top: 0.25rem;
    }
    .radio-text {
      display: flex;
      flex-direction: column;
      font-size: 0.875rem;
    }
    .border-t {
      border-top: 1px solid var(--border-color);
    }
    .flex { display: flex; }
    .gap-2 { gap: 0.5rem; }
  `]
})
export class CreateRepoComponent {
  private fb = inject(FormBuilder);
  private repoService = inject(RepoService);
  private authService = inject(AuthService);
  private router = inject(Router);

  currentUser = this.authService.currentUser();
  loading = false;

  repoForm = this.fb.group({
    name: ['', Validators.required],
    description: [''],
    visibility: ['PUBLIC', Validators.required]
  });

  onSubmit() {
    if (this.repoForm.valid) {
      this.loading = true;
      const formValue = this.repoForm.value as any;
      
      this.repoService.createRepository(formValue).subscribe({
        next: (repo) => {
          this.router.navigate(['/repos', this.currentUser?.username, repo.slug]);
        },
        error: (err) => {
          console.error(err);
          alert('Failed to create repository. Please check your inputs.');
          this.loading = false;
        }
      });
    }
  }

  cancel() {
    this.router.navigate(['/repos']);
  }
}
