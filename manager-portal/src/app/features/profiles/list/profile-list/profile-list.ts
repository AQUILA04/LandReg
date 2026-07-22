import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CustomGrid, Column, PageEvent } from '../../../../shared/components/data-grid/custom-grid/custom-grid';
import { UserManagementService } from '../../../../shared/services/user-management.service';

@Component({
  selector: 'app-profile-list',
  standalone: true,
  imports: [CommonModule, FormsModule, CustomGrid],
  template: `
    <div class="page-container container">
      <div class="page-header">
        <h1>Gestion des Profils (RBAC)</h1>
      </div>

      <div class="assignment-panel">
        <h3>Ajouter une Permission à un Profil</h3>
        <div class="assignment-form">
            <select [(ngModel)]="selectedProfilId" class="custom-select">
                <option [ngValue]="null">-- Sélectionner le profil --</option>
                @for (p of profiles; track p.id) {
                    <option [ngValue]="p.id">{{ p.name }}</option>
                }
            </select>
            <select [(ngModel)]="selectedPermission" class="custom-select">
                <option [ngValue]="null">-- Sélectionner la permission --</option>
                @for (perm of permissionsList; track perm) {
                    <option [ngValue]="perm">{{ perm }}</option>
                }
            </select>
            <button class="btn btn-primary" [disabled]="!selectedProfilId || !selectedPermission || isAssigning" (click)="addPermission()">
                {{ isAssigning ? 'Ajout...' : 'Ajouter' }}
            </button>
        </div>
      </div>

      <app-custom-grid
        [columns]="columns"
        [data]="profiles"
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

    .assignment-panel {
      background-color: var(--color-bg-surface);
      border: 1px solid var(--color-border);
      border-radius: var(--radius-md);
      padding: calc(var(--spacing-base) * 4);
      margin-bottom: calc(var(--spacing-base) * 6);
    }

    .assignment-panel h3 {
      font-size: 14px;
      font-weight: 600;
      margin-bottom: calc(var(--spacing-base) * 3);
      color: var(--color-text-secondary);
    }

    .assignment-form {
      display: flex;
      gap: calc(var(--spacing-base) * 4);
      align-items: center;
    }

    .custom-select {
      flex: 1;
      padding: calc(var(--spacing-base) * 2);
      background-color: var(--color-bg-base);
      border: 1px solid var(--color-border);
      border-radius: var(--radius-sm);
      color: var(--color-text-primary);
      font-family: inherit;
      font-size: 13px;
      height: 36px;
    }

    .btn {
      padding: calc(var(--spacing-base) * 2) calc(var(--spacing-base) * 4);
      border-radius: var(--radius-sm);
      font-weight: 500;
      height: 36px;
    }

    .btn-primary {
      background-color: var(--color-primary);
      color: white;
    }

    .btn-primary:disabled {
      opacity: 0.5;
      cursor: not-allowed;
    }
  `]
})
export class ProfileList {
  private readonly userService = inject(UserManagementService);

  columns: Column[] = [
    { key: 'id', label: 'ID' },
    { key: 'name', label: 'Nom du Profil' },
    { key: 'description', label: 'Description' },
    { key: 'permissionsCount', label: 'Nb Permissions' }
  ];

  profiles: any[] = [];
  permissionsList: string[] = [];
  isLoading = false;
  isAssigning = false;
  totalElements = 0;
  pageSize = 10;
  currentPage = 0;

  selectedProfilId: number | null = null;
  selectedPermission: string | null = null;

  ngOnInit() {
    this.loadData();
    this.loadPermissions();
  }

  loadPermissions() {
    this.userService.getAllPermissions().subscribe({
      next: (res: string[]) => {
        this.permissionsList = res || [];
      },
      error: (err) => console.error('Failed to load permissions list', err)
    });
  }

  loadData() {
    this.isLoading = true;
    this.userService.getProfiles(this.currentPage, this.pageSize).subscribe({
      next: (response: any) => {
        this.profiles = (response.content || []).map((p: any) => ({
          ...p,
          permissionsCount: p.permissions ? p.permissions.length : 0
        }));
        this.totalElements = response.totalElements || 0;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error fetching profiles', err);
        this.isLoading = false;
        this.profiles = [];
      }
    });
  }

  onPageChange(event: PageEvent) {
    this.currentPage = event.pageIndex;
    this.pageSize = event.pageSize;
    this.loadData();
  }

  addPermission() {
    if (!this.selectedProfilId || !this.selectedPermission) return;

    this.isAssigning = true;
    this.userService.addPermissionsToProfile(this.selectedProfilId, [this.selectedPermission]).subscribe({
        next: () => {
            this.isAssigning = false;
            this.selectedPermission = null;
            this.loadData(); // Refresh the grid
        },
        error: (err) => {
            console.error('Failed to add permission', err);
            this.isAssigning = false;
        }
    });
  }
}
