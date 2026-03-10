import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import ProcessingFingerprintResolve from './route/processing-fingerprint-routing-resolve.service';

const processingFingerprintRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/processing-fingerprint.component').then(m => m.ProcessingFingerprintComponent),
    data: {},
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/processing-fingerprint-detail.component').then(m => m.ProcessingFingerprintDetailComponent),
    resolve: {
      processingFingerprint: ProcessingFingerprintResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/processing-fingerprint-update.component').then(m => m.ProcessingFingerprintUpdateComponent),
    resolve: {
      processingFingerprint: ProcessingFingerprintResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/processing-fingerprint-update.component').then(m => m.ProcessingFingerprintUpdateComponent),
    resolve: {
      processingFingerprint: ProcessingFingerprintResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default processingFingerprintRoute;
