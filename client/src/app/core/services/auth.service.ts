import { Injectable, inject, signal } from '@angular/core';
import { ApiService } from './api.service';
import { Observable, tap, catchError, throwError } from 'rxjs';
import { Router } from '@angular/router';
import { User, AuthResponse, LoginRequest, RegisterRequest } from '../models/auth.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiService = inject(ApiService);
  private router = inject(Router);

  private readonly TOKEN_KEY = 'devflow_access_token';
  private readonly REFRESH_TOKEN_KEY = 'devflow_refresh_token';
  private readonly USER_KEY = 'devflow_user';

  // State using Angular Signals
  currentUser = signal<User | null>(this.getStoredUser());
  isAuthenticated = signal<boolean>(!!this.getStoredToken());

  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.apiService.post<AuthResponse>('/auth/login', credentials).pipe(
      tap(res => this.handleAuthSuccess(res))
    );
  }

  register(data: RegisterRequest): Observable<AuthResponse> {
    return this.apiService.post<AuthResponse>('/auth/register', data).pipe(
      tap(res => this.handleAuthSuccess(res))
    );
  }

  refreshToken(): Observable<AuthResponse> {
    const refreshToken = this.getStoredRefreshToken();
    if (!refreshToken) {
      this.clearAuth();
      return throwError(() => new Error('No refresh token available'));
    }

    return this.apiService.post<AuthResponse>('/auth/refresh', { token: refreshToken }).pipe(
      tap(response => this.handleAuthSuccess(response)),
      catchError(error => {
        this.clearAuth();
        return throwError(() => error);
      })
    );
  }

  logout(): void {
    const refreshToken = this.getStoredRefreshToken();
    if (refreshToken) {
      this.apiService.post('/auth/logout', { token: refreshToken }).subscribe({
        next: () => this.clearAuth(),
        error: () => this.clearAuth()
      });
    } else {
      this.clearAuth();
    }
  }

  getAccessToken(): string | null {
    return this.getStoredToken();
  }

  private handleAuthSuccess(response: AuthResponse): void {
    localStorage.setItem(this.TOKEN_KEY, response.accessToken);
    localStorage.setItem(this.REFRESH_TOKEN_KEY, response.refreshToken);
    localStorage.setItem(this.USER_KEY, JSON.stringify(response.user));
    
    this.currentUser.set(response.user);
    this.isAuthenticated.set(true);
  }

  private clearAuth(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.REFRESH_TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);
    
    this.currentUser.set(null);
    this.isAuthenticated.set(false);
    this.router.navigate(['/login']);
  }

  private getStoredToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  private getStoredRefreshToken(): string | null {
    return localStorage.getItem(this.REFRESH_TOKEN_KEY);
  }

  private getStoredUser(): User | null {
    const userStr = localStorage.getItem(this.USER_KEY);
    try {
      return userStr ? JSON.parse(userStr) : null;
    } catch {
      return null;
    }
  }
}
