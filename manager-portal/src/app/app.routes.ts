import { Routes } from '@angular/router';
import { authGuard } from './core/auth/guards/auth.guard';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () => import('./features/login/login').then(m => m.Login)
  },
  {
    path: 'actors',
    canActivate: [authGuard],
    loadComponent: () => import('./features/actors/list/actors-list/actors-list').then(m => m.ActorsList)
  },
  {
    path: 'findings',
    canActivate: [authGuard],
    loadComponent: () => import('./features/findings/list/findings-list/findings-list').then(m => m.FindingsList)
  },
  {
    path: 'synchro-history',
    canActivate: [authGuard],
    loadComponent: () => import('./features/synchro-history/list/synchro-list/synchro-list').then(m => m.SynchroList)
  },
  {
    path: 'users',
    canActivate: [authGuard],
    loadComponent: () => import('./features/users/list/user-list/user-list').then(m => m.UserList)
  },
  {
    path: 'profiles',
    canActivate: [authGuard],
    loadComponent: () => import('./features/profiles/list/profile-list/profile-list').then(m => m.ProfileList)
  },
  {
    path: '',
    redirectTo: 'findings',
    pathMatch: 'full'
  }
];
