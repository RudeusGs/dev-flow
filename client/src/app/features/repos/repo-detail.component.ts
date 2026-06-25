import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { RepoService, Repository, RepositoryRelease } from '../../core/services/repo.service';
import { PullRequestService, PullRequest } from '../../core/services/pull-request.service';
import { BranchService, Branch } from '../../core/services/branch.service';
import { IssueService, Issue } from '../../core/services/issue.service';
import { SourceFileService, SourceFile } from '../../core/services/source-file.service';
import { FormsModule } from '@angular/forms';
import { CollaboratorsComponent } from './collaborators/collaborators.component';
import { SourceFileViewerComponent } from './source-file-viewer/source-file-viewer.component';
import { CommitHistoryComponent } from './commit-history/commit-history.component';

@Component({
  selector: 'app-repo-detail',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, CollaboratorsComponent, SourceFileViewerComponent, CommitHistoryComponent],
  template: `
    <div class="fade-in" *ngIf="repo">
      <div class="header glass-panel p-4">
        <div class="repo-title">
          <div class="flex-align">
            <h1>{{ repo.ownerUsername }} / {{ repo.name }}</h1>
            <span class="badge" [ngClass]="repo.visibility === 'PRIVATE' ? 'badge-neutral' : 'badge-success'">
              {{ repo.visibility === 'PRIVATE' ? 'Private' : 'Public' }}
            </span>
          </div>
          <div class="repo-actions">
            <button class="btn btn-secondary mr-2" (click)="toggleWatch()">
              <i class="bi" [ngClass]="repo.isWatched ? 'bi-eye-slash' : 'bi-eye'"></i> {{ repo.isWatched ? 'Unwatch' : 'Watch' }}
            </button>
            <button class="btn btn-secondary" (click)="forkRepo()">
              <span class="badge badge-neutral">{{ repo.forksCount || 0 }}</span> Fork
            </button>
            <button class="btn btn-secondary" (click)="toggleStar()">
              <i class="bi" [ngClass]="repo.isStarred ? 'bi-star-fill text-warning' : 'bi-star'"></i> {{ repo.isStarred ? 'Unstar' : 'Star' }} <span class="badge badge-neutral ml-2">{{ repo.starsCount || 0 }}</span>
            </button>
            <button class="btn btn-danger" (click)="deleteRepo()">Delete</button>
            <div class="dropdown-wrapper">
              <button class="btn btn-success" (click)="toggleCloneDropdown()">
                <i class="bi bi-code-slash"></i> Code <i class="bi bi-caret-down-fill text-xs"></i>
              </button>
              <div class="clone-dropdown glass-panel p-4" *ngIf="showCloneDropdown">
                <div class="clone-header">
                  <h4 class="text-sm font-bold mb-2">Clone</h4>
                  <p class="text-xs text-secondary mb-3">Clone this repository using the command line.</p>
                </div>
                <div class="clone-input-group">
                  <input type="text" class="input-field clone-url-input" [value]="getCloneCommand()" readonly #cloneInput>
                  <button class="btn copy-btn" (click)="copyCloneCommand(cloneInput)">
                    <i class="bi" [ngClass]="copied ? 'bi-check-lg text-success' : 'bi-clipboard'"></i>
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
        <p class="description">{{ repo.description }}</p>
        
        <div class="tabs">
          <button class="tab-btn" [class.active]="activeTab === 'code'" (click)="activeTab = 'code'">Code</button>
          <button class="tab-btn" [class.active]="activeTab === 'issues'" (click)="activeTab = 'issues'">Issues</button>
          <button class="tab-btn" [class.active]="activeTab === 'pulls'" (click)="activeTab = 'pulls'">Pull Requests</button>
          <button class="tab-btn" [class.active]="activeTab === 'branches'" (click)="activeTab = 'branches'">Branches</button>
          <button class="tab-btn" [class.active]="activeTab === 'commits'" (click)="activeTab = 'commits'">Commits</button>
          <button class="tab-btn" [class.active]="activeTab === 'releases'" (click)="activeTab = 'releases'">Releases</button>
          <button class="tab-btn" [class.active]="activeTab === 'settings'" (click)="activeTab = 'settings'">Settings</button>
        </div>
      </div>

      <div class="content mt-4" [class.layout-with-sidebar]="activeTab === 'code'">
        <!-- Code Tab -->
        <div *ngIf="activeTab === 'code'" class="main-content">
          <div class="glass-panel p-4">
            <div class="flex-between">
              <h3>Source Files</h3>
              <div class="branch-selector" *ngIf="branches.length > 0">
                <select class="input-field" [(ngModel)]="selectedBranch" (change)="loadSourceFiles()">
                  <option *ngFor="let branch of branches" [value]="branch.name">{{ branch.name }}</option>
                </select>
              </div>
            </div>
            
            <table class="data-table mt-4" *ngIf="sourceFiles.length > 0 && !selectedFilePath">
              <thead>
                <tr>
                  <th>Name</th>
                  <th>Size</th>
                </tr>
              </thead>
              <tbody>
                <tr *ngFor="let file of sourceFiles" (click)="openFile(file)" class="cursor-pointer">
                  <td>
                  <i *ngIf="file.type === 'DIRECTORY'" class="bi bi-folder-fill text-warning mr-2"></i>
                  <i *ngIf="file.type === 'FILE'" class="bi bi-file-earmark-text text-secondary mr-2"></i>
                  {{ file.name }}
                </td>
                  <td class="text-tertiary">{{ file.size }} bytes</td>
                </tr>
              </tbody>
            </table>
            <div class="empty-state mt-4" *ngIf="sourceFiles.length === 0 && !selectedFilePath">
              <p>No files found in branch <strong>{{ selectedBranch }}</strong>.</p>
            </div>
            
            <div class="mt-4" *ngIf="selectedFilePath">
              <app-source-file-viewer 
                [ownerUsername]="ownerUsername"
                [repoName]="repoName"
                [branchName]="selectedBranch"
                [path]="selectedFilePath"
                [onClose]="closeFileViewer">
              </app-source-file-viewer>
            </div>
          </div>
        </div>

        <div class="sidebar" *ngIf="activeTab === 'code'">
          <div class="glass-panel p-4 mb-4">
            <h4 class="sidebar-title">About</h4>
            <p class="text-secondary text-sm m-0">{{ repo.description || 'No description provided.' }}</p>
          </div>

          <div class="glass-panel p-4 mb-4">
            <div class="flex-between mb-2">
              <h4 class="sidebar-title m-0">Releases</h4>
            </div>
            <div *ngIf="releases.length > 0">
              <div class="release-item mb-3" *ngFor="let release of releases | slice:0:3">
                <a href="javascript:void(0)" class="text-primary font-bold text-sm">{{ release.tagName }}</a>
                <span class="badge badge-success ml-2" *ngIf="!release.isPreRelease">Latest</span>
                <p class="text-secondary text-sm m-0">{{ release.name }}</p>
                <p class="text-tertiary text-xs m-0">{{ release.publishedAt | date:'mediumDate' }}</p>
              </div>
              <a href="javascript:void(0)" class="text-primary text-sm" (click)="activeTab = 'releases'">+ {{ releases.length }} releases</a>
            </div>
            <div *ngIf="releases.length === 0">
              <p class="text-secondary text-sm m-0">No releases published</p>
            </div>
          </div>
        </div>

        <!-- Issues Tab -->
        <div *ngIf="activeTab === 'issues'" class="glass-panel p-4">
          <div class="flex-between">
            <h3>Issues</h3>
            <button class="btn btn-primary" (click)="createIssue()">New Issue</button>
          </div>

          <table class="data-table mt-4" *ngIf="issues.length > 0">
            <thead>
              <tr>
                <th>Title</th>
                <th>State</th>
                <th>Author</th>
                <th>Created</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let issue of issues" class="cursor-pointer" [routerLink]="['/repos', ownerUsername, repoName, 'issues', issue.number]">
                <td><strong>#{{ issue.number }}</strong> {{ issue.title }}</td>
                <td>
                  <span class="badge" [ngClass]="issue.state === 'OPEN' ? 'badge-success' : 'badge-danger'">
                    {{ issue.state }}
                  </span>
                </td>
                <td>{{ issue.authorUsername }}</td>
                <td class="text-tertiary">{{ issue.createdAt | date:'mediumDate' }}</td>
              </tr>
            </tbody>
          </table>
          <div class="empty-state mt-4" *ngIf="issues.length === 0">
            <p>No issues found.</p>
          </div>
        </div>

        <!-- Pull Requests Tab -->
        <div *ngIf="activeTab === 'pulls'" class="glass-panel p-4">
          <div class="flex-between">
            <h3>Pull Requests</h3>
            <button class="btn btn-primary" (click)="createPR()">New Pull Request</button>
          </div>

          <table class="data-table mt-4" *ngIf="pullRequests.length > 0">
            <thead>
              <tr>
                <th>Title</th>
                <th>State</th>
                <th>Author</th>
                <th>Created</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let pr of pullRequests" class="cursor-pointer" [routerLink]="['/repos', ownerUsername, repoName, 'pulls', pr.number]">
                <td><strong>#{{ pr.number }}</strong> {{ pr.title }}</td>
                <td>
                  <span class="badge" 
                        [ngClass]="{'badge-success': pr.state === 'OPEN', 'badge-primary': pr.state === 'MERGED', 'badge-danger': pr.state === 'CLOSED'}">
                    {{ pr.state }}
                  </span>
                </td>
                <td>{{ pr.authorUsername }}</td>
                <td class="text-tertiary">{{ pr.createdAt | date:'mediumDate' }}</td>
              </tr>
            </tbody>
          </table>
          <div class="empty-state mt-4" *ngIf="pullRequests.length === 0">
            <p>No pull requests found.</p>
          </div>
        </div>

        <!-- Branches Tab -->
        <div *ngIf="activeTab === 'branches'" class="glass-panel p-4">
          <div class="flex-between">
            <h3>Branches</h3>
            <button class="btn btn-primary" (click)="createBranch()">New Branch</button>
          </div>

          <table class="data-table mt-4" *ngIf="branches.length > 0">
            <thead>
              <tr>
                <th>Name</th>
                <th>Commit Hash</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let branch of branches">
                <td>{{ branch.name }} <span *ngIf="branch.name === repo.defaultBranchName" class="badge badge-neutral">Default</span></td>
                <td class="text-tertiary">{{ branch.commitHash }}</td>
                <td>
                  <button class="btn" *ngIf="branch.name !== repo.defaultBranchName" (click)="deleteBranch(branch.name)">Delete</button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- Commits Tab -->
        <div *ngIf="activeTab === 'commits'" class="glass-panel p-4">
          <div class="flex-between">
            <h3>Commit History</h3>
            <div class="branch-selector" *ngIf="branches.length > 0">
              <select class="input-field" [(ngModel)]="selectedBranch">
                <option *ngFor="let branch of branches" [value]="branch.name">{{ branch.name }}</option>
              </select>
            </div>
          </div>
          <app-commit-history
            [ownerUsername]="ownerUsername"
            [repoName]="repoName"
            [branchName]="selectedBranch">
          </app-commit-history>
        </div>

        <!-- Releases Full Tab -->
        <div *ngIf="activeTab === 'releases'" class="glass-panel p-4">
          <div class="flex-between mb-4">
            <h3>Releases</h3>
            <button class="btn btn-primary">Draft a new release</button>
          </div>
          <div class="release-list mt-4">
            <div class="release-card glass-panel p-4 mb-4" *ngFor="let release of releases">
              <div class="release-header flex-between mb-3 border-bottom pb-3">
                <div>
                  <h4 class="m-0 text-primary">{{ release.name }}</h4>
                  <p class="text-secondary text-sm m-0">Tag: <strong>{{ release.tagName }}</strong></p>
                </div>
                <div class="text-right">
                  <span class="badge" [ngClass]="release.isPreRelease ? 'badge-neutral' : 'badge-success'">
                    {{ release.isPreRelease ? 'Pre-release' : 'Latest Release' }}
                  </span>
                  <p class="text-tertiary text-sm mt-2 m-0">{{ release.publishedAt | date }}</p>
                </div>
              </div>
              <div class="release-body">
                <p class="m-0" style="white-space: pre-wrap;">{{ release.body }}</p>
              </div>
            </div>
            <div *ngIf="releases.length === 0" class="text-center p-4">
              <p class="text-secondary">No releases found.</p>
            </div>
          </div>
        </div>

        <!-- Settings Tab -->
        <div *ngIf="activeTab === 'settings'" class="glass-panel p-4">
          <div class="flex-between">
            <h3>General Settings</h3>
          </div>
          <div class="mt-4">
            <div class="form-group mb-4">
              <label>Repository Name</label>
              <input type="text" class="input-field" [value]="repo.name" readonly>
              <p class="text-sm text-secondary mt-1">Renaming is currently not supported via UI.</p>
            </div>
            
            <div class="border-t my-4"></div>
            
            <h4 class="text-danger mt-4">Danger Zone</h4>
            <div class="danger-zone p-4 mt-2 border border-danger rounded">
              <div class="flex-between">
                <div>
                  <strong>Delete this repository</strong>
                  <p class="text-sm text-secondary m-0">Once you delete a repository, there is no going back. Please be certain.</p>
                </div>
                <button class="btn btn-danger" (click)="deleteRepo()">Delete repository</button>
              </div>
            </div>
            
            <div class="border-t my-4"></div>
            
            <app-collaborators [ownerUsername]="ownerUsername" [repoSlug]="repo.slug"></app-collaborators>
          </div>
        </div>
      </div>
    </div>
    
    <div *ngIf="loading" class="flex-center p-4">
      <div class="spinner"></div>
    </div>
  `,
  styles: [`
    .p-4 { padding: 1.5rem; }
    .mt-4 { margin-top: 1rem; }
    .ml-2 { margin-left: 0.5rem; }
    .repo-title { display: flex; justify-content: space-between; align-items: center; margin-bottom: 0.5rem; }
    .flex-align { display: flex; align-items: center; gap: 1rem; }
    .repo-actions { display: flex; gap: 0.5rem; }
    .repo-title h1 { margin: 0; color: var(--primary); }
    .description { color: var(--text-secondary); margin-bottom: 1.5rem; }
    .flex-between { display: flex; justify-content: space-between; align-items: center; }
    
    .tabs { display: flex; gap: 1rem; border-bottom: 1px solid var(--border-color); }
    .tab-btn {
      background: none; border: none; padding: 0.5rem 1rem; font-weight: 500;
      color: var(--text-secondary); cursor: pointer; border-bottom: 2px solid transparent;
      transition: all var(--transition-fast);
    }
    .tab-btn:hover { color: var(--primary); }
    .tab-btn.active { color: var(--primary); border-bottom-color: var(--primary); }
    
    .text-tertiary { color: var(--text-tertiary); font-size: 0.875rem; }
    .empty-state { text-align: center; padding: 3rem; color: var(--text-secondary); border: 1px dashed var(--border-color); border-radius: var(--radius-md); }
    .branch-selector { width: 250px; }
    .cursor-pointer { cursor: pointer; }
    .cursor-pointer:hover { background-color: rgba(0,0,0,0.02); }
    .text-danger { color: var(--accent); }
    .border-danger { border-color: var(--accent); }
    .border { border: 1px solid; }
    .rounded { border-radius: var(--radius-md); }
    .m-0 { margin: 0; }
    .mb-4 { margin-bottom: 1rem; }
    .text-sm { font-size: 0.875rem; }
    .text-xs { font-size: 0.75rem; }
    .border-bottom { border-bottom: 1px solid var(--border-color); }
    .pb-3 { padding-bottom: 1rem; }
    
    .layout-with-sidebar { display: flex; gap: 1.5rem; align-items: flex-start; }
    .main-content { flex: 1; min-width: 0; }
    .sidebar { width: 300px; flex-shrink: 0; }
    .sidebar-title { font-size: 1rem; color: var(--text-primary); border-bottom: 1px solid var(--border-color); padding-bottom: 0.5rem; margin-bottom: 1rem; }
    .dropdown-wrapper { position: relative; display: inline-block; }
    .clone-dropdown { position: absolute; top: calc(100% + 8px); right: 0; width: 320px; z-index: 10; background: var(--bg-surface); border: 1px solid var(--border-color); border-radius: var(--radius-md); box-shadow: var(--shadow-lg); text-align: left; }
    .clone-header h4 { margin: 0; color: var(--text-primary); }
    .clone-header p { margin: 0; color: var(--text-secondary); }
    .clone-input-group { display: flex; gap: 0.5rem; align-items: center; }
    .clone-url-input { flex: 1; font-family: monospace; font-size: 0.775rem; background-color: var(--bg-main); color: var(--text-primary); }
    .copy-btn { padding: 0.5rem; flex-shrink: 0; display: flex; align-items: center; justify-content: center; width: 34px; height: 34px; }
  `]
})
export class RepoDetailComponent implements OnInit {
  route = inject(ActivatedRoute);
  repoService = inject(RepoService);
  prService = inject(PullRequestService);
  branchService = inject(BranchService);
  issueService = inject(IssueService);
  sourceFileService = inject(SourceFileService);

  repo: Repository | null = null;
  pullRequests: PullRequest[] = [];
  branches: Branch[] = [];
  releases: RepositoryRelease[] = [];
  issues: Issue[] = [];
  sourceFiles: SourceFile[] = [];

  loading = true;
  activeTab = 'code';
  selectedBranch = '';
  selectedFilePath: string | null = null;

  ownerUsername = '';
  repoName = '';

  showCloneDropdown = false;
  copied = false;

  toggleCloneDropdown() {
    this.showCloneDropdown = !this.showCloneDropdown;
  }

  getCloneCommand(): string {
    const protocol = window.location.protocol;
    const host = window.location.host;
    return `git clone ${protocol}//${host}/git/${this.ownerUsername}/${this.repoName}.git`;
  }

  copyCloneCommand(input: HTMLInputElement) {
    input.select();
    navigator.clipboard.writeText(input.value).then(() => {
      this.copied = true;
      setTimeout(() => this.copied = false, 2000);
    });
  }

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      this.ownerUsername = params.get('owner')!;
      this.repoName = params.get('repo')!;
      this.loadRepository();
    });
  }

  loadRepository() {
    this.loading = true;
    this.repoService.getRepository(this.ownerUsername, this.repoName).subscribe({
      next: (data) => {
        this.repo = data;
        this.selectedBranch = data.defaultBranchName || 'main';
        this.loading = false;
        this.loadBranches();
        this.loadPullRequests();
        this.loadIssues();
        this.loadReleases();
      },
      error: () => this.loading = false
    });
  }

  loadBranches() {
    this.branchService.listBranches(this.ownerUsername, this.repoName).subscribe({
      next: (data) => {
        this.branches = data;
        this.loadSourceFiles();
      }
    });
  }

  loadReleases() {
    this.repoService.listReleases(this.ownerUsername, this.repoName).subscribe({
      next: (data) => {
        this.releases = data.content;
      }
    });
  }

  loadSourceFiles() {
    if (!this.selectedBranch) return;
    this.selectedFilePath = null;
    this.sourceFileService.listFiles(this.ownerUsername, this.repoName, this.selectedBranch).subscribe({
      next: (data) => this.sourceFiles = data
    });
  }

  openFile(file: SourceFile) {
    if (file.type === 'FILE') {
      this.selectedFilePath = file.path || file.name;
    }
  }

  closeFileViewer = () => {
    this.selectedFilePath = null;
  }

  loadIssues() {
    this.issueService.listIssues(this.ownerUsername, this.repoName).subscribe({
      next: (page) => this.issues = page.content
    });
  }

  loadPullRequests() {
    this.prService.getPullRequests(this.ownerUsername, this.repoName).subscribe({
      next: (page) => this.pullRequests = page.content
    });
  }

  createPR() {
    const title = prompt('PR Title:');
    if (title) {
      this.prService.createPullRequest(this.ownerUsername, this.repoName, {
        title,
        description: 'New PR',
        sourceBranch: 'feature',
        targetBranch: 'main'
      }).subscribe({
        next: () => this.loadPullRequests(),
        error: () => alert('Failed to create PR')
      });
    }
  }

  createIssue() {
    const title = prompt('Issue Title:');
    if (title) {
      this.issueService.createIssue(this.ownerUsername, this.repoName, {
        title,
        description: 'New Issue Description'
      }).subscribe({
        next: () => this.loadIssues(),
        error: () => alert('Failed to create Issue')
      });
    }
  }

  createBranch() {
    const name = prompt('New Branch Name:');
    if (name) {
      this.branchService.createBranch(this.ownerUsername, this.repoName, {
        name,
        sourceBranch: this.selectedBranch || 'main'
      }).subscribe({
        next: () => this.loadBranches(),
        error: () => alert('Failed to create Branch')
      });
    }
  }

  deleteBranch(name: string) {
    if (confirm(`Delete branch ${name}?`)) {
      this.branchService.deleteBranch(this.ownerUsername, this.repoName, name).subscribe({
        next: () => this.loadBranches(),
        error: () => alert('Failed to delete Branch')
      });
    }
  }

  toggleStar() {
    if (!this.repo) return;
    const action = this.repo.isStarred 
      ? this.repoService.unstarRepository(this.ownerUsername, this.repoName)
      : this.repoService.starRepository(this.ownerUsername, this.repoName);
      
    action.subscribe({
      next: () => this.loadRepository(),
      error: () => alert('Failed to update star status')
    });
  }

  toggleWatch() {
    if (!this.repo) return;
    const action = this.repo.isWatched
      ? this.repoService.unwatchRepository(this.ownerUsername, this.repoName)
      : this.repoService.watchRepository(this.ownerUsername, this.repoName, 'ALL');

    action.subscribe({
      next: () => this.loadRepository(),
      error: () => alert('Failed to update watch status')
    });
  }

  forkRepo() {
    if (confirm('Fork this repository to your account?')) {
      this.repoService.forkRepository(this.ownerUsername, this.repoName).subscribe({
        next: (forkedRepo) => {
          alert('Successfully forked!');
          window.location.href = `/repos/${forkedRepo.ownerUsername}/${forkedRepo.name}`;
        },
        error: () => alert('Failed to fork repository')
      });
    }
  }

  deleteRepo() {
    if (confirm('Are you absolutely sure you want to delete this repository? This action cannot be undone.')) {
      this.repoService.deleteRepository(this.ownerUsername, this.repoName).subscribe({
        next: () => {
          // Typically navigate to user profile or dashboard
          // Here we just alert and maybe go to /
          alert('Repository deleted.');
          window.location.href = '/';
        },
        error: () => alert('Failed to delete repository')
      });
    }
  }
}
