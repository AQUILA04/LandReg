import { Component, inject } from '@angular/core';
import { RouterOutlet, RouterModule } from '@angular/router';
import { AuthService } from './core/auth/services/auth.service';
import { ToastManager } from './shared/components/toast-notification/toast-notification';
import { NgxPermissionsModule } from 'ngx-permissions';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterModule, ToastManager, NgxPermissionsModule],
  template: `
    <div class="app-layout">
      @if (authService.isAuthenticated()) {
        <header class="app-header">
          <div class="container header-content">
            <div class="logo">LandReg Portal</div>
            <nav class="main-nav">
              <a routerLink="/actors" routerLinkActive="active">Acteurs</a>
              <a routerLink="/findings" routerLinkActive="active">Constatations</a>
              <a routerLink="/synchro-history" routerLinkActive="active">Synchro</a>
              <a *ngxPermissionsOnly="['SUPERADMIN']" routerLink="/users" routerLinkActive="active">Utilisateurs</a>
              <a *ngxPermissionsOnly="['SUPERADMIN']" routerLink="/profiles" routerLinkActive="active">Profils</a>
            </nav>
            <div class="user-actions">
              <span class="user-name">{{ authService.currentUser()?.username }}</span>
              <button class="btn-logout" (click)="logout()">Déconnexion</button>
            </div>
          </div>
        </header>
      }

      <main class="main-content">
        <router-outlet></router-outlet>
      </main>
      <app-toast-manager></app-toast-manager>
    </div>
  `,
  styles: [`
    .app-layout {
      display: flex;
      flex-direction: column;
      min-height: 100vh;
    }

    .app-header {
      background-color: var(--color-bg-surface);
      border-bottom: 1px solid var(--color-border);
      padding: calc(var(--spacing-base) * 3) 0;
    }

    .header-content {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .logo {
      font-weight: 700;
      font-size: 16px;
      color: var(--color-primary);
    }

    .main-nav {
      display: flex;
      gap: calc(var(--spacing-base) * 6);
    }

    .main-nav a {
      color: var(--color-text-secondary);
      font-weight: 500;
      padding: calc(var(--spacing-base) * 2) 0;
      border-bottom: 2px solid transparent;
    }

    .main-nav a:hover, .main-nav a.active {
      color: var(--color-text-primary);
      border-bottom-color: var(--color-primary);
    }

    .user-actions {
      display: flex;
      align-items: center;
      gap: calc(var(--spacing-base) * 4);
    }

    .user-name {
      font-weight: 500;
    }

    .btn-logout {
      color: var(--color-danger);
      font-size: 13px;
      font-weight: 500;
      padding: calc(var(--spacing-base) * 1.5) calc(var(--spacing-base) * 3);
      border: 1px solid var(--color-danger);
      border-radius: var(--radius-sm);
      transition: all var(--transition-fast);
    }

    .btn-logout:hover {
      background-color: rgba(239, 68, 68, 0.1);
    }

    .main-content {
      flex: 1;
      display: flex;
      flex-direction: column;
    }
  `]
})
export class App {
  authService = inject(AuthService);

  logout() {
    this.authService.logout();
  }
}
