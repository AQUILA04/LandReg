import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IMatcherJobHistory } from '../matcher-job-history.model';
import { MatcherJobHistoryService } from '../service/matcher-job-history.service';

const matcherJobHistoryResolve = (route: ActivatedRouteSnapshot): Observable<null | IMatcherJobHistory> => {
  const id = route.params.id;
  if (id) {
    return inject(MatcherJobHistoryService)
      .find(id)
      .pipe(
        mergeMap((matcherJobHistory: HttpResponse<IMatcherJobHistory>) => {
          if (matcherJobHistory.body) {
            return of(matcherJobHistory.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default matcherJobHistoryResolve;
