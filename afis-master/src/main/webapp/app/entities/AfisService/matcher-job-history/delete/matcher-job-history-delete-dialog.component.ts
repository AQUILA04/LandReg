import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IMatcherJobHistory } from '../matcher-job-history.model';
import { MatcherJobHistoryService } from '../service/matcher-job-history.service';

@Component({
  standalone: true,
  templateUrl: './matcher-job-history-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class MatcherJobHistoryDeleteDialogComponent {
  matcherJobHistory?: IMatcherJobHistory;

  protected matcherJobHistoryService = inject(MatcherJobHistoryService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.matcherJobHistoryService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
