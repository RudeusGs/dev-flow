import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { PullRequestService, PullRequest, PullRequestComment, PullRequestReview } from '../../core/services/pull-request.service';

@Component({
  selector: 'app-pr-detail',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="fade-in" *ngIf="pr">
      <div class="glass-panel p-4 mb-4">
        <div class="flex-between">
          <div class="pr-header">
            <h2>{{ pr.title }} <span class="text-tertiary">#{{ pr.number }}</span></h2>
            <div class="meta-info">
              <span class="badge" 
                    [ngClass]="{'badge-success': pr.state === 'OPEN', 'badge-primary': pr.state === 'MERGED', 'badge-danger': pr.state === 'CLOSED'}">
                {{ pr.state }}
              </span>
              <span class="text-secondary ml-2">
                <strong>{{ pr.authorUsername }}</strong> wants to merge into <code>{{ pr.targetBranchName }}</code> from <code>{{ pr.sourceBranchName }}</code>
              </span>
            </div>
          </div>
          <button class="btn btn-primary" *ngIf="pr.state === 'OPEN'" (click)="mergePR()">Merge Pull Request</button>
        </div>
      </div>

      <div class="glass-panel p-4 mb-4">
        <h3>Description</h3>
        <p class="description mt-4">{{ pr.description || 'No description provided.' }}</p>
      </div>

      <div class="reviews-section mt-4" *ngIf="reviews.length > 0">
        <h3>Reviews</h3>
        <div class="review-item glass-panel p-3 mb-3" *ngFor="let review of reviews">
          <div class="flex-between mb-2">
            <strong>{{ review.authorUsername }}</strong>
            <span class="badge" [ngClass]="getReviewBadgeClass(review.state)">{{ review.state }}</span>
          </div>
          <p class="m-0">{{ review.body }}</p>
        </div>
      </div>

      <div class="comments-section mt-4">
        <h3>Comments ({{ comments.length }})</h3>
        <div class="comment-list mt-3">
          <div class="comment-item glass-panel p-3 mb-3 fade-in" *ngFor="let comment of comments">
            <div class="flex-between mb-2 border-bottom pb-2">
              <strong>{{ comment.authorUsername }}</strong>
              <span class="text-tertiary text-sm">{{ comment.createdAt | date:'medium' }}</span>
            </div>
            <p class="m-0">{{ comment.body }}</p>
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
      
      <div class="add-review mt-4 glass-panel p-4" *ngIf="pr.state === 'OPEN'">
        <h4>Submit Review</h4>
        <textarea class="input-field mt-2" rows="4" [(ngModel)]="newReviewBody" placeholder="Review feedback..."></textarea>
        <div class="flex-between mt-3">
          <select class="input-field" style="width: auto;" [(ngModel)]="newReviewState">
            <option value="COMMENTED">Comment</option>
            <option value="APPROVED">Approve</option>
            <option value="CHANGES_REQUESTED">Request Changes</option>
          </select>
          <button class="btn btn-success" [disabled]="!newReviewBody.trim() && newReviewState !== 'APPROVED'" (click)="submitReview()">Submit Review</button>
        </div>
      </div>
    </div>
    
    <div *ngIf="loading" class="flex-center p-4">
      <div class="spinner"></div>
    </div>
  `,
  styles: [`
    .p-4 { padding: 1.5rem; }
    .mb-4 { margin-bottom: 1rem; }
    .mt-4 { margin-top: 1rem; }
    .flex-between { display: flex; justify-content: space-between; align-items: flex-start; }
    .pr-header h2 { margin: 0 0 0.5rem 0; color: var(--text-primary); }
    .text-tertiary { color: var(--text-tertiary); font-weight: normal; }
    .meta-info { display: flex; align-items: center; gap: 0.5rem; font-size: 0.9rem; }
    .ml-2 { margin-left: 0.5rem; }
    code { background: var(--bg-surface); padding: 0.2rem 0.4rem; border-radius: var(--radius-sm); color: var(--primary); }
    .description { color: var(--text-secondary); line-height: 1.6; white-space: pre-wrap; }
    .border-bottom { border-bottom: 1px solid var(--border-color); }
    .pb-2 { padding-bottom: 0.5rem; }
    .mt-3 { margin-top: 1rem; }
    .text-sm { font-size: 0.875rem; }
    .text-right { text-align: right; }
    .m-0 { margin: 0; }
  `]
})
export class PrDetailComponent implements OnInit {
  route = inject(ActivatedRoute);
  prService = inject(PullRequestService);

  pr: PullRequest | null = null;
  comments: PullRequestComment[] = [];
  reviews: PullRequestReview[] = [];
  loading = true;
  newCommentBody = '';
  newReviewBody = '';
  newReviewState = 'COMMENTED';

  owner = '';
  repo = '';
  prNumber = 0;

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      this.owner = params.get('owner')!;
      this.repo = params.get('repo')!;
      this.prNumber = +params.get('number')!;
      this.loadPR();
    });
  }

  loadPR() {
    this.loading = true;
    this.prService.getPullRequest(this.owner, this.repo, this.prNumber).subscribe({
      next: (data) => {
        this.pr = data;
        this.loadComments();
        this.loadReviews();
      },
      error: () => this.loading = false
    });
  }

  loadComments() {
    this.prService.listComments(this.owner, this.repo, this.prNumber).subscribe({
      next: (data) => {
        this.comments = data.content;
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }

  loadReviews() {
    this.prService.listReviews(this.owner, this.repo, this.prNumber).subscribe({
      next: (data) => this.reviews = data.content
    });
  }

  mergePR() {
    if (confirm('Are you sure you want to merge this pull request?')) {
      this.prService.mergePullRequest(this.owner, this.repo, this.prNumber).subscribe({
        next: () => this.loadPR(),
        error: () => alert('Failed to merge PR')
      });
    }
  }

  addComment() {
    if (!this.newCommentBody.trim()) return;
    this.prService.createComment(this.owner, this.repo, this.prNumber, this.newCommentBody).subscribe({
      next: () => {
        this.newCommentBody = '';
        this.loadComments();
      },
      error: () => alert('Failed to add comment')
    });
  }

  submitReview() {
    this.prService.createReview(this.owner, this.repo, this.prNumber, { body: this.newReviewBody, state: this.newReviewState }).subscribe({
      next: () => {
        this.newReviewBody = '';
        this.newReviewState = 'COMMENTED';
        this.loadReviews();
      },
      error: () => alert('Failed to submit review')
    });
  }

  getReviewBadgeClass(state: string): string {
    switch(state) {
      case 'APPROVED': return 'badge-success';
      case 'CHANGES_REQUESTED': return 'badge-danger';
      default: return 'badge-neutral';
    }
  }
}
