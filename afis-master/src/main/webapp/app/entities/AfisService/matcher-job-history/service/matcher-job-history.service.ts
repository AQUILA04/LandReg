import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IMatcherJobHistory, NewMatcherJobHistory } from '../matcher-job-history.model';

export type PartialUpdateMatcherJobHistory = Partial<IMatcherJobHistory> & Pick<IMatcherJobHistory, 'id'>;

export type EntityResponseType = HttpResponse<IMatcherJobHistory>;
export type EntityArrayResponseType = HttpResponse<IMatcherJobHistory[]>;

@Injectable({ providedIn: 'root' })
export class MatcherJobHistoryService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/matcher-job-histories', 'afismaster');

  create(matcherJobHistory: NewMatcherJobHistory): Observable<EntityResponseType> {
    return this.http.post<IMatcherJobHistory>(this.resourceUrl, matcherJobHistory, { observe: 'response' });
  }

  update(matcherJobHistory: IMatcherJobHistory): Observable<EntityResponseType> {
    return this.http.put<IMatcherJobHistory>(
      `${this.resourceUrl}/${this.getMatcherJobHistoryIdentifier(matcherJobHistory)}`,
      matcherJobHistory,
      { observe: 'response' },
    );
  }

  partialUpdate(matcherJobHistory: PartialUpdateMatcherJobHistory): Observable<EntityResponseType> {
    return this.http.patch<IMatcherJobHistory>(
      `${this.resourceUrl}/${this.getMatcherJobHistoryIdentifier(matcherJobHistory)}`,
      matcherJobHistory,
      { observe: 'response' },
    );
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<IMatcherJobHistory>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IMatcherJobHistory[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getMatcherJobHistoryIdentifier(matcherJobHistory: Pick<IMatcherJobHistory, 'id'>): string {
    return matcherJobHistory.id;
  }

  compareMatcherJobHistory(o1: Pick<IMatcherJobHistory, 'id'> | null, o2: Pick<IMatcherJobHistory, 'id'> | null): boolean {
    return o1 && o2 ? this.getMatcherJobHistoryIdentifier(o1) === this.getMatcherJobHistoryIdentifier(o2) : o1 === o2;
  }

  addMatcherJobHistoryToCollectionIfMissing<Type extends Pick<IMatcherJobHistory, 'id'>>(
    matcherJobHistoryCollection: Type[],
    ...matcherJobHistoriesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const matcherJobHistories: Type[] = matcherJobHistoriesToCheck.filter(isPresent);
    if (matcherJobHistories.length > 0) {
      const matcherJobHistoryCollectionIdentifiers = matcherJobHistoryCollection.map(matcherJobHistoryItem =>
        this.getMatcherJobHistoryIdentifier(matcherJobHistoryItem),
      );
      const matcherJobHistoriesToAdd = matcherJobHistories.filter(matcherJobHistoryItem => {
        const matcherJobHistoryIdentifier = this.getMatcherJobHistoryIdentifier(matcherJobHistoryItem);
        if (matcherJobHistoryCollectionIdentifiers.includes(matcherJobHistoryIdentifier)) {
          return false;
        }
        matcherJobHistoryCollectionIdentifiers.push(matcherJobHistoryIdentifier);
        return true;
      });
      return [...matcherJobHistoriesToAdd, ...matcherJobHistoryCollection];
    }
    return matcherJobHistoryCollection;
  }
}
