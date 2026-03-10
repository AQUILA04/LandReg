import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import FingerprintStoreResolve from './route/fingerprint-store-routing-resolve.service';

const fingerprintStoreRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/fingerprint-store.component').then(m => m.FingerprintStoreComponent),
    data: {},
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/fingerprint-store-detail.component').then(m => m.FingerprintStoreDetailComponent),
    resolve: {
      fingerprintStore: FingerprintStoreResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/fingerprint-store-update.component').then(m => m.FingerprintStoreUpdateComponent),
    resolve: {
      fingerprintStore: FingerprintStoreResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/fingerprint-store-update.component').then(m => m.FingerprintStoreUpdateComponent),
    resolve: {
      fingerprintStore: FingerprintStoreResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default fingerprintStoreRoute;
