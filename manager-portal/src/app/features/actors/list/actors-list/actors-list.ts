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
  selector: 'app-actors-list',
  standalone: true,
  imports: [CommonModule, CustomGrid, CascadeFilter, NgxPermissionsModule],
  template: `
    <div class="page-container container">
      <div class="page-header">
        <h1>Annuaire des Acteurs</h1>

        <button *ngxPermissionsOnly="['EXPORT_DATA', 'SUPERADMIN']"
                class="btn-export" (click)="exportData('csv')">
          Exporter (CSV)
        </button>
      </div>

      <app-cascade-filter></app-cascade-filter>

      <app-custom-grid
        [columns]="columns"
        [data]="actors"
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
      background-color: #059669; /* darker emerald */
    }
  `]
})
export class ActorsList {
  readonly filterStore = inject(CascadeFilterStore);
  readonly poller = inject(MassExportPollerService);
  private readonly dataService = inject(DataService);

  columns: Column[] = [
    { key: 'id', label: 'ID' },
    { key: 'uin', label: 'UIN' },
    { key: 'name', label: 'Nom/Raison Sociale' },
    { key: 'type', label: 'Type' },
    { key: 'role', label: 'Rôle' },
    { key: 'status', label: 'Statut' }
  ];

  actors: any[] = [];
  isLoading = false;
  totalElements = 0;
  pageSize = 10;
  currentPage = 0;

  constructor() {
    // Re-fetch data whenever filters change using Signal effect
    effect(() => {
      const filters = {
        startDate: this.filterStore.startDate(),
        endDate: this.filterStore.endDate()
      };
      this.currentPage = 0; // Reset pagination on filter change
      this.loadData(filters);
    });
  }

  ngOnInit() {
    // Initial load handled by effect automatically
  }

  loadData(currentFilters?: any) {
    this.isLoading = true;
    const filters = currentFilters || {
      startDate: this.filterStore.startDate(),
      endDate: this.filterStore.endDate()
    };

    this.dataService.getActors(this.currentPage, this.pageSize, filters).subscribe({
      next: (response: any) => {
        this.actors = response.content || [];
        this.totalElements = response.totalElements || 0;
        this.isLoading = false;
      },
      error: (err: any) => {
        console.error('Error fetching actors', err);
        this.isLoading = false;
        this.actors = [];
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
      targetModule: 'ACTORS',
      format: format.toUpperCase(),
      filters: {
        startDate: this.filterStore.startDate(),
        endDate: this.filterStore.endDate()
      }
    });
  }
}
