import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

export interface Column {
  key: string;
  label: string;
}

export interface PageEvent {
  pageIndex: number;
  pageSize: number;
}

@Component({
  selector: 'app-custom-grid',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './custom-grid.html',
  styleUrl: './custom-grid.css'
})
export class CustomGrid {
  @Input() columns: Column[] = [];
  @Input() data: any[] = [];
  @Input() isLoading: boolean = false;
  @Input() totalElements: number = 0;
  @Input() pageSize: number = 10;
  @Input() currentPage: number = 0;

  @Output() pageChange = new EventEmitter<PageEvent>();

  get totalPages(): number {
    return Math.ceil(this.totalElements / this.pageSize);
  }

  onPageChange(newPageIndex: number): void {
    if (newPageIndex >= 0 && newPageIndex < this.totalPages) {
      this.pageChange.emit({ pageIndex: newPageIndex, pageSize: this.pageSize });
    }
  }
}
