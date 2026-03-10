import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IFingerprintStore } from '../fingerprint-store.model';
import { FingerprintStoreService } from '../service/fingerprint-store.service';

@Component({
  standalone: true,
  templateUrl: './fingerprint-store-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class FingerprintStoreDeleteDialogComponent {
  fingerprintStore?: IFingerprintStore;

  protected fingerprintStoreService = inject(FingerprintStoreService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.fingerprintStoreService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
