import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { ApiResponse } from '../../core/models/api-response.model';

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

@Injectable({
  providedIn: 'root'
})
export class DataService {
  private readonly http = inject(HttpClient);

  // Common wrapper extractor as dictated by the architecture
  private extractData<T>(res: ApiResponse<T>): T {
    return res.data;
  }

  getActors(page: number, size: number, filters: any): Observable<PageResponse<any>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.post<ApiResponse<PageResponse<any>>>('/land-reg/api/v1/actors/filter', filters, { params })
      .pipe(map(this.extractData));
  }

  getFindings(page: number, size: number, filters: any): Observable<PageResponse<any>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.post<ApiResponse<PageResponse<any>>>('/land-reg/api/v1/constatations/filter', filters, { params })
      .pipe(map(this.extractData));
  }

  getSynchroHistory(page: number, size: number, filters: any): Observable<PageResponse<any>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.post<ApiResponse<PageResponse<any>>>('/land-reg/api/v1/synchro-histories/filter', filters, { params })
      .pipe(map(this.extractData));
  }
}
