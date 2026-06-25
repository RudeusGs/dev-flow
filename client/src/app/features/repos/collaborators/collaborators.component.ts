import { Component, Input, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RepoMemberService, RepositoryMember } from '../../../core/services/repo-member.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-collaborators',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="collaborators-wrapper mt-4">
      <div class="flex-between">
        <h3>Collaborators</h3>
      </div>
      
      <p class="text-secondary text-sm">Manage who has access to this repository.</p>

      <!-- Add Collaborator Form -->
      <div class="add-collab-form flex gap-2 mt-4">
        <input type="text" class="input-field" placeholder="Search by username..." [(ngModel)]="newUsername">
        <select class="input-field w-auto" [(ngModel)]="newRole">
          <option value="READ">Read</option>
          <option value="WRITE">Write</option>
          <option value="ADMIN">Admin</option>
        </select>
        <button class="btn btn-primary" (click)="addCollaborator()" [disabled]="!newUsername">Add</button>
      </div>

      <!-- Collaborators List -->
      <table class="data-table mt-4" *ngIf="members.length > 0">
        <thead>
          <tr>
            <th>User</th>
            <th>Role</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr *ngFor="let member of members">
            <td>
              <div class="flex-align">
                <div class="avatar-sm flex-center">{{ member.username.charAt(0).toUpperCase() }}</div>
                <strong>{{ member.username }}</strong>
              </div>
            </td>
            <td>
              <select class="input-field w-auto select-sm" [ngModel]="member.role" (ngModelChange)="updateRole(member, $event)">
                <option value="READ">Read</option>
                <option value="WRITE">Write</option>
                <option value="ADMIN">Admin</option>
              </select>
            </td>
            <td>
              <button class="btn btn-danger text-sm" (click)="removeCollaborator(member.username)">Remove</button>
            </td>
          </tr>
        </tbody>
      </table>

      <div class="empty-state mt-4" *ngIf="members.length === 0 && !loading">
        <p>No collaborators found.</p>
      </div>
      
      <div *ngIf="loading" class="flex-center p-4">
        <div class="spinner"></div>
      </div>
    </div>
  `,
  styles: [`
    .flex-between { display: flex; justify-content: space-between; align-items: center; }
    .flex-align { display: flex; align-items: center; gap: 0.75rem; }
    .flex { display: flex; }
    .gap-2 { gap: 0.5rem; }
    .mt-4 { margin-top: 1rem; }
    .text-secondary { color: var(--text-secondary); }
    .text-sm { font-size: 0.875rem; }
    .w-auto { width: auto; }
    .select-sm { padding: 0.35rem 1.5rem 0.35rem 0.5rem; }
    .avatar-sm { width: 32px; height: 32px; background: var(--primary); color: white; border-radius: var(--radius-full); font-size: 0.8rem; font-weight: 600; }
    .empty-state { text-align: center; padding: 2rem; border: 1px dashed var(--border-color); border-radius: var(--radius-md); color: var(--text-secondary); }
  `]
})
export class CollaboratorsComponent implements OnInit {
  @Input({ required: true }) ownerUsername!: string;
  @Input({ required: true }) repoSlug!: string;

  private memberService = inject(RepoMemberService);
  
  members: RepositoryMember[] = [];
  loading = false;

  newUsername = '';
  newRole = 'READ';

  ngOnInit() {
    this.loadMembers();
  }

  loadMembers() {
    this.loading = true;
    this.memberService.getMembers(this.ownerUsername, this.repoSlug).subscribe({
      next: (data) => {
        this.members = data;
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }

  addCollaborator() {
    if (!this.newUsername) return;
    this.memberService.addMember(this.ownerUsername, this.repoSlug, this.newUsername, this.newRole).subscribe({
      next: () => {
        this.newUsername = '';
        this.newRole = 'READ';
        this.loadMembers();
      },
      error: (err) => alert(err?.error?.message || 'Failed to add collaborator.')
    });
  }

  updateRole(member: RepositoryMember, newRole: string) {
    if (member.role === newRole) return;
    this.memberService.updateMemberRole(this.ownerUsername, this.repoSlug, member.username, newRole).subscribe({
      next: () => this.loadMembers(),
      error: (err) => {
        alert(err?.error?.message || 'Failed to update role.');
        this.loadMembers(); // reload to revert change in UI
      }
    });
  }

  removeCollaborator(username: string) {
    if (confirm(`Are you sure you want to remove ${username}?`)) {
      this.memberService.removeMember(this.ownerUsername, this.repoSlug, username).subscribe({
        next: () => this.loadMembers(),
        error: (err) => alert('Failed to remove collaborator.')
      });
    }
  }
}
