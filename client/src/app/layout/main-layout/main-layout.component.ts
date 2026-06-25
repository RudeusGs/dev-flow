import { Component, OnInit, OnDestroy, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { NotificationService, Notification } from '../../core/services/notification.service';
import { AuthService } from '../../core/services/auth.service';
import { interval, Subscription } from 'rxjs';

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive],
  template: `
    <div class="layout-container-topnav">
      <!-- Top Navigation Header -->
      <header class="top-navbar">
        <div class="navbar-left">
          <a class="brand-logo" routerLink="/">
            <i class="bi bi-git"></i>
            <span class="brand-name">DevFlow</span>
          </a>
          <nav class="navbar-links">
            <a routerLink="/" routerLinkActive="active" [routerLinkActiveOptions]="{exact: true}" class="navbar-link">Dashboard</a>
            <a routerLink="/repos" routerLinkActive="active" class="navbar-link">Repositories</a>
            <a routerLink="/pulls" routerLinkActive="active" class="navbar-link">Pull Requests</a>
          </nav>
        </div>

        <div class="navbar-right">
          <!-- Search Box -->
          <div class="search-box-top">
            <i class="bi bi-search"></i>
            <input type="text" placeholder="Search resources...">
          </div>

          <!-- Notifications -->
          <div class="notification-trigger" (click)="toggleNotifications()" title="Notifications">
            <i class="bi bi-bell"></i>
            <span class="notification-dot" *ngIf="unreadCount > 0"></span>
            
            <!-- Notifications Panel -->
            <div class="dropdown-panel-top glass-panel" *ngIf="showNotifications" (click)="$event.stopPropagation()">
              <div class="panel-header">
                <h4>Notifications</h4>
              </div>
              <div class="panel-content">
                <div class="notification-item" *ngFor="let notif of notifications" [class.unread]="!notif.isRead" (click)="markRead(notif.id)">
                  <p class="notif-title"><strong>{{ notif.title }}</strong></p>
                  <p class="notif-desc">{{ notif.content }}</p>
                </div>
                <div class="empty-notif" *ngIf="notifications.length === 0">
                  No new notifications.
                </div>
              </div>
            </div>
          </div>

          <!-- User Profile Avatar -->
          <div class="user-avatar" routerLink="/users/me" title="My Profile">
            {{ authService.currentUser()?.displayName?.charAt(0) || authService.currentUser()?.username?.charAt(0) || 'U' | uppercase }}
          </div>

          <!-- Logout Button -->
          <button class="btn btn-secondary logout-btn" (click)="logout()" title="Log out">
            <i class="bi bi-box-arrow-right"></i>
          </button>
        </div>
      </header>

      <!-- Main Content Page Area -->
      <main class="page-body">
        <div class="page-container">
          <router-outlet></router-outlet>
        </div>
      </main>
    </div>
  `,
  styles: [`
    /* Notifications Panel Custom Styling */
    .dropdown-panel-top {
      position: absolute;
      top: 42px;
      right: 0;
      width: 320px;
      max-height: 400px;
      display: flex;
      flex-direction: column;
      z-index: 100;
      box-shadow: var(--shadow-lg);
      overflow: hidden;
      cursor: default;
      background-color: var(--bg-surface);
      border: 1px solid var(--border-color);
      border-radius: var(--radius-lg);
      animation: slideDown 0.15s ease-out;
    }

    @keyframes slideDown {
      from { opacity: 0; transform: translateY(-5px); }
      to { opacity: 1; transform: translateY(0); }
    }

    .panel-header {
      padding: 1rem;
      border-bottom: 1px solid var(--border-color);
      background-color: var(--bg-surface);
    }
    .panel-header h4 {
      margin: 0;
      font-size: 0.95rem;
      font-weight: 600;
      color: var(--text-primary);
    }
    .panel-content {
      overflow-y: auto;
      flex: 1;
      background-color: var(--bg-surface);
    }
    .notification-item {
      padding: 0.875rem 1rem;
      border-bottom: 1px solid var(--border-color);
      cursor: pointer;
      transition: background 0.2s;
    }
    .notification-item:hover {
      background-color: var(--primary-light);
    }
    .notification-item.unread {
      border-left: 3px solid var(--secondary);
      background-color: var(--primary-light);
    }
    .notif-title {
      margin: 0 0 0.15rem 0;
      color: var(--text-primary);
      font-size: 0.85rem;
      line-height: 1.3;
    }
    .notif-desc {
      margin: 0;
      font-size: 0.775rem;
      color: var(--text-secondary);
      line-height: 1.3;
    }
    .empty-notif {
      padding: 2rem 1rem;
      text-align: center;
      color: var(--text-tertiary);
      font-size: 0.85rem;
    }
    .badge-danger {
      background-color: var(--accent);
      color: white;
    }
  `]
})
export class MainLayoutComponent implements OnInit, OnDestroy {
  notificationService = inject(NotificationService);
  authService = inject(AuthService);
  
  notifications: Notification[] = [];
  unreadCount = 0;
  showNotifications = false;
  private pollSub?: Subscription;

  ngOnInit() {
    this.loadNotifications();
    this.pollSub = interval(30000).subscribe(() => this.loadNotifications());
  }

  ngOnDestroy() {
    if (this.pollSub) {
      this.pollSub.unsubscribe();
    }
  }

  loadNotifications() {
    if (this.authService.isAuthenticated()) {
      this.notificationService.getNotifications(false, 0, 10).subscribe({
        next: (page) => {
          this.notifications = page.content;
          this.unreadCount = this.notifications.filter(n => !n.isRead).length;
        },
        error: () => console.error('Failed to load notifications')
      });
    }
  }

  toggleNotifications() {
    this.showNotifications = !this.showNotifications;
  }

  markRead(id: string) {
    this.notificationService.markAsRead(id).subscribe({
      next: () => this.loadNotifications()
    });
  }

  logout() {
    this.authService.logout();
  }
}
