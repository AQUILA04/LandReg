import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IProcessingFingerprint, NewProcessingFingerprint } from '../processing-fingerprint.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IProcessingFingerprint for edit and NewProcessingFingerprintFormGroupInput for create.
 */
type ProcessingFingerprintFormGroupInput = IProcessingFingerprint | PartialWithRequiredKeyOf<NewProcessingFingerprint>;

type ProcessingFingerprintFormDefaults = Pick<NewProcessingFingerprint, 'id'>;

type ProcessingFingerprintFormGroupContent = {
  id: FormControl<IProcessingFingerprint['id'] | NewProcessingFingerprint['id']>;
  rid: FormControl<IProcessingFingerprint['rid']>;
  handType: FormControl<IProcessingFingerprint['handType']>;
  fingerName: FormControl<IProcessingFingerprint['fingerName']>;
  fingerprintImage: FormControl<IProcessingFingerprint['fingerprintImage']>;
  fingerprintImageContentType: FormControl<IProcessingFingerprint['fingerprintImageContentType']>;
};

export type ProcessingFingerprintFormGroup = FormGroup<ProcessingFingerprintFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ProcessingFingerprintFormService {
  createProcessingFingerprintFormGroup(
    processingFingerprint: ProcessingFingerprintFormGroupInput = { id: null },
  ): ProcessingFingerprintFormGroup {
    const processingFingerprintRawValue = {
      ...this.getFormDefaults(),
      ...processingFingerprint,
    };
    return new FormGroup<ProcessingFingerprintFormGroupContent>({
      id: new FormControl(
        { value: processingFingerprintRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      rid: new FormControl(processingFingerprintRawValue.rid, {
        validators: [Validators.required],
      }),
      handType: new FormControl(processingFingerprintRawValue.handType),
      fingerName: new FormControl(processingFingerprintRawValue.fingerName),
      fingerprintImage: new FormControl(processingFingerprintRawValue.fingerprintImage),
      fingerprintImageContentType: new FormControl(processingFingerprintRawValue.fingerprintImageContentType),
    });
  }

  getProcessingFingerprint(form: ProcessingFingerprintFormGroup): IProcessingFingerprint | NewProcessingFingerprint {
    return form.getRawValue() as IProcessingFingerprint | NewProcessingFingerprint;
  }

  resetForm(form: ProcessingFingerprintFormGroup, processingFingerprint: ProcessingFingerprintFormGroupInput): void {
    const processingFingerprintRawValue = { ...this.getFormDefaults(), ...processingFingerprint };
    form.reset(
      {
        ...processingFingerprintRawValue,
        id: { value: processingFingerprintRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): ProcessingFingerprintFormDefaults {
    return {
      id: null,
    };
  }
}
