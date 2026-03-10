import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'fingerprint-store',
    data: { pageTitle: 'afisMasterApp.afisMasterFingerprintStore.home.title' },
    loadChildren: () => import('./AfisMaster/fingerprint-store/fingerprint-store.routes'),
  },
  {
    path: 'matcher-job-history',
    data: { pageTitle: 'afisMasterApp.afisServiceMatcherJobHistory.home.title' },
    loadChildren: () => import('./AfisService/matcher-job-history/matcher-job-history.routes'),
  },
  {
    path: 'processing-fingerprint',
    data: { pageTitle: 'afisMasterApp.afisMasterProcessingFingerprint.home.title' },
    loadChildren: () => import('./AfisMaster/processing-fingerprint/processing-fingerprint.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
