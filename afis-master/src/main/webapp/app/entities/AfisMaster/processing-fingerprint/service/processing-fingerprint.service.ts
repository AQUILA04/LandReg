import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IProcessingFingerprint, NewProcessingFingerprint } from '../processing-fingerprint.model';

export type PartialUpdateProcessingFingerprint = Partial<IProcessingFingerprint> & Pick<IProcessingFingerprint, 'id'>;

export type EntityResponseType = HttpResponse<IProcessingFingerprint>;
export type EntityArrayResponseType = HttpResponse<IProcessingFingerprint[]>;

@Injectable({ providedIn: 'root' })
export class ProcessingFingerprintService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/processing-fingerprints', 'afismaster');

  create(processingFingerprint: NewProcessingFingerprint): Observable<EntityResponseType> {
    return this.http.post<IProcessingFingerprint>(this.resourceUrl, processingFingerprint, { observe: 'response' });
  }

  update(processingFingerprint: IProcessingFingerprint): Observable<EntityResponseType> {
    return this.http.put<IProcessingFingerprint>(
      `${this.resourceUrl}/${this.getProcessingFingerprintIdentifier(processingFingerprint)}`,
      processingFingerprint,
      { observe: 'response' },
    );
  }

  partialUpdate(processingFingerprint: PartialUpdateProcessingFingerprint): Observable<EntityResponseType> {
    return this.http.patch<IProcessingFingerprint>(
      `${this.resourceUrl}/${this.getProcessingFingerprintIdentifier(processingFingerprint)}`,
      processingFingerprint,
      { observe: 'response' },
    );
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<IProcessingFingerprint>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IProcessingFingerprint[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getProcessingFingerprintIdentifier(processingFingerprint: Pick<IProcessingFingerprint, 'id'>): string {
    return processingFingerprint.id;
  }

  compareProcessingFingerprint(o1: Pick<IProcessingFingerprint, 'id'> | null, o2: Pick<IProcessingFingerprint, 'id'> | null): boolean {
    return o1 && o2 ? this.getProcessingFingerprintIdentifier(o1) === this.getProcessingFingerprintIdentifier(o2) : o1 === o2;
  }

  addProcessingFingerprintToCollectionIfMissing<Type extends Pick<IProcessingFingerprint, 'id'>>(
    processingFingerprintCollection: Type[],
    ...processingFingerprintsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const processingFingerprints: Type[] = processingFingerprintsToCheck.filter(isPresent);
    if (processingFingerprints.length > 0) {
      const processingFingerprintCollectionIdentifiers = processingFingerprintCollection.map(processingFingerprintItem =>
        this.getProcessingFingerprintIdentifier(processingFingerprintItem),
      );
      const processingFingerprintsToAdd = processingFingerprints.filter(processingFingerprintItem => {
        const processingFingerprintIdentifier = this.getProcessingFingerprintIdentifier(processingFingerprintItem);
        if (processingFingerprintCollectionIdentifiers.includes(processingFingerprintIdentifier)) {
          return false;
        }
        processingFingerprintCollectionIdentifiers.push(processingFingerprintIdentifier);
        return true;
      });
      return [...processingFingerprintsToAdd, ...processingFingerprintCollection];
    }
    return processingFingerprintCollection;
  }
}
