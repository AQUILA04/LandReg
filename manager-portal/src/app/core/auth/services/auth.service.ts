import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { AuthResponse, LoginRequest } from '../../models/auth.model';
import { NgxPermissionsService } from 'ngx-permissions';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly permissionsService = inject(NgxPermissionsService);

  private readonly API_URL = '/api/auth';
  private readonly TOKEN_KEY = 'auth_token';
  private readonly REFRESH_TOKEN_KEY = 'refresh_token';
  private readonly USER_KEY = 'auth_user';

  public readonly isAuthenticated = signal<boolean>(this.hasToken());
  public readonly currentUser = signal<AuthResponse | null>(this.getStoredUser());

  constructor() {
    this.restorePermissions();
  }

  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.API_URL}/signin`, credentials).pipe(
      tap(response => {
        this.saveSession(response);
      })
    );
  }

  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.REFRESH_TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);

    this.permissionsService.flushPermissions();
    this.isAuthenticated.set(false);
    this.currentUser.set(null);

    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  private hasToken(): boolean {
    return !!this.getToken();
  }

  private saveSession(response: AuthResponse): void {
    localStorage.setItem(this.TOKEN_KEY, response.accessToken);
    localStorage.setItem(this.REFRESH_TOKEN_KEY, response.refreshToken);
    localStorage.setItem(this.USER_KEY, JSON.stringify(response));

    this.isAuthenticated.set(true);
    this.currentUser.set(response);

    this.loadPermissions(response);
  }

  private getStoredUser(): AuthResponse | null {
    const userStr = localStorage.getItem(this.USER_KEY);
    if (userStr) {
      try {
        return JSON.parse(userStr);
      } catch {
        return null;
      }
    }
    return null;
  }

  private loadPermissions(user: AuthResponse): void {
    this.permissionsService.flushPermissions();
    const permissions: string[] = [];

    if (user.roles) {
      permissions.push(...user.roles);
    }

    if (user.profil && user.profil.permissions) {
      permissions.push(...user.profil.permissions);
    }

    this.permissionsService.loadPermissions(permissions);
  }

  private restorePermissions(): void {
    const user = this.getStoredUser();
    if (user) {
      this.loadPermissions(user);
    }
  }
}
