import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { guestGuard } from './core/guards/guest.guard';

export const routes: Routes = [
  {
    path: 'login',
    canActivate: [guestGuard],
    loadComponent: () => import('./features/auth/login/login.component').then(c => c.LoginComponent)
  },
  {
    path: 'register',
    canActivate: [guestGuard],
    loadComponent: () => import('./features/auth/register/register.component').then(c => c.RegisterComponent)
  },
  {
    path: '',
    canActivate: [authGuard],
    loadComponent: () => import('./layout/main-layout/main-layout.component').then(c => c.MainLayoutComponent),
    children: [
      {
        path: '',
        redirectTo: 'repos',
        pathMatch: 'full'
      },
      {
        path: 'repos',
        loadComponent: () => import('./features/repos/repos-list.component').then(c => c.ReposListComponent)
      },
      {
        path: 'repos/new',
        loadComponent: () => import('./features/repos/create-repo/create-repo.component').then(c => c.CreateRepoComponent)
      },
      {
        path: 'repos/:owner/:repo',
        loadComponent: () => import('./features/repos/repo-detail.component').then(c => c.RepoDetailComponent)
      },
      {
        path: 'repos/:owner/:repo/pulls/:number',
        loadComponent: () => import('./features/pull-requests/pr-detail.component').then(c => c.PrDetailComponent)
      },
      {
        path: 'repos/:owner/:repo/issues/:number',
        loadComponent: () => import('./features/issues/issue-detail.component').then(c => c.IssueDetailComponent)
      },
      {
        path: 'pulls',
        loadComponent: () => import('./features/pull-requests/pr-list.component').then(c => c.PrListComponent)
      },
      {
        path: 'users/:username',
        loadComponent: () => import('./features/users/user-profile.component').then(c => c.UserProfileComponent)
      },
      {
        path: 'orgs/new',
        loadComponent: () => import('./features/organizations/create-org.component').then(c => c.CreateOrgComponent)
      },
      {
        path: 'orgs/:name',
        loadComponent: () => import('./features/organizations/organization.component').then(c => c.OrganizationComponent)
      }
    ]
  },
  { path: '**', redirectTo: '' }
];
