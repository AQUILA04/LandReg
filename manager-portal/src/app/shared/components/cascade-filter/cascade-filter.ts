import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CascadeFilterStore } from '../../../core/store/cascade-filter.store';

@Component({
  selector: 'app-cascade-filter',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './cascade-filter.html',
  styleUrl: './cascade-filter.css'
})
export class CascadeFilter {
  readonly store = inject(CascadeFilterStore);

  regions = ['Maritime', 'Plateaux', 'Centrale', 'Kara', 'Savanes'];
  prefecturesMap: Record<string, string[]> = {
    'Maritime': ['Golfe', 'Lacs', 'Zio'],
    'Plateaux': ['Kloto', 'Ogou', 'Amou'],
  };
  communesMap: Record<string, string[]> = {
    'Golfe': ['Golfe 1', 'Golfe 2'],
    'Lacs': ['Lacs 1'],
  };
  cantonsMap: Record<string, string[]> = {
    'Golfe 1': ['Bè', 'Amoutivé'],
  };

  get availablePrefectures(): string[] {
    const region = this.store.region();
    return region ? (this.prefecturesMap[region] || []) : [];
  }

  get availableCommunes(): string[] {
    const prefecture = this.store.prefecture();
    return prefecture ? (this.communesMap[prefecture] || []) : [];
  }

  get availableCantons(): string[] {
    const commune = this.store.commune();
    return commune ? (this.cantonsMap[commune] || []) : [];
  }

  onRegionChange(event: Event): void {
    const value = (event.target as HTMLSelectElement).value;
    this.store.setRegion(value || null);
  }

  onPrefectureChange(event: Event): void {
    const value = (event.target as HTMLSelectElement).value;
    this.store.setPrefecture(value || null);
  }

  onCommuneChange(event: Event): void {
    const value = (event.target as HTMLSelectElement).value;
    this.store.setCommune(value || null);
  }

  onCantonChange(event: Event): void {
    const value = (event.target as HTMLSelectElement).value;
    this.store.setCanton(value || null);
  }

  onStartDateChange(event: Event): void {
    const value = (event.target as HTMLInputElement).value;
    this.store.setDateRange(value || null, this.store.endDate());
  }

  onEndDateChange(event: Event): void {
    const value = (event.target as HTMLInputElement).value;
    this.store.setDateRange(this.store.startDate(), value || null);
  }

  resetAll(): void {
    this.store.resetFilters();
  }
}
