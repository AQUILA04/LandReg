import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../processing-fingerprint.test-samples';

import { ProcessingFingerprintFormService } from './processing-fingerprint-form.service';

describe('ProcessingFingerprint Form Service', () => {
  let service: ProcessingFingerprintFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ProcessingFingerprintFormService);
  });

  describe('Service methods', () => {
    describe('createProcessingFingerprintFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createProcessingFingerprintFormGroup();

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

      it('passing IProcessingFingerprint should create a new form with FormGroup', () => {
        const formGroup = service.createProcessingFingerprintFormGroup(sampleWithRequiredData);

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

    describe('getProcessingFingerprint', () => {
      it('should return NewProcessingFingerprint for default ProcessingFingerprint initial value', () => {
        const formGroup = service.createProcessingFingerprintFormGroup(sampleWithNewData);

        const processingFingerprint = service.getProcessingFingerprint(formGroup) as any;

        expect(processingFingerprint).toMatchObject(sampleWithNewData);
      });

      it('should return NewProcessingFingerprint for empty ProcessingFingerprint initial value', () => {
        const formGroup = service.createProcessingFingerprintFormGroup();

        const processingFingerprint = service.getProcessingFingerprint(formGroup) as any;

        expect(processingFingerprint).toMatchObject({});
      });

      it('should return IProcessingFingerprint', () => {
        const formGroup = service.createProcessingFingerprintFormGroup(sampleWithRequiredData);

        const processingFingerprint = service.getProcessingFingerprint(formGroup) as any;

        expect(processingFingerprint).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IProcessingFingerprint should not enable id FormControl', () => {
        const formGroup = service.createProcessingFingerprintFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewProcessingFingerprint should disable id FormControl', () => {
        const formGroup = service.createProcessingFingerprintFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
