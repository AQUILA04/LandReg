import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { timer, Subject, takeUntil, switchMap, catchError, EMPTY, tap, filter } from 'rxjs';

export interface ExportStatus {
  jobId: string;
  status: 'PENDING' | 'PROCESSING' | 'COMPLETED' | 'FAILED';
  fileFormat: string;
  targetModule: string;
  filePath?: string;
  errorMessage?: string;
}

@Injectable({
  providedIn: 'root'
})
export class MassExportPollerService {
  private readonly http = inject(HttpClient);
  private readonly API_URL = '/land-reg/api/v1/exports';

  private stopPolling$ = new Subject<void>();

  // Expose signals to the UI (e.g. for the Toast Manager)
  public readonly activeJob = signal<ExportStatus | null>(null);
  public readonly isPolling = signal<boolean>(false);

  startExport(payload: any): void {
    // Stop any existing polling
    this.stopPolling$.next();

    this.http.post<any>(`${this.API_URL}/mass-export`, payload).pipe(
      tap((res) => {
        // According to our architecture, unwrap the standard `.data` envelope
        const data = res.data;
        if (data && data.jobId) {
          this.activeJob.set({
            jobId: data.jobId,
            status: 'PENDING',
            fileFormat: payload.format,
            targetModule: payload.targetModule
          });
          this.isPolling.set(true);
          this.beginPolling(data.jobId);
        }
      })
    ).subscribe({
      error: (err) => {
        console.error('Failed to initiate export', err);
        this.activeJob.set({
          jobId: 'unknown',
          status: 'FAILED',
          fileFormat: payload.format,
          targetModule: payload.targetModule,
          errorMessage: 'Erreur lors de l\'initialisation de l\'export'
        });
      }
    });
  }

  private beginPolling(jobId: string): void {
    // Poll every 3 seconds
    timer(0, 3000).pipe(
      takeUntil(this.stopPolling$),
      switchMap(() => this.http.get<any>(`${this.API_URL}/status/${jobId}`).pipe(
        catchError(err => {
          console.error('Polling error', err);
          // Don't stop polling on single network error, return EMPTY
          return EMPTY;
        })
      )),
      filter(res => !!res),
      tap((res) => {
        const jobStatus: ExportStatus = res.data;
        this.activeJob.set(jobStatus);

        if (jobStatus.status === 'COMPLETED' || jobStatus.status === 'FAILED') {
          this.isPolling.set(false);
          this.stopPolling$.next();
        }
      })
    ).subscribe();
  }

  downloadFile(jobId: string): void {
    window.location.href = `${this.API_URL}/download/${jobId}`;
  }

  clearJob(): void {
    this.activeJob.set(null);
    this.isPolling.set(false);
    this.stopPolling$.next();
  }
}
