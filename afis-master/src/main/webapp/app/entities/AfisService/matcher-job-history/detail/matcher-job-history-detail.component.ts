import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { IMatcherJobHistory } from '../matcher-job-history.model';

@Component({
  standalone: true,
  selector: 'jhi-matcher-job-history-detail',
  templateUrl: './matcher-job-history-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class MatcherJobHistoryDetailComponent {
  matcherJobHistory = input<IMatcherJobHistory | null>(null);

  previousState(): void {
    window.history.back();
  }
}
