import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IFingerprintStore } from '../fingerprint-store.model';
import { FingerprintStoreService } from '../service/fingerprint-store.service';

const fingerprintStoreResolve = (route: ActivatedRouteSnapshot): Observable<null | IFingerprintStore> => {
  const id = route.params.id;
  if (id) {
    return inject(FingerprintStoreService)
      .find(id)
      .pipe(
        mergeMap((fingerprintStore: HttpResponse<IFingerprintStore>) => {
          if (fingerprintStore.body) {
            return of(fingerprintStore.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default fingerprintStoreResolve;
