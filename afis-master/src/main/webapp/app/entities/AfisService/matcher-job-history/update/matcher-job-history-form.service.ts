import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IMatcherJobHistory, NewMatcherJobHistory } from '../matcher-job-history.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IMatcherJobHistory for edit and NewMatcherJobHistoryFormGroupInput for create.
 */
type MatcherJobHistoryFormGroupInput = IMatcherJobHistory | PartialWithRequiredKeyOf<NewMatcherJobHistory>;

type MatcherJobHistoryFormDefaults = Pick<NewMatcherJobHistory, 'id' | 'foundMatch'>;

type MatcherJobHistoryFormGroupContent = {
  id: FormControl<IMatcherJobHistory['id'] | NewMatcherJobHistory['id']>;
  rid: FormControl<IMatcherJobHistory['rid']>;
  producerCount: FormControl<IMatcherJobHistory['producerCount']>;
  consumerReponseCount: FormControl<IMatcherJobHistory['consumerReponseCount']>;
  highScore: FormControl<IMatcherJobHistory['highScore']>;
  foundMatch: FormControl<IMatcherJobHistory['foundMatch']>;
  matchedRID: FormControl<IMatcherJobHistory['matchedRID']>;
  status: FormControl<IMatcherJobHistory['status']>;
};

export type MatcherJobHistoryFormGroup = FormGroup<MatcherJobHistoryFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class MatcherJobHistoryFormService {
  createMatcherJobHistoryFormGroup(matcherJobHistory: MatcherJobHistoryFormGroupInput = { id: null }): MatcherJobHistoryFormGroup {
    const matcherJobHistoryRawValue = {
      ...this.getFormDefaults(),
      ...matcherJobHistory,
    };
    return new FormGroup<MatcherJobHistoryFormGroupContent>({
      id: new FormControl(
        { value: matcherJobHistoryRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      rid: new FormControl(matcherJobHistoryRawValue.rid),
      producerCount: new FormControl(matcherJobHistoryRawValue.producerCount),
      consumerReponseCount: new FormControl(matcherJobHistoryRawValue.consumerReponseCount),
      highScore: new FormControl(matcherJobHistoryRawValue.highScore),
      foundMatch: new FormControl(matcherJobHistoryRawValue.foundMatch),
      matchedRID: new FormControl(matcherJobHistoryRawValue.matchedRID),
      status: new FormControl(matcherJobHistoryRawValue.status),
    });
  }

  getMatcherJobHistory(form: MatcherJobHistoryFormGroup): IMatcherJobHistory | NewMatcherJobHistory {
    return form.getRawValue() as IMatcherJobHistory | NewMatcherJobHistory;
  }

  resetForm(form: MatcherJobHistoryFormGroup, matcherJobHistory: MatcherJobHistoryFormGroupInput): void {
    const matcherJobHistoryRawValue = { ...this.getFormDefaults(), ...matcherJobHistory };
    form.reset(
      {
        ...matcherJobHistoryRawValue,
        id: { value: matcherJobHistoryRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): MatcherJobHistoryFormDefaults {
    return {
      id: null,
      foundMatch: false,
    };
  }
}
