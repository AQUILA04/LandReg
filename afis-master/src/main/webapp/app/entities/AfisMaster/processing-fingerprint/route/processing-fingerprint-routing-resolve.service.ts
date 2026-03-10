import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IProcessingFingerprint } from '../processing-fingerprint.model';
import { ProcessingFingerprintService } from '../service/processing-fingerprint.service';

const processingFingerprintResolve = (route: ActivatedRouteSnapshot): Observable<null | IProcessingFingerprint> => {
  const id = route.params.id;
  if (id) {
    return inject(ProcessingFingerprintService)
      .find(id)
      .pipe(
        mergeMap((processingFingerprint: HttpResponse<IProcessingFingerprint>) => {
          if (processingFingerprint.body) {
            return of(processingFingerprint.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default processingFingerprintResolve;
