import { Component, Input, OnChanges, SimpleChanges, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CommitService, Commit } from '../../../core/services/commit.service';
import { Page } from '../../../core/services/repo.service';

@Component({
  selector: 'app-commit-history',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="commit-history fade-in">
      <div *ngIf="loading" class="flex-center p-4">
        <div class="spinner"></div>
      </div>
      
      <div *ngIf="!loading && commits.length === 0" class="empty-state p-4 text-center">
        <p>No commits found for branch <strong>{{ branchName }}</strong>.</p>
      </div>

      <div class="timeline" *ngIf="commits.length > 0">
        <div class="commit-item glass-panel p-3 mb-3" *ngFor="let commit of commits">
          <div class="flex-between">
            <div>
              <h4 class="commit-msg mb-1">{{ commit.message }}</h4>
              <div class="commit-meta text-sm text-secondary">
                <strong>{{ commit.authorName }}</strong> committed on {{ commit.committedAt | date:'medium' }}
              </div>
            </div>
            <div class="commit-hash">
              <span class="badge badge-neutral font-mono">{{ commit.commitHash.substring(0, 7) }}</span>
            </div>
          </div>
        </div>
      </div>
      
      <div class="pagination flex-center gap-2 mt-4" *ngIf="totalPages > 1">
        <button class="btn btn-sm" [disabled]="page === 0" (click)="loadPage(page - 1)">Previous</button>
        <span class="text-sm">Page {{ page + 1 }} of {{ totalPages }}</span>
        <button class="btn btn-sm" [disabled]="page >= totalPages - 1" (click)="loadPage(page + 1)">Next</button>
      </div>
    </div>
  `,
  styles: [`
    .commit-history { margin-top: 1rem; }
    .commit-item {
      border-left: 3px solid var(--primary);
      transition: all var(--transition-fast);
    }
    .commit-item:hover {
      transform: translateX(4px);
    }
    .commit-msg { margin: 0; color: var(--text-primary); }
    .commit-meta { margin-top: 0.25rem; }
    .font-mono { font-family: monospace; font-size: 0.9rem; }
    .gap-2 { gap: 0.5rem; }
    .btn-sm { padding: 0.25rem 0.75rem; }
  `]
})
export class CommitHistoryComponent implements OnChanges {
  @Input({ required: true }) ownerUsername!: string;
  @Input({ required: true }) repoName!: string;
  @Input({ required: true }) branchName!: string;

  private commitService = inject(CommitService);

  commits: Commit[] = [];
  loading = false;
  page = 0;
  totalPages = 0;

  ngOnChanges(changes: SimpleChanges) {
    if (changes['branchName'] && !changes['branchName'].firstChange) {
      this.page = 0;
      this.loadCommits();
    } else if (changes['ownerUsername'] || changes['repoName'] || changes['branchName']) {
      this.loadCommits();
    }
  }

  loadCommits() {
    if (!this.branchName) return;
    this.loading = true;
    this.commitService.listCommits(this.ownerUsername, this.repoName, this.branchName, this.page).subscribe({
      next: (data: Page<Commit>) => {
        this.commits = data.content;
        this.totalPages = data.totalPages;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  loadPage(newPage: number) {
    this.page = newPage;
    this.loadCommits();
  }
}
