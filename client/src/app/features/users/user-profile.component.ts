import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { UserService, UserProfile, UserSummary } from '../../core/services/user.service';
import { ContributionService, ContributionCalendar } from '../../core/services/contribution.service';

@Component({
  selector: 'app-user-profile',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="fade-in profile-container" *ngIf="user">
      <!-- Left Sidebar: User Info -->
      <aside class="profile-sidebar glass-panel">
        <div class="avatar-large flex-center">
          {{ user.username.charAt(0).toUpperCase() }}
        </div>
        <h2 class="mt-4">{{ user.displayName || user.username }}</h2>
        <p class="text-secondary">{{ user.email }}</p>
        <p class="bio mt-4 text-tertiary">&#64;{{ user.username }}</p>

        <button class="btn w-100 mt-4" 
                [ngClass]="user.isFollowing ? 'btn-secondary' : 'btn-primary'"
                (click)="toggleFollow()">
          {{ user.isFollowing ? 'Unfollow' : 'Follow' }}
        </button>

        <div class="stats mt-4">
          <div class="stat-box">
            <strong>{{ user.followersCount }}</strong>
            <span class="text-tertiary">Followers</span>
          </div>
          <div class="stat-box">
            <strong>{{ user.followingCount }}</strong>
            <span class="text-tertiary">Following</span>
          </div>
        </div>
      </aside>

      <!-- Right Content: Contributions & Tabs -->
      <main class="profile-content">
        <div class="tabs mb-4">
          <button class="tab-btn" [class.active]="activeTab === 'overview'" (click)="setTab('overview')">Overview</button>
          <button class="tab-btn" [class.active]="activeTab === 'repositories'" (click)="setTab('repositories')">Repositories</button>
          <button class="tab-btn" [class.active]="activeTab === 'followers'" (click)="setTab('followers')">Followers ({{ user.followersCount }})</button>
          <button class="tab-btn" [class.active]="activeTab === 'following'" (click)="setTab('following')">Following ({{ user.followingCount }})</button>
        </div>

        <div class="glass-panel p-4" *ngIf="activeTab === 'overview'">
          <h3>{{ calendar?.totalContributions || 0 }} contributions in the last year</h3>
          
          <div class="contribution-graph mt-4" *ngIf="calendar && calendar.days.length > 0">
            <div class="graph-grid">
              <div *ngFor="let day of calendar.days" 
                   class="graph-cell" 
                   [class]="getContributionClass(day.count)"
                   [title]="day.count + ' contributions on ' + day.date">
              </div>
            </div>
            <div class="graph-legend mt-4">
              <span class="text-tertiary text-sm">Less</span>
              <div class="graph-cell level-0"></div>
              <div class="graph-cell level-1"></div>
              <div class="graph-cell level-2"></div>
              <div class="graph-cell level-3"></div>
              <div class="graph-cell level-4"></div>
              <span class="text-tertiary text-sm">More</span>
            </div>
          </div>

          <div class="empty-state mt-4" *ngIf="!calendar || calendar.days.length === 0">
            <p>No recent contributions to display.</p>
          </div>
        </div>

        <!-- Followers Tab -->
        <div class="glass-panel p-4" *ngIf="activeTab === 'followers'">
          <h3>Followers</h3>
          <div class="user-list mt-4">
            <div class="user-list-item flex-between border-bottom pb-3 mb-3" *ngFor="let follower of followers">
              <div class="flex-align-center gap-3">
                <div class="avatar-small">{{ follower.username.charAt(0).toUpperCase() }}</div>
                <a [routerLink]="['/users', follower.username]" class="text-primary font-bold">{{ follower.username }}</a>
              </div>
            </div>
            <div class="empty-state mt-4" *ngIf="followers.length === 0">
              <p>No followers yet.</p>
            </div>
          </div>
        </div>

        <!-- Following Tab -->
        <div class="glass-panel p-4" *ngIf="activeTab === 'following'">
          <h3>Following</h3>
          <div class="user-list mt-4">
            <div class="user-list-item flex-between border-bottom pb-3 mb-3" *ngFor="let f of following">
              <div class="flex-align-center gap-3">
                <div class="avatar-small">{{ f.username.charAt(0).toUpperCase() }}</div>
                <a [routerLink]="['/users', f.username]" class="text-primary font-bold">{{ f.username }}</a>
              </div>
            </div>
            <div class="empty-state mt-4" *ngIf="following.length === 0">
              <p>Not following anyone yet.</p>
            </div>
          </div>
        </div>
        
        <!-- Repositories Tab Mock -->
        <div class="glass-panel p-4" *ngIf="activeTab === 'repositories'">
          <h3>Repositories</h3>
          <p class="text-secondary mt-4">View all repositories on the Repositories page.</p>
        </div>

      </main>
    </div>
    
    <div *ngIf="loading" class="flex-center p-4">
      <div class="spinner"></div>
    </div>
  `,
  styles: [`
    .profile-container {
      display: flex;
      gap: 2rem;
      padding: 1rem;
    }
    @media (max-width: 768px) {
      .profile-container { flex-direction: column; }
    }
    
    .profile-sidebar {
      width: 300px;
      padding: 2rem;
      text-align: center;
      display: flex;
      flex-direction: column;
      align-items: center;
    }
    .profile-content {
      flex: 1;
    }
    .avatar-large {
      width: 120px;
      height: 120px;
      border-radius: var(--radius-full);
      background: linear-gradient(135deg, var(--primary), var(--secondary));
      color: white;
      font-size: 3rem;
      font-weight: 600;
    }
    .bio { font-size: 0.95rem; color: var(--text-primary); }
    .w-100 { width: 100%; }
    .btn-secondary { background: var(--bg-surface); color: var(--text-primary); border: 1px solid var(--border-color); }
    .btn-secondary:hover { background: rgba(0,0,0,0.05); }

    .stats {
      display: flex;
      width: 100%;
      gap: 1rem;
      justify-content: center;
    }
    .stat-box { display: flex; flex-direction: column; align-items: center; }

    /* Contribution Graph */
    .contribution-graph {
      display: flex;
      flex-direction: column;
    }
    .graph-grid {
      display: grid;
      grid-template-rows: repeat(7, 1fr);
      grid-auto-flow: column;
      gap: 4px;
      overflow-x: auto;
      padding-bottom: 0.5rem;
    }
    .graph-cell {
      width: 12px;
      height: 12px;
      border-radius: 2px;
      background-color: var(--border-color);
    }
    
    .level-0 { background-color: var(--border-color); }
    .level-1 { background-color: color-mix(in srgb, var(--secondary) 25%, var(--border-color)); }
    .level-2 { background-color: color-mix(in srgb, var(--secondary) 50%, var(--border-color)); }
    .level-3 { background-color: color-mix(in srgb, var(--secondary) 75%, var(--border-color)); }
    .level-4 { background-color: var(--secondary); }

    .graph-legend {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      justify-content: flex-end;
    }
    .text-sm { font-size: 0.75rem; }

    .p-4 { padding: 1.5rem; }
    .mt-4 { margin-top: 1rem; }
    .text-secondary { color: var(--text-secondary); }
    .text-tertiary { color: var(--text-tertiary); }
    .empty-state { text-align: center; padding: 3rem; border: 1px dashed var(--border-color); border-radius: var(--radius-md); }
    
    .tabs { display: flex; gap: 1rem; border-bottom: 1px solid var(--border-color); padding: 0 1rem; }
    .tab-btn { background: none; border: none; padding: 1rem; color: var(--text-secondary); cursor: pointer; font-weight: 500; border-bottom: 2px solid transparent; transition: all 0.2s; }
    .tab-btn:hover { color: var(--text-primary); }
    .tab-btn.active { color: var(--primary); border-bottom-color: var(--primary); }
    .avatar-small { width: 40px; height: 40px; border-radius: var(--radius-full); background: var(--primary); color: white; display: flex; align-items: center; justify-content: center; font-weight: bold; }
    .flex-align-center { display: flex; align-items: center; }
    .flex-between { display: flex; justify-content: space-between; align-items: center; }
    .gap-3 { gap: 1rem; }
    .text-primary { color: var(--text-primary); text-decoration: none; }
    .text-primary:hover { text-decoration: underline; color: var(--primary); }
    .font-bold { font-weight: 600; }
    .border-bottom { border-bottom: 1px solid var(--border-color); }
    .pb-3 { padding-bottom: 1rem; }
    .mb-3 { margin-bottom: 1rem; }
    .mb-4 { margin-bottom: 1.5rem; }
  `]
})
export class UserProfileComponent implements OnInit {
  route = inject(ActivatedRoute);
  userService = inject(UserService);
  contributionService = inject(ContributionService);

  user: UserProfile | null = null;
  calendar: ContributionCalendar | null = null;
  followers: UserSummary[] = [];
  following: UserSummary[] = [];
  
  loading = true;
  username = '';
  activeTab = 'overview';

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      this.username = params.get('username')!;
      this.loadProfile();
    });
  }

  loadProfile() {
    this.loading = true;
    this.userService.getUserProfile(this.username).subscribe({
      next: (data) => {
        this.user = data;
        this.loadContributions();
      },
      error: () => this.loading = false
    });
  }

  loadContributions() {
    if (!this.user) return;
    this.contributionService.getCalendar(this.user.id).subscribe({
      next: (data) => {
        this.calendar = data;
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }

  setTab(tab: string) {
    this.activeTab = tab;
    if (tab === 'followers' && this.followers.length === 0) {
      this.userService.getFollowers(this.username).subscribe(data => this.followers = data.content);
    } else if (tab === 'following' && this.following.length === 0) {
      this.userService.getFollowing(this.username).subscribe(data => this.following = data.content);
    }
  }

  toggleFollow() {
    if (!this.user) return;
    if (this.user.isFollowing) {
      this.userService.unfollowUser(this.username).subscribe(() => this.loadProfile());
    } else {
      this.userService.followUser(this.username).subscribe(() => this.loadProfile());
    }
  }

  getContributionClass(count: number): string {
    if (count === 0) return 'level-0';
    if (count <= 2) return 'level-1';
    if (count <= 5) return 'level-2';
    if (count <= 8) return 'level-3';
    return 'level-4';
  }
}
