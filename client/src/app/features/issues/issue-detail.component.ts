import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { IssueService, Issue, IssueComment } from '../../core/services/issue.service';

@Component({
  selector: 'app-issue-detail',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="fade-in issue-container" *ngIf="issue">
      <div class="main-content">
        <div class="glass-panel p-4 mb-4">
          <div class="flex-between">
          <div class="issue-header">
            <h2>{{ issue.title }} <span class="text-tertiary">#{{ issue.number }}</span></h2>
            <div class="meta-info mt-2">
              <span class="badge" [ngClass]="issue.state === 'OPEN' ? 'badge-success' : 'badge-danger'">
                {{ issue.state }}
              </span>
              <span class="text-secondary ml-2">
                <strong>{{ issue.authorUsername }}</strong> opened this issue on {{ issue.createdAt | date:'mediumDate' }}
              </span>
            </div>
          </div>
          <button class="btn" [ngClass]="issue.state === 'OPEN' ? 'btn-danger' : 'btn-success'" (click)="toggleState()">
            {{ issue.state === 'OPEN' ? 'Close Issue' : 'Reopen Issue' }}
          </button>
        </div>
      </div>

      <div class="glass-panel p-4 mb-4">
        <p class="description">{{ issue.description || 'No description provided.' }}</p>
      </div>

      <div class="comments-section mt-4">
        <h3>Comments ({{ comments.length }})</h3>
        
        <div class="comment-list mt-3">
          <div class="comment-item glass-panel p-3 mb-3 fade-in" *ngFor="let comment of comments">
            <div class="comment-header flex-between mb-2 border-bottom pb-2">
              <strong>{{ comment.authorUsername }}</strong>
              <span class="text-tertiary text-sm">{{ comment.createdAt | date:'medium' }}</span>
            </div>
            <div class="comment-body">
              <p class="m-0">{{ comment.body }}</p>
            </div>
          </div>
        </div>

            <div class="add-comment mt-4 glass-panel p-4">
              <h4>Leave a comment</h4>
              <textarea class="input-field mt-2" rows="4" [(ngModel)]="newCommentBody" placeholder="Write your comment here..."></textarea>
              <div class="mt-3 text-right">
                <button class="btn btn-primary" [disabled]="!newCommentBody.trim()" (click)="addComment()">Comment</button>
              </div>
            </div>
          </div>
      </div>
      
      <div class="sidebar">
        <div class="glass-panel p-4 mb-4">
          <h4 class="sidebar-title">Assignees</h4>
          <p class="text-secondary text-sm m-0">No one assigned</p>
        </div>
        
        <div class="glass-panel p-4 mb-4">
          <h4 class="sidebar-title">Labels</h4>
          <p class="text-secondary text-sm m-0">None yet</p>
          <!-- Future: Add label badges here -->
        </div>
        
        <div class="glass-panel p-4 mb-4">
          <h4 class="sidebar-title">Milestone</h4>
          <p class="text-secondary text-sm m-0">No milestone</p>
        </div>
      </div>
    </div>
    
    <div *ngIf="loading" class="flex-center p-4">
      <div class="spinner"></div>
    </div>
  `,
  styles: [`
    .issue-container { display: flex; gap: 1.5rem; align-items: flex-start; }
    .main-content { flex: 1; min-width: 0; }
    .sidebar { width: 280px; flex-shrink: 0; }
    .sidebar-title { margin: 0 0 0.5rem 0; font-size: 0.95rem; color: var(--text-primary); border-bottom: 1px solid var(--border-color); padding-bottom: 0.5rem; }
    .p-4 { padding: 1.5rem; }
    .mb-4 { margin-bottom: 1rem; }
    .mt-2 { margin-top: 0.5rem; }
    .flex-between { display: flex; justify-content: space-between; align-items: flex-start; }
    .issue-header h2 { margin: 0; color: var(--text-primary); }
    .text-tertiary { color: var(--text-tertiary); font-weight: normal; }
    .meta-info { display: flex; align-items: center; gap: 0.5rem; font-size: 0.9rem; }
    .ml-2 { margin-left: 0.5rem; }
    .description { color: var(--text-secondary); line-height: 1.6; white-space: pre-wrap; margin: 0; }
    .btn-danger { background: var(--danger); color: white; border: none; }
    .btn-success { background: var(--success); color: white; border: none; }
    .border-bottom { border-bottom: 1px solid var(--border-color); }
    .pb-2 { padding-bottom: 0.5rem; }
    .mt-3 { margin-top: 1rem; }
    .text-sm { font-size: 0.875rem; }
    .text-right { text-align: right; }
    .m-0 { margin: 0; }
  `]
})
export class IssueDetailComponent implements OnInit {
  route = inject(ActivatedRoute);
  issueService = inject(IssueService);

  issue: Issue | null = null;
  comments: IssueComment[] = [];
  loading = true;
  newCommentBody = '';

  owner = '';
  repo = '';
  issueNumber = 0;

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      this.owner = params.get('owner')!;
      this.repo = params.get('repo')!;
      this.issueNumber = +params.get('number')!;
      this.loadIssue();
    });
  }

  loadIssue() {
    this.loading = true;
    this.issueService.getIssue(this.owner, this.repo, this.issueNumber).subscribe({
      next: (data) => {
        this.issue = data;
        this.loadComments();
      },
      error: () => this.loading = false
    });
  }

  loadComments() {
    this.issueService.listIssueComments(this.owner, this.repo, this.issueNumber).subscribe({
      next: (data) => {
        this.comments = data.content;
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }

  toggleState() {
    if (!this.issue) return;
    const newState = this.issue.state === 'OPEN' ? 'CLOSED' : 'OPEN';
    this.issueService.updateIssue(this.owner, this.repo, this.issueNumber, { state: newState }).subscribe({
      next: () => this.loadIssue(),
      error: () => alert('Failed to update issue')
    });
  }

  addComment() {
    if (!this.newCommentBody.trim()) return;
    this.issueService.createIssueComment(this.owner, this.repo, this.issueNumber, this.newCommentBody).subscribe({
      next: () => {
        this.newCommentBody = '';
        this.loadComments();
      },
      error: () => alert('Failed to add comment')
    });
  }
}
