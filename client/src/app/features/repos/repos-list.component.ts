import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { RepoService, Repository, Page } from '../../core/services/repo.service';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-repos-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="fade-in">
      <div class="header">
        <h1 class="page-title">Repositories</h1>
        <button class="btn btn-primary" (click)="openCreateModal()">
          <i class="bi bi-plus-lg"></i>
          <span>New Repository</span>
        </button>
      </div>

      <div class="repos-filter-bar">
        <div class="search-box">
          <i class="bi bi-search"></i>
          <input type="text" placeholder="Search repositories..." (input)="filterRepos($event)">
        </div>
      </div>

      <div *ngIf="loading" class="flex-center p-8">
        <div class="spinner"></div>
      </div>

      <div class="repos-grid" *ngIf="!loading && filteredRepos.length > 0">
        <div *ngFor="let repo of filteredRepos" class="repo-card glass-panel">
          <div class="repo-card-header">
            <div class="repo-meta-left">
              <i class="bi bi-journal-code repo-icon"></i>
              <a [routerLink]="['/repos', repo.ownerUsername, repo.slug]" class="repo-name-link">
                {{ repo.ownerUsername }} / <strong>{{ repo.name }}</strong>
              </a>
            </div>
            <span class="badge" [ngClass]="repo.visibility === 'PRIVATE' ? 'badge-neutral' : 'badge-success'">
              {{ repo.visibility === 'PRIVATE' ? 'Private' : 'Public' }}
            </span>
          </div>
          
          <p class="repo-description">{{ repo.description || 'No description provided for this repository.' }}</p>
          
          <div class="repo-card-footer">
            <div class="repo-stats">
              <span class="stat-item"><i class="bi bi-star"></i> {{ repo.starsCount || 0 }}</span>
              <span class="stat-item"><i class="bi bi-diagram-2"></i> {{ repo.forksCount || 0 }}</span>
            </div>
            <span class="updated-time">Updated {{ repo.updatedAt | date:'mediumDate' }}</span>
          </div>
        </div>
      </div>

      <div *ngIf="!loading && filteredRepos.length === 0" class="empty-state glass-panel">
        <i class="bi bi-folder-x" style="font-size: 3rem; color: var(--text-tertiary); margin-bottom: 1rem; display: block;"></i>
        <h3>No repositories found</h3>
        <p class="text-secondary mt-1">Create a new repository or adjust your search filter to get started.</p>
        <button class="btn btn-primary mt-4" (click)="openCreateModal()">Create Repository</button>
      </div>
    </div>
  `,
  styles: [`
    .header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 2rem;
    }
    .page-title {
      font-size: 1.5rem;
      font-weight: 700;
      color: var(--text-primary);
      letter-spacing: -0.02em;
    }
    
    .repos-filter-bar {
      margin-bottom: 1.5rem;
      display: flex;
      width: 100%;
    }
    .search-box {
      position: relative;
      flex: 1;
      max-width: 400px;
    }
    .search-box i {
      position: absolute;
      left: 1rem;
      top: 50%;
      transform: translateY(-50%);
      color: var(--text-tertiary);
      font-size: 0.9rem;
    }
    .search-box input {
      width: 100%;
      padding: 0.625rem 1rem 0.625rem 2.5rem;
      font-size: 0.875rem;
      border: 1px solid var(--border-color);
      border-radius: var(--radius-md);
      background-color: var(--bg-surface);
      color: var(--text-primary);
      outline: none;
      transition: all var(--transition-fast);
      font-family: inherit;
    }
    .search-box input:focus {
      border-color: var(--primary);
      box-shadow: 0 0 0 3px var(--primary-light);
    }

    .repos-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
      gap: 1.25rem;
    }
    
    .repo-card {
      padding: 1.5rem;
      display: flex;
      flex-direction: column;
      gap: 1rem;
      background-color: var(--bg-surface);
    }
    
    .repo-card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      gap: 0.75rem;
    }
    .repo-meta-left {
      display: flex;
      align-items: center;
      gap: 0.625rem;
      min-width: 0;
    }
    .repo-icon {
      color: var(--text-secondary);
      font-size: 1.15rem;
      flex-shrink: 0;
    }
    .repo-name-link {
      color: var(--text-primary);
      font-size: 0.95rem;
      text-decoration: none;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
      font-weight: 500;
    }
    .repo-name-link strong {
      color: var(--text-primary);
    }
    .repo-name-link:hover {
      color: var(--secondary);
    }
    
    .repo-description {
      color: var(--text-secondary);
      font-size: 0.85rem;
      line-height: 1.4;
      margin: 0;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
      text-overflow: ellipsis;
      height: 2.8rem;
    }
    
    .repo-card-footer {
      display: flex;
      justify-content: space-between;
      align-items: center;
      font-size: 0.8rem;
      color: var(--text-tertiary);
      border-top: 1px solid var(--border-color);
      padding-top: 0.875rem;
      margin-top: 0.25rem;
    }
    .repo-stats {
      display: flex;
      gap: 1rem;
    }
    .stat-item {
      display: flex;
      align-items: center;
      gap: 0.25rem;
      color: var(--text-secondary);
    }
    .updated-time {
      color: var(--text-tertiary);
    }
    
    .empty-state {
      text-align: center;
      padding: 4rem 2rem;
      color: var(--text-secondary);
      background-color: var(--bg-surface);
    }
    .empty-state h3 {
      font-size: 1.2rem;
      font-weight: 600;
      color: var(--text-primary);
      margin: 0.5rem 0 0 0;
    }
    
    .p-8 { padding: 4rem; }
    .mt-4 { margin-top: 1rem; }
  `]
})
export class ReposListComponent implements OnInit {
  repoService = inject(RepoService);
  authService = inject(AuthService);
  router = inject(Router);
  repos: Repository[] = [];
  filteredRepos: Repository[] = [];
  loading = true;

  ngOnInit() {
    this.loadRepos();
  }

  loadRepos() {
    this.loading = true;
    const user = this.authService.currentUser();
    
    if (user) {
      this.repoService.getUserRepositories(user.username, 0, 100).subscribe({
        next: (page: Page<Repository>) => {
          this.repos = page.content;
          this.filteredRepos = [...this.repos];
          this.loading = false;
        },
        error: (err) => {
          console.error('Failed to load repositories', err);
          this.loading = false;
        }
      });
    } else {
      this.repoService.getPublicRepositories(0, 100).subscribe({
        next: (page: Page<Repository>) => {
          this.repos = page.content;
          this.filteredRepos = [...this.repos];
          this.loading = false;
        },
        error: (err) => {
          console.error('Failed to load public repositories', err);
          this.loading = false;
        }
      });
    }
  }

  filterRepos(event: any) {
    const query = event.target.value.toLowerCase().trim();
    if (!query) {
      this.filteredRepos = [...this.repos];
    } else {
      this.filteredRepos = this.repos.filter(r => 
        r.name.toLowerCase().includes(query) || 
        (r.description && r.description.toLowerCase().includes(query)) ||
        r.ownerUsername.toLowerCase().includes(query)
      );
    }
  }

  openCreateModal() {
    this.router.navigate(['/repos/new']);
  }
}
