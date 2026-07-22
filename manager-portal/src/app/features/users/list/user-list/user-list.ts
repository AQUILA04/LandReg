import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CustomGrid, Column, PageEvent } from '../../../../shared/components/data-grid/custom-grid/custom-grid';
import { UserManagementService } from '../../../../shared/services/user-management.service';

@Component({
  selector: 'app-user-list',
  standalone: true,
  imports: [CommonModule, FormsModule, CustomGrid],
  template: `
    <div class="page-container container">
      <div class="page-header">
        <h1>Gestion des Utilisateurs</h1>
      </div>

      <div class="assignment-panel">
        <h3>Assigner un Profil</h3>
        <div class="assignment-form">
            <select [(ngModel)]="selectedUserId" class="custom-select">
                <option [ngValue]="null">-- Sélectionner l'utilisateur --</option>
                @for (u of users; track u.id) {
                    <option [ngValue]="u.id">{{ u.username }} ({{ u.firstname }} {{ u.lastname }})</option>
                }
            </select>
            <select [(ngModel)]="selectedProfilId" class="custom-select">
                <option [ngValue]="null">-- Sélectionner le profil --</option>
                @for (p of profiles; track p.id) {
                    <option [ngValue]="p.id">{{ p.name }}</option>
                }
            </select>
            <button class="btn btn-primary" [disabled]="!selectedUserId || !selectedProfilId || isAssigning" (click)="assignProfile()">
                {{ isAssigning ? 'Affectation...' : 'Assigner' }}
            </button>
        </div>
      </div>

      <app-custom-grid
        [columns]="columns"
        [data]="users"
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
export class UserList {
  private readonly userService = inject(UserManagementService);

  columns: Column[] = [
    { key: 'id', label: 'ID' },
    { key: 'username', label: 'Identifiant' },
    { key: 'firstname', label: 'Prénom' },
    { key: 'lastname', label: 'Nom' },
    { key: 'email', label: 'Email' },
    { key: 'profilName', label: 'Profil' }
  ];

  users: any[] = [];
  profiles: any[] = [];
  isLoading = false;
  isAssigning = false;
  totalElements = 0;
  pageSize = 10;
  currentPage = 0;

  selectedUserId: number | null = null;
  selectedProfilId: number | null = null;

  ngOnInit() {
    this.loadData();
    this.loadProfiles();
  }

  loadProfiles() {
    this.userService.getAllProfilesList().subscribe({
      next: (res: any[]) => {
        this.profiles = res || [];
      },
      error: (err) => console.error('Failed to load profiles for assignment', err)
    });
  }

  loadData() {
    this.isLoading = true;
    this.userService.getUsers(this.currentPage, this.pageSize).subscribe({
      next: (response: any) => {
        // Map profil object to profilName string for the grid
        this.users = (response.content || []).map((u: any) => ({
          ...u,
          profilName: u.profil ? u.profil.name : 'Aucun'
        }));
        this.totalElements = response.totalElements || 0;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error fetching users', err);
        this.isLoading = false;
        this.users = [];
      }
    });
  }

  onPageChange(event: PageEvent) {
    this.currentPage = event.pageIndex;
    this.pageSize = event.pageSize;
    this.loadData();
  }

  assignProfile() {
    if (!this.selectedUserId || !this.selectedProfilId) return;

    this.isAssigning = true;
    this.userService.assignProfile(this.selectedUserId, this.selectedProfilId).subscribe({
        next: () => {
            this.isAssigning = false;
            this.selectedUserId = null;
            this.selectedProfilId = null;
            this.loadData(); // Refresh the grid
        },
        error: (err) => {
            console.error('Failed to assign profile', err);
            this.isAssigning = false;
        }
    });
  }
}
