import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import MatcherJobHistoryResolve from './route/matcher-job-history-routing-resolve.service';

const matcherJobHistoryRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/matcher-job-history.component').then(m => m.MatcherJobHistoryComponent),
    data: {},
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/matcher-job-history-detail.component').then(m => m.MatcherJobHistoryDetailComponent),
    resolve: {
      matcherJobHistory: MatcherJobHistoryResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/matcher-job-history-update.component').then(m => m.MatcherJobHistoryUpdateComponent),
    resolve: {
      matcherJobHistory: MatcherJobHistoryResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/matcher-job-history-update.component').then(m => m.MatcherJobHistoryUpdateComponent),
    resolve: {
      matcherJobHistory: MatcherJobHistoryResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default matcherJobHistoryRoute;
