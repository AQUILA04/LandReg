import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { MatchJobStatus } from 'app/entities/enumerations/match-job-status.model';
import { IMatcherJobHistory } from '../matcher-job-history.model';
import { MatcherJobHistoryService } from '../service/matcher-job-history.service';
import { MatcherJobHistoryFormGroup, MatcherJobHistoryFormService } from './matcher-job-history-form.service';

@Component({
  standalone: true,
  selector: 'jhi-matcher-job-history-update',
  templateUrl: './matcher-job-history-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class MatcherJobHistoryUpdateComponent implements OnInit {
  isSaving = false;
  matcherJobHistory: IMatcherJobHistory | null = null;
  matchJobStatusValues = Object.keys(MatchJobStatus);

  protected matcherJobHistoryService = inject(MatcherJobHistoryService);
  protected matcherJobHistoryFormService = inject(MatcherJobHistoryFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: MatcherJobHistoryFormGroup = this.matcherJobHistoryFormService.createMatcherJobHistoryFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ matcherJobHistory }) => {
      this.matcherJobHistory = matcherJobHistory;
      if (matcherJobHistory) {
        this.updateForm(matcherJobHistory);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const matcherJobHistory = this.matcherJobHistoryFormService.getMatcherJobHistory(this.editForm);
    if (matcherJobHistory.id !== null) {
      this.subscribeToSaveResponse(this.matcherJobHistoryService.update(matcherJobHistory));
    } else {
      this.subscribeToSaveResponse(this.matcherJobHistoryService.create(matcherJobHistory));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IMatcherJobHistory>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(matcherJobHistory: IMatcherJobHistory): void {
    this.matcherJobHistory = matcherJobHistory;
    this.matcherJobHistoryFormService.resetForm(this.editForm, matcherJobHistory);
  }
}
