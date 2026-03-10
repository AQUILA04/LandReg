import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IFingerprintStore, NewFingerprintStore } from '../fingerprint-store.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IFingerprintStore for edit and NewFingerprintStoreFormGroupInput for create.
 */
type FingerprintStoreFormGroupInput = IFingerprintStore | PartialWithRequiredKeyOf<NewFingerprintStore>;

type FingerprintStoreFormDefaults = Pick<NewFingerprintStore, 'id'>;

type FingerprintStoreFormGroupContent = {
  id: FormControl<IFingerprintStore['id'] | NewFingerprintStore['id']>;
  rid: FormControl<IFingerprintStore['rid']>;
  handType: FormControl<IFingerprintStore['handType']>;
  fingerName: FormControl<IFingerprintStore['fingerName']>;
  fingerprintImage: FormControl<IFingerprintStore['fingerprintImage']>;
  fingerprintImageContentType: FormControl<IFingerprintStore['fingerprintImageContentType']>;
};

export type FingerprintStoreFormGroup = FormGroup<FingerprintStoreFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class FingerprintStoreFormService {
  createFingerprintStoreFormGroup(fingerprintStore: FingerprintStoreFormGroupInput = { id: null }): FingerprintStoreFormGroup {
    const fingerprintStoreRawValue = {
      ...this.getFormDefaults(),
      ...fingerprintStore,
    };
    return new FormGroup<FingerprintStoreFormGroupContent>({
      id: new FormControl(
        { value: fingerprintStoreRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      rid: new FormControl(fingerprintStoreRawValue.rid, {
        validators: [Validators.required],
      }),
      handType: new FormControl(fingerprintStoreRawValue.handType),
      fingerName: new FormControl(fingerprintStoreRawValue.fingerName),
      fingerprintImage: new FormControl(fingerprintStoreRawValue.fingerprintImage),
      fingerprintImageContentType: new FormControl(fingerprintStoreRawValue.fingerprintImageContentType),
    });
  }

  getFingerprintStore(form: FingerprintStoreFormGroup): IFingerprintStore | NewFingerprintStore {
    return form.getRawValue() as IFingerprintStore | NewFingerprintStore;
  }

  resetForm(form: FingerprintStoreFormGroup, fingerprintStore: FingerprintStoreFormGroupInput): void {
    const fingerprintStoreRawValue = { ...this.getFormDefaults(), ...fingerprintStore };
    form.reset(
      {
        ...fingerprintStoreRawValue,
        id: { value: fingerprintStoreRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): FingerprintStoreFormDefaults {
    return {
      id: null,
    };
  }
}
