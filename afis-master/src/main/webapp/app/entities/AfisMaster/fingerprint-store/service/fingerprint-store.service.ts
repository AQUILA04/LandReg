import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IFingerprintStore, NewFingerprintStore } from '../fingerprint-store.model';

export type PartialUpdateFingerprintStore = Partial<IFingerprintStore> & Pick<IFingerprintStore, 'id'>;

export type EntityResponseType = HttpResponse<IFingerprintStore>;
export type EntityArrayResponseType = HttpResponse<IFingerprintStore[]>;

@Injectable({ providedIn: 'root' })
export class FingerprintStoreService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/fingerprint-stores', 'afismaster');

  create(fingerprintStore: NewFingerprintStore): Observable<EntityResponseType> {
    return this.http.post<IFingerprintStore>(this.resourceUrl, fingerprintStore, { observe: 'response' });
  }

  update(fingerprintStore: IFingerprintStore): Observable<EntityResponseType> {
    return this.http.put<IFingerprintStore>(
      `${this.resourceUrl}/${this.getFingerprintStoreIdentifier(fingerprintStore)}`,
      fingerprintStore,
      { observe: 'response' },
    );
  }

  partialUpdate(fingerprintStore: PartialUpdateFingerprintStore): Observable<EntityResponseType> {
    return this.http.patch<IFingerprintStore>(
      `${this.resourceUrl}/${this.getFingerprintStoreIdentifier(fingerprintStore)}`,
      fingerprintStore,
      { observe: 'response' },
    );
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<IFingerprintStore>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IFingerprintStore[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getFingerprintStoreIdentifier(fingerprintStore: Pick<IFingerprintStore, 'id'>): string {
    return fingerprintStore.id;
  }

  compareFingerprintStore(o1: Pick<IFingerprintStore, 'id'> | null, o2: Pick<IFingerprintStore, 'id'> | null): boolean {
    return o1 && o2 ? this.getFingerprintStoreIdentifier(o1) === this.getFingerprintStoreIdentifier(o2) : o1 === o2;
  }

  addFingerprintStoreToCollectionIfMissing<Type extends Pick<IFingerprintStore, 'id'>>(
    fingerprintStoreCollection: Type[],
    ...fingerprintStoresToCheck: (Type | null | undefined)[]
  ): Type[] {
    const fingerprintStores: Type[] = fingerprintStoresToCheck.filter(isPresent);
    if (fingerprintStores.length > 0) {
      const fingerprintStoreCollectionIdentifiers = fingerprintStoreCollection.map(fingerprintStoreItem =>
        this.getFingerprintStoreIdentifier(fingerprintStoreItem),
      );
      const fingerprintStoresToAdd = fingerprintStores.filter(fingerprintStoreItem => {
        const fingerprintStoreIdentifier = this.getFingerprintStoreIdentifier(fingerprintStoreItem);
        if (fingerprintStoreCollectionIdentifiers.includes(fingerprintStoreIdentifier)) {
          return false;
        }
        fingerprintStoreCollectionIdentifiers.push(fingerprintStoreIdentifier);
        return true;
      });
      return [...fingerprintStoresToAdd, ...fingerprintStoreCollection];
    }
    return fingerprintStoreCollection;
  }
}
