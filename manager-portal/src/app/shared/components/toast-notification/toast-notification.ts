import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MassExportPollerService } from '../../services/mass-export-poller.service';

@Component({
  selector: 'app-toast-manager',
  standalone: true,
  imports: [CommonModule],
  template: `
    @if (exportPoller.activeJob(); as job) {
      <div class="toast-container" [ngClass]="job.status.toLowerCase()">
        <div class="toast-content">
          <div class="toast-header">
            <strong>Export: {{ job.targetModule }} ({{ job.fileFormat }})</strong>
            <button class="btn-close" (click)="dismiss()">×</button>
          </div>

          <div class="toast-body">
            @if (job.status === 'PENDING' || job.status === 'PROCESSING') {
              <div class="loading-indicator">
                <span class="spinner"></span>
                <span>Génération en cours... Veuillez patienter.</span>
              </div>
            } @else if (job.status === 'COMPLETED') {
              <div class="success-indicator">
                <span>Le fichier est prêt !</span>
                <button class="btn-download" (click)="download(job.jobId)">Télécharger</button>
              </div>
            } @else if (job.status === 'FAILED') {
              <div class="error-indicator">
                <span>Erreur: {{ job.errorMessage || 'L\\'export a échoué' }}</span>
              </div>
            }
          </div>
        </div>
      </div>
    }
  `,
  styles: [`
    .toast-container {
      position: fixed;
      bottom: calc(var(--spacing-base) * 6);
      right: calc(var(--spacing-base) * 6);
      width: 350px;
      background-color: var(--color-bg-surface);
      border: 1px solid var(--color-border);
      border-radius: var(--radius-md);
      box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05);
      z-index: 9999;
      animation: slideIn 0.3s ease-out forwards;
    }

    .toast-container.completed {
      border-left: 4px solid var(--color-success);
    }

    .toast-container.failed {
      border-left: 4px solid var(--color-danger);
    }

    .toast-container.processing, .toast-container.pending {
      border-left: 4px solid var(--color-primary);
    }

    .toast-content {
      padding: calc(var(--spacing-base) * 4);
    }

    .toast-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: calc(var(--spacing-base) * 2);
      color: var(--color-text-primary);
    }

    .toast-body {
      font-size: 13px;
      color: var(--color-text-secondary);
    }

    .btn-close {
      color: var(--color-text-muted);
      font-size: 18px;
      line-height: 1;
      padding: 0 calc(var(--spacing-base));
    }

    .btn-close:hover {
      color: var(--color-text-primary);
    }

    .loading-indicator {
      display: flex;
      align-items: center;
      gap: calc(var(--spacing-base) * 2);
    }

    .spinner {
      width: 16px;
      height: 16px;
      border: 2px solid var(--color-border);
      border-top-color: var(--color-primary);
      border-radius: 50%;
      animation: spin 1s linear infinite;
    }

    .success-indicator {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .error-indicator {
      color: var(--color-danger);
    }

    .btn-download {
      background-color: var(--color-primary);
      color: white;
      padding: calc(var(--spacing-base) * 1.5) calc(var(--spacing-base) * 3);
      border-radius: var(--radius-sm);
      font-weight: 500;
    }

    .btn-download:hover {
      background-color: var(--color-primary-hover);
    }

    @keyframes slideIn {
      from { transform: translateX(100%); opacity: 0; }
      to { transform: translateX(0); opacity: 1; }
    }

    @keyframes spin {
      to { transform: rotate(360deg); }
    }
  `]
})
export class ToastManager {
  readonly exportPoller = inject(MassExportPollerService);

  dismiss(): void {
    this.exportPoller.clearJob();
  }

  download(jobId: string): void {
    this.exportPoller.downloadFile(jobId);
    this.dismiss();
  }
}
