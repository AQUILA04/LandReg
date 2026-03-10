import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../fingerprint-store.test-samples';

import { FingerprintStoreFormService } from './fingerprint-store-form.service';

describe('FingerprintStore Form Service', () => {
  let service: FingerprintStoreFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FingerprintStoreFormService);
  });

  describe('Service methods', () => {
    describe('createFingerprintStoreFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createFingerprintStoreFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            rid: expect.any(Object),
            handType: expect.any(Object),
            fingerName: expect.any(Object),
            fingerprintImage: expect.any(Object),
          }),
        );
      });

      it('passing IFingerprintStore should create a new form with FormGroup', () => {
        const formGroup = service.createFingerprintStoreFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            rid: expect.any(Object),
            handType: expect.any(Object),
            fingerName: expect.any(Object),
            fingerprintImage: expect.any(Object),
          }),
        );
      });
    });

    describe('getFingerprintStore', () => {
      it('should return NewFingerprintStore for default FingerprintStore initial value', () => {
        const formGroup = service.createFingerprintStoreFormGroup(sampleWithNewData);

        const fingerprintStore = service.getFingerprintStore(formGroup) as any;

        expect(fingerprintStore).toMatchObject(sampleWithNewData);
      });

      it('should return NewFingerprintStore for empty FingerprintStore initial value', () => {
        const formGroup = service.createFingerprintStoreFormGroup();

        const fingerprintStore = service.getFingerprintStore(formGroup) as any;

        expect(fingerprintStore).toMatchObject({});
      });

      it('should return IFingerprintStore', () => {
        const formGroup = service.createFingerprintStoreFormGroup(sampleWithRequiredData);

        const fingerprintStore = service.getFingerprintStore(formGroup) as any;

        expect(fingerprintStore).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IFingerprintStore should not enable id FormControl', () => {
        const formGroup = service.createFingerprintStoreFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewFingerprintStore should disable id FormControl', () => {
        const formGroup = service.createFingerprintStoreFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
