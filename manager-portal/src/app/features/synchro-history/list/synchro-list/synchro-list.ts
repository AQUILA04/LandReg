import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CustomGrid, Column, PageEvent } from '../../../../shared/components/data-grid/custom-grid/custom-grid';
import { CascadeFilter } from '../../../../shared/components/cascade-filter/cascade-filter';
import { CascadeFilterStore } from '../../../../core/store/cascade-filter.store';
import { NgxPermissionsModule } from 'ngx-permissions';
import { MassExportPollerService } from '../../../../shared/services/mass-export-poller.service';
import { effect } from '@angular/core';
import { DataService } from '../../../../shared/services/data';

@Component({
  selector: 'app-synchro-list',
  standalone: true,
  imports: [CommonModule, CustomGrid, CascadeFilter, NgxPermissionsModule],
  template: `
    <div class="page-container container">
      <div class="page-header">
        <h1>Historique de Synchronisation</h1>

        <button *ngxPermissionsOnly="['EXPORT_DATA', 'SUPERADMIN']"
                class="btn-export" (click)="exportData('csv')">
          Exporter (CSV)
        </button>
      </div>

      <!-- Utilizing Cascade Filter for dates primarily -->
      <app-cascade-filter></app-cascade-filter>

      <app-custom-grid
        [columns]="columns"
        [data]="histories"
        [isLoading]="isLoading"
        [totalElements]="totalElements"
        [pageSize]="pageSize"
        [currentPage]="currentPage"
        (pageChange)="onPageChange($event)">
      </app-custom-grid>
    </div>
  `,
  styles: [`
    .page-container {
      padding-top: calc(var(--spacing-base) * 8);
      padding-bottom: calc(var(--spacing-base) * 8);
    }

    .page-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: calc(var(--spacing-base) * 6);
    }

    h1 {
      font-size: 20px;
      font-weight: 600;
    }

    .btn-export {
      padding: calc(var(--spacing-base) * 2) calc(var(--spacing-base) * 4);
      background-color: var(--color-success);
      color: white;
      border-radius: var(--radius-sm);
      font-weight: 500;
      transition: background-color var(--transition-fast);
    }

    .btn-export:hover {
      background-color: #059669;
    }
  `]
})
export class SynchroList {
  readonly filterStore = inject(CascadeFilterStore);
  readonly poller = inject(MassExportPollerService);
  private readonly dataService = inject(DataService);

  columns: Column[] = [
    { key: 'id', label: 'ID' },
    { key: 'batchNumber', label: 'Batch Number' },
    { key: 'initDate', label: 'Date Initiale' },
    { key: 'synchroStatus', label: 'Statut' },
    { key: 'operatorAgent', label: 'Agent' }
  ];

  histories: any[] = [];
  isLoading = false;
  totalElements = 0;
  pageSize = 10;
  currentPage = 0;

  constructor() {
    effect(() => {
      const filters = {
        startDate: this.filterStore.startDate(),
        endDate: this.filterStore.endDate()
      };
      this.currentPage = 0;
      this.loadData(filters);
    });
  }

  ngOnInit() {}

  loadData(currentFilters?: any) {
    this.isLoading = true;
    const filters = currentFilters || {
      startDate: this.filterStore.startDate(),
      endDate: this.filterStore.endDate()
    };

    this.dataService.getSynchroHistory(this.currentPage, this.pageSize, filters).subscribe({
      next: (response: any) => {
        this.histories = response.content || [];
        this.totalElements = response.totalElements || 0;
        this.isLoading = false;
      },
      error: (err: any) => {
        console.error('Error fetching synchro history', err);
        this.isLoading = false;
        this.histories = [];
      }
    });
  }

  onPageChange(event: PageEvent) {
    this.currentPage = event.pageIndex;
    this.pageSize = event.pageSize;
    this.loadData();
  }

  exportData(format: string) {
    this.poller.startExport({
      targetModule: 'SYNCHRO',
      format: format.toUpperCase(),
      filters: {
        startDate: this.filterStore.startDate(),
        endDate: this.filterStore.endDate()
      }
    });
  }
}
