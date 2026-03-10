import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IProcessingFingerprint } from '../processing-fingerprint.model';
import { ProcessingFingerprintService } from '../service/processing-fingerprint.service';

@Component({
  standalone: true,
  templateUrl: './processing-fingerprint-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class ProcessingFingerprintDeleteDialogComponent {
  processingFingerprint?: IProcessingFingerprint;

  protected processingFingerprintService = inject(ProcessingFingerprintService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.processingFingerprintService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
